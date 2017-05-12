package cz.iim.navsysclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.iim.navsysclient.api.NavsysAPI;
import cz.iim.navsysclient.api.NavsysAPIImpl;
import cz.iim.navsysclient.api.ResponseParser;
import cz.iim.navsysclient.entities.Location;

import static cz.iim.navsysclient.internal.Utils.requestRuntimePermissions;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String EXTRA_DESTINATION = MainActivity.class.getSimpleName() + ".DESTINATION";

    private NavsysAPI client = NavsysAPIImpl.getInstance();
    private ViewGroup rootView;
    private LocationAdapter destinationsAdapter;
    private List<Location> destinationsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootView = (ViewGroup) findViewById(R.id.main_root_view);

        // Handle permissions
        requestRuntimePermissions(this, rootView);

        // Setup Destinations ListView
        ListView destinationListView = (ListView) findViewById(R.id.destination_list_view);
        destinationsAdapter = new LocationAdapter(destinationsList, this);
        destinationListView.setAdapter(destinationsAdapter);
        registerListViewClickListener(destinationListView);

        // Populate Destinations ListView
        client.getDestinations(getDestinationsCallback());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void startNavigation(Location location) {
        Intent intent = new Intent(this, NavigationActivity.class);
        intent.putExtra(EXTRA_DESTINATION, location);
        startActivity(intent);
    }

    private void registerListViewClickListener(ListView listView) {
        // React to user clicks on item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
                startNavigation(destinationsAdapter.getItem(position));
            }
        });
    }

    private Callback getDestinationsCallback() {
        return new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, "Failed request: " + request, e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String body = response.body().string();
                Log.d(TAG, body);
                destinationsList = ResponseParser.parseDestinationsResponse(body);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        destinationsAdapter.setLocationList(destinationsList);
                        destinationsAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
    }
}
