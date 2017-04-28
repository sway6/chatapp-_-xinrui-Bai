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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stevens.cs522.chat.oneway.client.entities.Message;
import edu.stevens.cs522.chat.oneway.client.httpresponses.HttpResponse;

/**
 * Created by baixinrui on 3/18/16.
 */
public class SyncRequest extends Request
{

    final static private String TAG = SyncRequest.class.getSimpleName();
    public String serverurl;
    public String clientID;
    public String registrationID; // sanity check,UUID.tostring()
    public String sequencenumber;
    public double clientlatitude;
    public double clientlongitude;
    public List<Message> messages;
    // TODO: 4/11/16 add "latitude" and "longitude";

//    public Message(int id , String message , long timestamp , long messageid , long clientid)

    public SyncRequest(String serverurl , String clientID , String registrationID , String sequencenumber ,
                       double clientlatitude , double clientlongitude, List<Message> messages)
    {
        this.serverurl = serverurl;
        this.clientID = clientID;
        this.registrationID = registrationID;
        this.sequencenumber = sequencenumber;
        this.clientlatitude = clientlatitude;
        this.clientlongitude = clientlongitude;
        this.messages = messages;
    }

    @Override
    public Map<String, String> getRequestHeaders()
    {
        Map<String, String>  params = new HashMap<String, String>();
        params.put("Content-Type" , "application/json");
//        params.put("X-latitude", "40.7439905");
//        params.put("X-longitude", "74.0323626");
        params.put("X-latitude", Double.toString(clientlatitude));
        params.put("X-longitude", Double.toString(clientlongitude));
        return params;
    }

    @Override
    public Uri getRequestUri()
    {
        Log.d(TAG, "now in the PostMessageRequest and the clientID is: " + clientID);
        Uri requesturi = Uri.parse(serverurl + "/chat/" + clientID + "?regid=" + registrationID + "&seqnum="+sequencenumber);
     //   'http://localhost:8080/chat/1?regid=...&seqnum=0'
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
        writeMessagaeArray(jw);
    }

    public void writeMessagaeArray(JsonWriter writer) throws IOException
    {
        writer.beginArray();
        Log.d(TAG, "SyncRequest the messages which is going to send is " + Integer.toString(messages.size()));
        for(Message message : messages)
        {
            writeMessage(writer , message);
        }
        writer.endArray();

    }

    public void writeMessage(JsonWriter writer , Message message) throws IOException
    {
        writer.beginObject();
        writer.name("chatroom").value(message.chatroom);//write chatroom name
        writer.name("timestamp").value(message.timestamp);
        writer.name("latitude").value(message.message_latitude);
        writer.name("longitude").value(message.message_longitude);
        writer.name("text").value(message.message);
        writer.endObject();
    }

    public HttpResponse getResponse(HttpURLConnection connection) throws IOException
    {
        int status = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String clientid = "";
        return new HttpResponse(status , clientid , responseMessage);
    }

    public HttpResponse getResponse(HttpURLConnection connection, JsonReader rd) throws IOException
    {
        return null;
    }


    public SyncRequest(Parcel in)
    {
        serverurl = in.readString();
        clientID = in.readString();
        registrationID = in.readString();
        sequencenumber = in.readString();
        clientlatitude = in.readDouble();
        clientlongitude = in.readDouble();
        messages = new ArrayList<Message>();
        in.readTypedList(messages , Message.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(serverurl);
        dest.writeString(clientID);
        dest.writeString(registrationID);
        dest.writeString(sequencenumber);
        dest.writeDouble(clientlatitude);
        dest.writeDouble(clientlongitude);
        dest.writeTypedList(messages);
    }

    public static final Parcelable.Creator<SyncRequest> CREATOR = new Creator<SyncRequest>()
    {
        @Override
        public SyncRequest createFromParcel(Parcel source)
        {
            return new SyncRequest(source);
        }

        @Override
        public SyncRequest[] newArray(int size)
        {
            return new SyncRequest[size];
        }
    };
}
