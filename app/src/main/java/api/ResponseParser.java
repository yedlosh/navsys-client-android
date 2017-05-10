package api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.iim.navsysclient.Constants;
import entities.Destination;


public class ResponseParser {

    private ResponseParser(){}

    public static List<Destination> parseDestinationsResponse(String responseBody) {
        List<Destination> destinations = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(responseBody);
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject destinationJSON = jsonArray.getJSONObject(i);
                String id = destinationJSON.getString(Constants.NAVSYS_DESTINATION_ID_KEY);
                String name = destinationJSON.getString(Constants.NAVSYS_DESTINATION_ID_NAME);

                destinations.add(new Destination(id,name));
            }
            return destinations;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
