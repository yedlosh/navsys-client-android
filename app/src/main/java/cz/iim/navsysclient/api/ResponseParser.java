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
            JSONObject json = new JSONObject(responseBody);
            boolean success = json.getBoolean("success");

            if(success) {
                JSONArray jsonArray = json.getJSONArray("payload");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject destinationJSON = jsonArray.getJSONObject(i);
                    String id = destinationJSON.getString(Constants.NAVSYS_LOCATION_ID_KEY);
                    String name = destinationJSON.getString(Constants.NAVSYS_LOCATION_ID_NAME);

                    locations.add(new Location(id, name));
                }
                return locations;
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Location parseTrackResponse(String responseBody) {
        try {
            JSONObject json = new JSONObject(responseBody);
            boolean success = json.getBoolean("success");
            if(success) {
                String id = json.getString("location");
                String name = json.getString("name");

                return new Location(id, name);
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static RegisterResponse parseRegisterResponse(String responseBody) {
        try {
            JSONObject json = new JSONObject(responseBody);
            boolean success = json.getBoolean("success");
            if(success) {
                String locationId = json.getString("location");
                String name = json.getString("name");
                String color = json.getString("color");
                return new RegisterResponse(new Location(locationId, name), color);
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
