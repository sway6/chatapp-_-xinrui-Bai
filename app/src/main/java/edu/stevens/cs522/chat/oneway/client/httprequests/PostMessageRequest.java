package edu.stevens.cs522.chat.oneway.client.httprequests;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import edu.stevens.cs522.chat.oneway.client.httpresponses.HttpResponse;

/**
 * Created by baixinrui on 3/9/16.
 */
public class PostMessageRequest extends Request
{

    final static private String TAG = PostMessageRequest.class.getSimpleName();
    public String clientID;
    public String registrationID; // sanity check,UUID.tostring()
    private String serverurl;
    private String messageid;
    private String message;
    private long timestamp;

    public PostMessageRequest(String clientID , String registrationID , String serverurl , String messageid , long timestamp , String message)
    {
        this.clientID = clientID;
        this.registrationID = registrationID;
        this.serverurl = serverurl;
        this.messageid = messageid;
        this.timestamp = timestamp;
        this.message = message;
    }

    @Override
    public Map<String, String> getRequestHeaders()
    {
        Map<String, String>  params = new HashMap<String, String>();
        params.put("Content-Type" , "application/json");
        params.put("X-latitude", "40.7439905");
        params.put("X-longitude", "74.0323626");
        return params;
    }

    @Override
    public Uri getRequestUri()
    {
        Log.d(TAG, "now in the PostMessageRequest and the clientID is: " + clientID);
        Uri requesturi = Uri.parse(serverurl + "/chat/" + clientID + "?regid=" + registrationID);
        //'http://localhost:8080/chat/1?regid=...'
        return requesturi;
    }

    @Override
    public String getRequestEntity() throws IOException
    {
        StringWriter sw = new StringWriter();
        JsonWriter jw = new JsonWriter(sw);
        write(jw);
        return sw.toString();
    }

    public void write(JsonWriter jw) throws IOException
    {
        jw.beginObject();
        jw.name("chatroom").value(clientID);
        jw.name("timestamp").value(timestamp);
        jw.name("text").value(message);
        jw.endObject();
//        {
//            "chatroom" : "_default", "timestamp" : 12345678, "text" : "hello"
//        }
    }

    @Override
    public HttpResponse getResponse(HttpURLConnection connection, JsonReader rd) throws IOException
    {
        int status = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String clientid = "";
        rd.beginObject();
        while(rd.hasNext())
        {
            String name = rd.nextName();
            if(name.equals("id"))
            {
                clientid = rd.nextString();
            }
        }
        Log.d(TAG, "now in the PostMessageRequest class the responsecode is: " + Integer.toString(status) + " id is: " + clientid);

        return new HttpResponse(status , clientid , responseMessage);
    }

    public PostMessageRequest(Parcel in)
    {
        clientID = in.readString();
        registrationID = in.readString();
        serverurl = in.readString();
        messageid = in.readString();
        message = in.readString();
        timestamp = in.readLong();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(clientID);
        dest.writeString(registrationID);
        dest.writeString(serverurl);
        dest.writeString(messageid);
        dest.writeString(message);
        dest.writeLong(timestamp);
    }

    public static final Parcelable.Creator<PostMessageRequest> CREATOR = new Creator<PostMessageRequest>()
    {
        @Override
        public PostMessageRequest createFromParcel(Parcel source)
        {
            return new PostMessageRequest(source);
        }

        @Override
        public PostMessageRequest[] newArray(int size)
        {
            return new PostMessageRequest[size];
        }
    };
}
