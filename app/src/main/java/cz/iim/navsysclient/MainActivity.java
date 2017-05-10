package cz.iim.navsysclient;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import api.NavsysAPI;
import api.ResponseParser;
import entities.Destination;

import static cz.iim.navsysclient.Utils.requestRuntimePermissions;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private NavsysAPI client;
    private ViewGroup rootView;
    private DestinationAdapter destinationsAdapter;
    private List<Destination> destinationsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootView = (ViewGroup) findViewById(R.id.main_root_view);

        requestRuntimePermissions(this, rootView);

        ListView destinationListView = (ListView)findViewById(R.id.destination_list_view);

        destinationsAdapter = new DestinationAdapter(destinationsList, this);

        destinationListView.setAdapter(destinationsAdapter);

        Callback getDestinationsCallback = new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, "Failed request: " + request, e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String body = response.body().string();
                Log.d(TAG, body);
                destinationsList = ResponseParser.parseDestinationsResponse(body);
                destinationsAdapter.notifyDataSetChanged();
            }
        };
        client.getDestinations(getDestinationsCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
