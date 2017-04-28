package edu.stevens.cs522.chat.oneway.client.httpresponses;

import android.os.Parcel;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Created by baixinrui on 3/18/16.
 */
public class StreamingResponse
{
    public HttpURLConnection connection;

    public HttpResponse response;

    public StreamingResponse(HttpURLConnection connection , HttpResponse response)
    {
        this.connection = connection;
        this.response = response;
    }

    public InputStream getInputStream() throws IOException
    {
        return connection.getInputStream();
    }

    public void disconnect()
    {
        connection.disconnect();
    }
}
