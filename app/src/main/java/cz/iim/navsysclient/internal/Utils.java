package cz.iim.navsysclient.internal;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.ViewGroup;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener;

import cz.iim.navsysclient.R;

public class Utils {

    public static void requestRuntimePermissions(Activity activity, ViewGroup rootView) {
        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(SnackbarOnDeniedPermissionListener.Builder
                        .with(rootView, R.string.location_permission_rationale)
                        .withOpenSettingsButton(R.string.settings)
                        .build())
                .check();
    }

    // Checking Location service status
    public static boolean isLocationAvailable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        return (gps_enabled || network_enabled);
    }

    public static void showLocationServicePromptIfNeeded(final Activity activity) {
        if (!isLocationAvailable(activity)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            dialog.setMessage("Location service is not enabled. You have to turn on location services for Navsys Client to work properly");
            dialog.setPositiveButton("Enable Locations service", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    activity.startActivity(myIntent);
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Toast.makeText(activity, "Navsys Client can't work without location services", Toast.LENGTH_LONG).show();
                    activity.finish();
                }
            });
            dialog.show();
        }
    }
}
