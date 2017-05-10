package api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONObject;

import cz.iim.navsysclient.Constants;

public class NavsysAPIImpl implements NavsysAPI {
    private static final String TAG = NavsysAPIImpl.class.getSimpleName();
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    private final Context context;
    private final OkHttpClient httpClient;

    private enum METHOD { POST, GET, PUT, DELETE };

    // Constructor
    public NavsysAPIImpl(Context context) {
        this.context = context;
        this.httpClient = new OkHttpClient();
    }

    @Override
    public void track(Callback callback, JSONObject requestBody) {
        new RequestTask(Constants.NAVSYS_API_TRACK, METHOD.POST, requestBody.toString(), callback).execute();
    }

    @Override
    public void register(Callback callback, JSONObject requestBody) {
        new RequestTask(Constants.NAVSYS_API_REGISTER, METHOD.POST, requestBody.toString(), callback).execute();
    }

    @Override
    public void cancel(Callback callback, JSONObject requestBody) {
        new RequestTask(Constants.NAVSYS_API_CANCEL, METHOD.POST, requestBody.toString(), callback).execute();
    }

    @Override
    public void getDestinations(Callback callback) {
        new RequestTask(Constants.NAVSYS_API_DESTINATIONS, METHOD.GET, callback).execute();
    }

    private class RequestTask extends AsyncTask<Void, Void, Void> {
        private final String path;
        private final METHOD method;
        private final String json;
        private final Callback callback;

        RequestTask(String endpoint, METHOD method, Callback callback) {
            this.path = endpoint;
            this.method = method;
            this.json = null;
            this.callback = callback;
        }

        RequestTask(String endpoint, METHOD method, String json, Callback callback) {
            this.path = endpoint;
            this.method = method;
            this.json = json;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Request.Builder requestBuilder = new Request.Builder()
                        .url(Constants.NAVSYS_API_ADDR + path);
                switch (method) {
                    case PUT:
                        requestBuilder.put(RequestBody.create(MEDIA_TYPE_JSON, json));
                        break;
                    case POST:
                        requestBuilder.post(RequestBody.create(MEDIA_TYPE_JSON, json));
                        break;
                    case DELETE:
                        requestBuilder.delete(RequestBody.create(MEDIA_TYPE_JSON, json));
                        break;
                    default: break;
                }
                Request request = requestBuilder.build();
                httpClient.newCall(request).enqueue(callback);
            } catch (Exception e) {
                Log.e(TAG, "IOException", e);
            }
            return null;
        }
    }
}
