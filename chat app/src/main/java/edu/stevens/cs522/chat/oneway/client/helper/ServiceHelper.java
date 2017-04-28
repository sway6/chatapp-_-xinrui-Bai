package edu.stevens.cs522.chat.oneway.client.helper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import edu.stevens.cs522.chat.oneway.client.callbacks.ResultReceiverWrapper;
import edu.stevens.cs522.chat.oneway.client.httprequests.RegisterRequest;
import edu.stevens.cs522.chat.oneway.client.httprequests.SyncRequest;
import edu.stevens.cs522.chat.oneway.client.services.RequestService;

/**
 * Created by baixinrui on 3/11/16.
 */
public class ServiceHelper
{
    final static private String TAG = ServiceHelper.class.getSimpleName();
    private Context context;
    private ResultReceiverWrapper mReceiverWraper;

    public ServiceHelper(Context context , ResultReceiverWrapper mReceiverWraper)
    {
        this.context = context;
        this.mReceiverWraper = mReceiverWraper;
    }

    public void startRequestService(RegisterRequest registerRequest)
    {
        Intent intent = new Intent(context , RequestService.class);
        Bundle bundle = new Bundle();
        bundle.putString("REQUEST_TYPE" , "REGISTER");
        bundle.putParcelable("REGISTER_REQUEST", registerRequest);
        intent.putExtras(bundle);
        context.startService(intent);
    }

    public void startRequestService(SyncRequest syncRequest , int roomindex)
    {
        Intent intent = new Intent(context , RequestService.class);
        Bundle bundle = new Bundle();
        Log.d(TAG, "now in the ServiceHelper class and the messages lenght in syncRequest is " + Integer.toString(syncRequest.messages.size()));
        bundle.putString("REQUEST_TYPE", "SYNC");
        bundle.putInt("ROOM_ID" , roomindex);
        bundle.putParcelable("SYNC_REQUEST", syncRequest);
        bundle.putParcelable("RECEIVER_WRAPPER" , mReceiverWraper);
        intent.putExtras(bundle);
        context.startService(intent);
    }

}
