package edu.stevens.cs522.chat.oneway.client.processor;

import android.net.Uri;
import android.util.JsonReader;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import edu.stevens.cs522.chat.oneway.client.callbacks.StreamingOutput;
import edu.stevens.cs522.chat.oneway.client.httprequests.PostMessageRequest;
import edu.stevens.cs522.chat.oneway.client.httprequests.RegisterRequest;
import edu.stevens.cs522.chat.oneway.client.httprequests.SyncRequest;
import edu.stevens.cs522.chat.oneway.client.httpresponses.HttpResponse;
import edu.stevens.cs522.chat.oneway.client.httpresponses.StreamingResponse;

/**
 * Created by baixinrui on 3/9/16.
 */
public class RestMethod
{
    final static private String TAG = RestMethod.class.getSimpleName();

    private HttpURLConnection connection;

    private StreamingOutput out;

    public HttpResponse performRegister(RegisterRequest request) throws IOException
    {
        Log.d(TAG, "now in the performRegister");
        Uri registerUri = request.getRequestUri();
        URL registerUrl = new URL(registerUri.toString());

        connection = (HttpURLConnection)registerUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        addRegisterProperty(connection, request);

        JsonReader rd = new JsonReader(
                new BufferedReader(
                        new InputStreamReader(connection.getInputStream())));

        Log.d(TAG, "now in the RestMethod and the response code is: " + Integer.toString(connection.getResponseCode()));

        HttpResponse httpresponse = request.getResponse(connection, rd); //write the response function in the request subclass
        rd.close();

        return httpresponse;
    }

//    public HttpResponse performPost(PostMessageRequest request) throws IOException
//    {
//        Uri postUri = request.getRequestUri();
//        URL postUrl = new URL(postUri.toString());
//        connection = (HttpURLConnection)postUrl.openConnection();
//
//        connection.setRequestMethod("POST");
//        addPostmsgProperty(connection, request);
////        outputRequestEntity(request);
//
//        JsonReader rd = new JsonReader(
//                new BufferedReader(
//                        new InputStreamReader(connection.getInputStream())));
//
//        HttpResponse httpresponse = request.getResponse(connection, rd); //write the response function in the request subclass
//        rd.close();
//
//        return httpresponse;
//    }

    public StreamingResponse performSync(SyncRequest request) throws IOException
    {
        Uri asyncUri = request.getRequestUri();
        URL asyncUrl = new URL(asyncUri.toString());
        connection = (HttpURLConnection)asyncUrl.openConnection();

        out = new RequestProcessor();
        out.write(connection , request);//write messages which is going to be sent to the server by client

        HttpResponse response = request.getResponse(connection);//get response messages from server

        return new StreamingResponse(connection , response);
    }

    public void addRegisterProperty(URLConnection connection , RegisterRequest request)
    {
        Map<String,String> headers = request.getRequestHeaders();
        for (Map.Entry<String,String> header : headers.entrySet())
        {
            connection.addRequestProperty(header.getKey(), header.getValue());
        }

    }

    public void addPostmsgProperty(URLConnection connection , PostMessageRequest request)
    {
        Map<String,String> headers = request.getRequestHeaders();
        for (Map.Entry<String,String> header : headers.entrySet())
        {
            connection.addRequestProperty(header.getKey(), header.getValue());
        }
    }

    public void addAsyncProperty(URLConnection connection , SyncRequest request)
    {
        Map<String,String> headers = request.getRequestHeaders();
        for (Map.Entry<String,String> header : headers.entrySet())
        {
            connection.addRequestProperty(header.getKey(), header.getValue());
        }
    }

//    public void outputRequestEntity(PostMessageRequest request) throws IOException //ppt p36; write the messages into the stream to the server;
//    {
//        String requestEntity = request.getRequestEntity();
//        if (requestEntity != null)
//        {
//            connection.setDoOutput(true);
//            connection.setRequestProperty("CONTENT_TYPE", "application/json");
//            byte[] outputEntity = requestEntity.getBytes("UTF-8");
//            connection.setFixedLengthStreamingMode(outputEntity.length);
//            OutputStream out = new BufferedOutputStream(
//                    connection.getOutputStream());
//            out.write(outputEntity);
//            out.flush();
//            out.close();
//        }
//    }
}