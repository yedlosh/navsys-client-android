package cz.iim.navsysclient;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootView = (ViewGroup) findViewById(R.id.main_root_view);

        // Handle permissions
        requestRuntimePermissions(this, rootView);

        // Set ActionBar title
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(R.string.title_pick_a_destination);
        }

        // Setup SwipeRefreshLayout which is wrapped around the destinationsList ListView
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(TAG, "onRefresh called from SwipeRefreshLayout");

                        client.getDestinations(getDestinationsCallback());
                    }
                }
        );

        // Show list as refreshing upon startup
        swipeRefreshLayout.setRefreshing(true);

        // Setup Destinations ListView
        ListView destinationListView = (ListView) findViewById(R.id.destination_list_view);
        destinationsAdapter = new LocationAdapter(destinationsList, this);
        destinationListView.setAdapter(destinationsAdapter);
        registerListViewClickListener(destinationListView);

        // Set empty view for Destinations ListView
        TextView emptyTextView = (TextView) findViewById(R.id.empty_list_item);
        destinationListView.setEmptyView(emptyTextView);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        wifiManager.startScan();

        // Populate Destinations ListView
        client.getDestinations(getDestinationsCallback());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

/*
 * Listen for option item selections so that we receive a notification
 * when the user requests a refresh by selecting the refresh action bar item.
 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Check if user triggered a refresh:
            case R.id.menu_refresh:
                Log.i(TAG, "Refresh menu item selected");

                // Signal SwipeRefreshLayout to start the progress indicator
                swipeRefreshLayout.setRefreshing(true);
                client.getDestinations(getDestinationsCallback());

                return true;
        }

        // User didn't trigger a refresh, let the superclass handle this action
        return super.onOptionsItemSelected(item);

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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String body = response.body().string();
                Log.d(TAG, body);
                destinationsList = ResponseParser.parseDestinationsResponse(body);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(destinationsList != null) {
                            destinationsAdapter.setLocationList(destinationsList);
                            destinationsAdapter.notifyDataSetChanged();
                        }
                        if(swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
            }
        };
    }
}
