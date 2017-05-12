package cz.iim.navsysclient.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
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


public class TrackingService {
    private static final String TAG = TrackingService.class.getSimpleName();
    private static final String WAKE_LOCK_TAG = TAG + ".WAKE_LOCK_TAG";
    public static final String TRACKING_LOCATION_BROADCAST = TAG + ".LOCATION_BROADCAST";
    public static final String TRACKING_LOCATION_EXTRA = TAG + ".LOCATION_EXTRA";

    private Context context;
    private PowerManager.WakeLock wakeLock;
    private WifiScanCompleteReceiver scanReceiver;
    private WifiManager wifiManager;

    private Handler handler;

    private boolean isTracking = false;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public TrackingService(Context context) {
        this.context = context;
    }

    public boolean isTracking() {
        return isTracking;
    }

    private Runnable runTracking = new Runnable() {

        @Override
        public void run() {
            Log.d(TAG, "Starting scan...");
            wifiManager.startScan();

            handler.postDelayed(runTracking, Constants.TRACKING_INTERVAL);
        }
    };

    public void startTracking() {
        Log.d(TAG, "Starting tracking, acquiring wake lock");
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG);
        wakeLock.acquire();

        //Make sure WiFi is running
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        //Looper.prepare();
        handler = new Handler();

        IntentFilter intentFilter = new IntentFilter( );
        intentFilter.addAction( WifiManager.SCAN_RESULTS_AVAILABLE_ACTION );
        scanReceiver =  new WifiScanCompleteReceiver();
        context.getApplicationContext().registerReceiver(scanReceiver,intentFilter);

        //Start polling scan
        handler.post(runTracking);
        isTracking = true;
    }

    public void stopTracking() {
        Log.d(TAG, "Stopping tracking, releasing wake lock");
        handler.removeCallbacks(runTracking);
        context.getApplicationContext().unregisterReceiver(scanReceiver);
        isTracking = false;
        wakeLock.release();
    }

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
                        LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(intent);
                    }
                }
            };
        }
    }
}
