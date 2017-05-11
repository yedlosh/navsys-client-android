package cz.iim.navsysclient;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cz.iim.navsysclient.entities.Destination;

import static cz.iim.navsysclient.internal.Utils.showLocationServicePromptIfNeeded;


public class NavigationActivity extends AppCompatActivity {

    final private int REQUEST_CODE_ASK_PERMISSIONS = 87654;

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
        getSupportActionBar().setTitle("Navigation");

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        Destination destination = intent.getParcelableExtra(MainActivity.EXTRA_DESTINATION);

        // Capture the layout's TextView and set the string as its text
        TextView textView = (TextView) findViewById(R.id.destination_textView);
        textView.setText(destination.getName());
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //TODO permission granted
                } else {
                    // Permission Denied
                    finish();
                }
            }
        }
    }

    public void cancelNavigation(View view) {
        finish();
    }
}
