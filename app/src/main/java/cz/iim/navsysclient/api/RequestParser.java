package cz.iim.navsysclient.api;

import android.net.wifi.ScanResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.iim.navsysclient.entities.Location;

public class RequestParser {

    public static JSONObject parseTrackRequest(List<ScanResult> scanResults, String username, Long time) {
        try {
            JSONObject trackRequest = new JSONObject();
            JSONArray resultsArray = new JSONArray();

            for (ScanResult result : scanResults) {
                JSONObject ap = new JSONObject();
                ap.put("mac", result.BSSID);
                ap.put("rssi", result.level);
                resultsArray.put(ap);
            }

            trackRequest.put("wifi-fingerprint", resultsArray);
            trackRequest.put("username", username);
            trackRequest.put("time", time);

            return trackRequest;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject parseRegisterRequest(String username, Location destination, List<ScanResult> scanResults, Long time) {
        try {
            JSONObject trackRequest = new JSONObject();
            JSONArray resultsArray = new JSONArray();

            for (ScanResult result : scanResults) {
                JSONObject ap = new JSONObject();
                ap.put("mac", result.BSSID);
                ap.put("rssi", result.level);
                resultsArray.put(ap);
            }

            trackRequest.put("wifi-fingerprint", resultsArray);
            trackRequest.put("username", username);
            trackRequest.put("destination", destination.getId());
            trackRequest.put("time", time);

            return trackRequest;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
