package edu.stevens.cs522.chat.oneway.client.httpresponses;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.IOException;

/**
 * Created by baixinrui on 3/11/16.
 */
public class HttpResponse extends Response
{
    final static private String TAG = HttpResponse.class.getSimpleName();

    public int statuscode;

    public String jasonString;//store the client id or message id which is response from the server

    private String responseMessage;//not the response jason, this is the message which is provided by the cloud server;

    private String exceptionMessage;

    public HttpResponse(int statuscode , String jasonString , String responseMessage)
    {
        this.statuscode = statuscode;
        this.jasonString = jasonString;
        this.responseMessage = responseMessage;
        this.exceptionMessage = null;
        Log.d(TAG, "now in the HttpResponse class, statuscode is: " + Integer.toString(statuscode));
    }

    public HttpResponse(Parcel in)
    {
        statuscode = in.readInt();
        jasonString = in.readString();
        responseMessage = in.readString();
        exceptionMessage = in.readString();
    }

    @Override
    public boolean isValid() throws IOException
    {
        if (statuscode < 200 || statuscode >= 300)
        {
            exceptionMessage = "Error response "
                    + statuscode + " "
                    + responseMessage;
            return false;
        }else
        {
            return true;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(statuscode);
        dest.writeString(jasonString);
        dest.writeString(responseMessage);
        dest.writeString(exceptionMessage);
    }

    public static final Parcelable.Creator<HttpResponse> CREATOR = new Creator<HttpResponse>()
    {
        @Override
        public HttpResponse createFromParcel(Parcel source)
        {
            return new HttpResponse(source);
        }

        @Override
        public HttpResponse[] newArray(int size)
        {
            return new HttpResponse[size];
        }
    };

}
