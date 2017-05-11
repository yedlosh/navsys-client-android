package cz.iim.navsysclient.api;

import android.os.Handler;
import android.os.Looper;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class RequestCallback implements Callback {

    // private variables
    private final com.squareup.okhttp.Callback delegate;
    private final Handler handler;

    //Constructor
    public RequestCallback(com.squareup.okhttp.Callback delegate) {
        this.delegate = delegate;
        this.handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onFailure(final Request request, final IOException e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                delegate.onFailure(request, e);
            }
        });
    }

    @Override
    public void onResponse(final Response response) throws IOException {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    delegate.onResponse(response);
                } catch (IOException e) {
                    delegate.onFailure(null, e);
                }
            }
        });
    }
}
