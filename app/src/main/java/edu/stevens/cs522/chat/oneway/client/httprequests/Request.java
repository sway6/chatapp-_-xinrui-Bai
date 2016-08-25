package edu.stevens.cs522.chat.oneway.client.httprequests;

import android.net.Uri;
import android.os.Parcelable;
import android.util.JsonReader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

import edu.stevens.cs522.chat.oneway.client.httpresponses.HttpResponse;

/**
 * Created by baixinrui on 3/8/16.
 */
public abstract class Request implements Parcelable
{
    // App-specific HTTP request headers.
    public abstract Map<String,String> getRequestHeaders();

    // Chat service URI with parameters e.g. query string parameters.
    public abstract Uri getRequestUri();

    // JSON body (if not null) for request data not passed in headers.
    public abstract  String getRequestEntity() throws IOException;

    // Define your own Response class, including HTTP response code.
    public abstract HttpResponse getResponse(HttpURLConnection connection, JsonReader rd) throws IOException;

}
