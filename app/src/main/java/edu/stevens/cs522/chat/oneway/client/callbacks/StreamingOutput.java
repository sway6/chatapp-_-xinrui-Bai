package edu.stevens.cs522.chat.oneway.client.callbacks;

import java.io.IOException;
import java.net.HttpURLConnection;

import edu.stevens.cs522.chat.oneway.client.httprequests.SyncRequest;

/**
 * Created by baixinrui on 3/18/16.
 */
public interface StreamingOutput
{
    public void write(HttpURLConnection connection , SyncRequest request) throws IOException;
}
