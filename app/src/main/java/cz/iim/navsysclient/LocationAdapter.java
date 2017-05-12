package cz.iim.navsysclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cz.iim.navsysclient.entities.Location;

public class LocationAdapter extends ArrayAdapter<Location> {
    private static final String TAG = LocationAdapter.class.getSimpleName();

    private List<Location> locationList;
    private Context context;

    public LocationAdapter(List<Location> locationList, Context ctx) {
        super(ctx, android.R.layout.simple_list_item_1, locationList);
        this.locationList = locationList;
        this.context = ctx;
    }

    public int getCount() {
        return locationList.size();
    }

    public Location getItem(int position) {
        return locationList.get(position);
    }

    public long getItemId(int position) {
        return locationList.get(position).hashCode();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        LocationHolder holder = new LocationHolder();

        // First let's verify the convertView is not null
        if (convertView == null) {
            // This a new view we inflate the new layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(android.R.layout.simple_list_item_1, null);
            // Now we can fill the layout with the right values
            TextView tv = (TextView) view.findViewById(android.R.id.text1);
            //TextView distView = (TextView) v.findViewById(R.id.dist);


            holder.locationNameView = tv;
            //holder.distView = distView;

            view.setTag(holder);
        } else
            holder = (LocationHolder) view.getTag();

        Location location = locationList.get(position);
        holder.locationNameView.setText(location.getName());
        //holder.locationNameView.setText("" + p.getDistance());
        return view;
    }

    public void setLocationList(List<Location> locationList) {
        this.locationList = locationList;
    }

    /* *********************************
     * We use the holder pattern
	 * It makes the view faster and avoid finding the component
	 * **********************************/

    private static class LocationHolder {
        public TextView locationNameView;
        //public TextView distView;
    }
}
