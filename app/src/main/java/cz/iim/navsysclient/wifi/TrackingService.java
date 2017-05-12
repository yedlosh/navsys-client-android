package cz.iim.navsysclient.wifi;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import com.google.android.gms.iid.InstanceID;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import cz.iim.navsysclient.api.NavsysAPI;
import cz.iim.navsysclient.api.NavsysAPIImpl;
import cz.iim.navsysclient.api.RequestParser;
import cz.iim.navsysclient.api.ResponseParser;
import cz.iim.navsysclient.entities.Location;
import cz.iim.navsysclient.internal.Constants;


public class TrackingService extends IntentService {
    private static final String TAG = TrackingService.class.getSimpleName();
    public static final String TRACKING_LOCATION_BROADCAST = TAG + ".LOCATION_BROADCAST";
    public static final String TRACKING_LOCATION_EXTRA = TAG + ".LOCATION_EXTRA";
    public static final String STOP_TRACKING_INTENT = TAG + ".STOP_TRACKING";

    private Intent wakefulIntent;

    private WifiScanCompleteReceiver scanReceiver;
    private WifiManager wifiManager;

    private Handler handler;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public TrackingService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean startTracking = intent.getBooleanExtra(TrackingManager.TRACKING_START_EXTRA, false);
        // IF start setup WiFiScanComplete receiver and start running timer to startScans
        Log.d(TAG, "Start tracking:" + startTracking);
        if(startTracking){
            wakefulIntent = intent;
            startTracking();
        } else {
            TrackingManager.completeWakefulIntent(intent);
        }
    }

    private Runnable runTracking = new Runnable() {

        @Override
        public void run() {
            Log.d(TAG, "Starting scan...");
            wifiManager.startScan();

            handler.postDelayed(runTracking, Constants.TRACKING_INTERVAL);
        }
    };

    private void startTracking() {
        //Make sure WiFi is running
        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);

        handler = new Handler();

        IntentFilter intentFilter = new IntentFilter( );
        intentFilter.addAction( WifiManager.SCAN_RESULTS_AVAILABLE_ACTION );
        scanReceiver =  new WifiScanCompleteReceiver();
        getApplicationContext().registerReceiver(scanReceiver,intentFilter);

        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(stopTrackingReceiver, new IntentFilter(STOP_TRACKING_INTENT));

        //Start polling scan
        handler.post(runTracking);
    }

    private void stopTracking() {
        Log.d(TAG, "Stopping tracking, releasing wake lock");
        handler.removeCallbacks(runTracking);
        getApplicationContext().unregisterReceiver(scanReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(stopTrackingReceiver);

        TrackingManager.completeWakefulIntent(wakefulIntent);
    }

    // Getting the CurrentLocation from the received braodcast
    private BroadcastReceiver stopTrackingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopTracking();
        }
    };

    private class WifiScanCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive( Context context, Intent intent ) {
            Log.d(TAG, "Got new WiFi Results");
            List<ScanResult> scanResultList = wifiManager.getScanResults();
            NavsysAPI client = NavsysAPIImpl.getInstance();

            String username = InstanceID.getInstance(context).getId();
            Log.d(TAG, "InstanceID:" + username);
            JSONObject trackRequest = RequestParser.parseTrackRequest(scanResultList, username, System.currentTimeMillis()/1000);

            client.track(getTrackCallback(), trackRequest);
        }

        private Callback getTrackCallback() {
            return new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.e(TAG, "Failed request: " + request, e);
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String body = response.body().string();
                    Log.d(TAG, "Got track response: " + body);
                    Location location = ResponseParser.parseTrackResponse(body);

                    // TODO sent via local intent to NavActivity
                    if(location != null) {
                        Intent intent = new Intent(TRACKING_LOCATION_BROADCAST);
                        intent.putExtra(TRACKING_LOCATION_EXTRA, location);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    }
                }
            };
        }
    }
}
