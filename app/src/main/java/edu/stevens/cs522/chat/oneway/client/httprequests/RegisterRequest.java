package edu.stevens.cs522.chat.oneway.client.httprequests;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import edu.stevens.cs522.chat.oneway.client.httpresponses.HttpResponse;

/**
 * Created by baixinrui on 3/9/16.
 */
public class RegisterRequest extends Request////we should put UUID to string and then transfer it to the request
{
    // TODO: 3/9/16 receive a shared preference from UI to initiate a request.There are server URL and client username in it;
    final static private String TAG = RegisterRequest.class.getSimpleName();
    public String registrationID;//UUID,sanity check,UUID.tostring()
    private String serverurl;
    private String username;
    private double registerlatitude;
    private double registerlongitude;

    public RegisterRequest(String registrationID , String serverurl , String username ,
                           double registerlatitude , double registerlongitude)
    {
        this.registrationID = registrationID;
        this.serverurl = serverurl;
        this.username = username;
        this.registerlatitude = registerlatitude;
        this.registerlongitude = registerlongitude;
    }

    public RegisterRequest(Parcel in)
    {
        registrationID = in.readString();
        serverurl = in.readString();
        username = in.readString();
        registerlatitude = in.readDouble();
        registerlongitude = in.readDouble();
    }

    @Override
    public Map<String, String> getRequestHeaders()
    {
        Map<String, String>  params = new HashMap<String, String>();
        params.put("X-latitude", Double.toString(registerlatitude));
        params.put("X-longitude", Double.toString(registerlongitude));

        return params;
    }

    @Override
    public Uri getRequestUri()//gen the uri using UUID and shared preference and encode();
    {
        Uri requesturi = Uri.parse(serverurl + "/chat?username=" + username + "&regid=" + registrationID);
//      'http://localhost:8080/chat?username=joe&regid=...'
        return requesturi;
    }

    @Override
    public String getRequestEntity() throws IOException//not use in RegisterRequest
    {
        return null;
    }

    @Override
    public HttpResponse getResponse(HttpURLConnection connection, JsonReader rd) throws IOException//find it in the ppt;deal with the response from server
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
        Log.d(TAG, "now in the RegisterRequest class the responsecode is: " + Integer.toString(status) + " id is: " + clientid);

        return new HttpResponse(status , clientid , responseMessage);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(registrationID);
        dest.writeString(serverurl);
        dest.writeString(username);
        dest.writeDouble(registerlatitude);
        dest.writeDouble(registerlongitude);
    }

    public static final Parcelable.Creator<RegisterRequest> CREATOR = new Creator<RegisterRequest>()
    {
        @Override
        public RegisterRequest createFromParcel(Parcel source)
        {
            return new RegisterRequest(source);
        }

        @Override
        public RegisterRequest[] newArray(int size)
        {
            return new RegisterRequest[size];
        }
    };

}
