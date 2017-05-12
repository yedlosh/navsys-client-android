package cz.iim.navsysclient.api;

import com.squareup.okhttp.Callback;

import org.json.JSONObject;

public interface NavsysAPI {

    void track(Callback callback, JSONObject requestBody);

    void register(Callback callback, JSONObject requestBody);

    void cancel(Callback callback, JSONObject requestBody);

    void getDestinations(Callback callback);
}
