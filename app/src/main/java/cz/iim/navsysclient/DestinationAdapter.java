package cz.iim.navsysclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cz.iim.navsysclient.entities.Destination;

public class DestinationAdapter extends ArrayAdapter<Destination> {
    private static final String TAG = DestinationAdapter.class.getSimpleName();

    private List<Destination> destinationList;
    private Context context;

    public DestinationAdapter(List<Destination> destinationList, Context ctx) {
        super(ctx, android.R.layout.simple_list_item_1, destinationList);
        this.destinationList = destinationList;
        this.context = ctx;
    }

    public int getCount() {
        return destinationList.size();
    }

    public Destination getItem(int position) {
        return destinationList.get(position);
    }

    public long getItemId(int position) {
        return destinationList.get(position).hashCode();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        DestinationHolder holder = new DestinationHolder();

        // First let's verify the convertView is not null
        if (convertView == null) {
            // This a new view we inflate the new layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(android.R.layout.simple_list_item_1, null);
            // Now we can fill the layout with the right values
            TextView tv = (TextView) view.findViewById(android.R.id.text1);
            //TextView distView = (TextView) v.findViewById(R.id.dist);


            holder.destinationNameView = tv;
            //holder.distView = distView;

            view.setTag(holder);
        } else
            holder = (DestinationHolder) view.getTag();

        Destination destination = destinationList.get(position);
        holder.destinationNameView.setText(destination.getName());
        //holder.destinationNameView.setText("" + p.getDistance());
        return view;
    }

    public void setDestinationList(List<Destination> destinationList) {
        this.destinationList = destinationList;
    }

    /* *********************************
     * We use the holder pattern
	 * It makes the view faster and avoid finding the component
	 * **********************************/

    private static class DestinationHolder {
        public TextView destinationNameView;
        //public TextView distView;
    }
}
