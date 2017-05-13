package cz.iim.navsysclient;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.iid.InstanceID;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import cz.iim.navsysclient.api.NavsysAPI;
import cz.iim.navsysclient.api.NavsysAPIImpl;
import cz.iim.navsysclient.api.RegisterResponse;
import cz.iim.navsysclient.api.RequestParser;
import cz.iim.navsysclient.api.ResponseParser;
import cz.iim.navsysclient.entities.Location;
import cz.iim.navsysclient.internal.Utils;
import cz.iim.navsysclient.views.AssignedColorView;
import cz.iim.navsysclient.services.TrackingService;

import static cz.iim.navsysclient.internal.Utils.showLocationServicePromptIfNeeded;


public class NavigationActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String START_TRACKING_INTENT = TAG + ".TRACKING";

    final private int REQUEST_CODE_ASK_PERMISSIONS = 87654;

    private TrackingService trackingService;
    private Location destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        showLocationServicePromptIfNeeded(this);

//        ActionBar actionBar = getActionBar();
//        if (actionBar != null) {
//            actionBar.setHomeButtonEnabled(false); // disable the button
//            actionBar.setDisplayHomeAsUpEnabled(false); // remove the left caret
//            actionBar.setDisplayShowHomeEnabled(false); // remove the icon
//        }

        // Set ActionBar title
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(R.string.title_navigation);
        }

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        destination = intent.getParcelableExtra(MainActivity.EXTRA_DESTINATION);

        // Capture the layout's TextView and set the string as its text
        TextView textView = (TextView) findViewById(R.id.destination_textView);
        textView.setText(destination.getName());

        trackingService = new TrackingService(this);
        //registerReceiver(trackingManager,new IntentFilter(START_TRACKING_INTENT));

        // Listener to the broadcast message from WifiIntent
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(locationReceiver,
                new IntentFilter(TrackingService.TRACKING_LOCATION_BROADCAST));

        startNavigation();
    }

    private void pollTracker() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // TODO show rationale
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
        } else {
            //TODO poll track

        }
    }


    // Getting the CurrentLocation from the received braodcast
    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location newLocation = intent.getParcelableExtra(TrackingService.TRACKING_LOCATION_EXTRA);
            if(newLocation != null) {
                Log.d(TAG, "Got Location from TrackingService: " + newLocation.getName());
                // Capture the layout's TextView and set the string as its text
                TextView textView = (TextView) findViewById(R.id.location_textView);
                textView.setText(newLocation.getName());
            } else {
                Log.w(TAG, "Got location intent without Location!");
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish();
                }
            }
        }
    }

    public void startNavigation() {
        NavsysAPI client = NavsysAPIImpl.getInstance();

        String username = InstanceID.getInstance(this).getId();
        Log.d(TAG, "InstanceID:" + username);
        List<ScanResult> latestScan = Utils.getLatestWifiScanResults(this);
        JSONObject request = RequestParser.parseRegisterRequest(username, destination, latestScan, System.currentTimeMillis()/1000);

        client.register(getRegisterCallback(), request);
    }

    private Callback getRegisterCallback() {
        return new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, "Navsys API: Failed register request: " + request, e);
                stopNavigation();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String body = response.body().string();
                Log.d(TAG, body);

                final RegisterResponse registerResponse = ResponseParser.parseRegisterResponse(body);
                if(registerResponse != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView textView = (TextView) findViewById(R.id.location_textView);
                            textView.setText(registerResponse.getLocation().getName());

                            AssignedColorView colorView = (AssignedColorView) findViewById(R.id.assigned_color_view);
                            colorView.setAssignedColor(Color.parseColor(registerResponse.getAssignedColor()));
                            trackingService.startTracking();
                        }
                    });
                    // Start tracking service
//                    Intent intent = new Intent(START_TRACKING_INTENT);
//                    sendBroadcast(intent);

                } else {
                    stopNavigation();
                }
            }
        };
    }

    public void cancelNavigation(View view) {
        NavsysAPI client = NavsysAPIImpl.getInstance();

        JSONObject request = RequestParser.parseCancelRequest(InstanceID.getInstance(this).getId(), System.currentTimeMillis()/1000);
        client.cancel(getCancelCallback(), request);

        stopNavigation();
    }

    public void stopNavigation() {

        //Intent intent = new Intent(TrackingService.STOP_TRACKING_INTENT);
        //LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        finish();
    }

    private Callback getCancelCallback() {
        return new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, "Navsys API: Failed cancel request" + request, e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String body = response.body().string();
                Log.d(TAG, "Navsys API: Navigation Cancelled");
            }
        };
    }

    @Override
    public void onDestroy() {
        if(trackingService.isTracking()){
            trackingService.stopTracking();
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(locationReceiver);
        super.onDestroy();
    }
}
