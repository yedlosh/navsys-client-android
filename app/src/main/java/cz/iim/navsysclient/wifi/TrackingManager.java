package cz.iim.navsysclient.wifi;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class TrackingManager extends WakefulBroadcastReceiver {
    private static final String TAG = TrackingManager.class.getSimpleName();
    public static final String TRACKING_START_EXTRA = TAG + ".TRACKING";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Starting Tracking Service");
        Intent trackingService = new Intent(context, TrackingService.class);
        trackingService.putExtra(TRACKING_START_EXTRA, true);
        startWakefulService(context, trackingService);
    }
}
