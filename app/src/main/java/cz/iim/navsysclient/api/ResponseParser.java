package cz.iim.navsysclient.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.iim.navsysclient.internal.Constants;
import cz.iim.navsysclient.entities.Location;


public class ResponseParser {

    private ResponseParser() {
    }

    public static List<Location> parseDestinationsResponse(String responseBody) {
        List<Location> locations = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(responseBody);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject destinationJSON = jsonArray.getJSONObject(i);
                String id = destinationJSON.getString(Constants.NAVSYS_LOCATION_ID_KEY);
                String name = destinationJSON.getString(Constants.NAVSYS_LOCATION_ID_NAME);

                locations.add(new Location(id, name));
            }
            return locations;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Location parseTrackResponse(String responseBody) {
        try {
            JSONObject json = new JSONObject(responseBody);
            String id = json.getString("location");
            String name = json.getString("name");

            return new Location(id, name);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static RegisterResponse parseRegisterResponse(String responseBody) {
        try {
            JSONObject json = new JSONObject(responseBody);
            String locationId = json.getString("location");
            String name = json.getString("name");
            String color = json.getString("color");
            return new RegisterResponse(new Location(locationId, name), color);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}