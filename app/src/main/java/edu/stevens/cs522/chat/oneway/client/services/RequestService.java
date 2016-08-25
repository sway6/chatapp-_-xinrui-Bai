package edu.stevens.cs522.chat.oneway.client.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.stevens.cs522.chat.oneway.client.entities.Message;
import edu.stevens.cs522.chat.oneway.client.entities.Peer;
import edu.stevens.cs522.chat.oneway.client.httprequests.RegisterRequest;
import edu.stevens.cs522.chat.oneway.client.httprequests.SyncRequest;
import edu.stevens.cs522.chat.oneway.client.httpresponses.HttpResponse;
import edu.stevens.cs522.chat.oneway.client.httpresponses.StreamingResponse;
import edu.stevens.cs522.chat.oneway.client.processor.RequestProcessor;

/**
 * Created by baixinrui on 3/8/16.
 */
public class RequestService extends IntentService
{
    final static private String TAG = RequestService.class.getSimpleName();
    public static final String RESPONSE_CODE = "edu.stevens.cs522.chat.oneway.client.RESPONSE_CODE";
    public static final String MESSAGE_RESPONSE = "edu.stevens.cs522.chat.oneway.client.MESSAGE_RESPONSE";
    private ResultReceiver mReceiver;

    public RequestService()
    {
        super("RequestService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Bundle bundle = intent.getExtras();
        String requestType = bundle.getString("REQUEST_TYPE");

        assert requestType != null;
        switch (requestType)
        {
            case "REGISTER":
                RegisterRequest registerRequest = bundle.getParcelable("REGISTER_REQUEST");
                RequestProcessor requestProcessor = new RequestProcessor(this);
                try
                {
                    HttpResponse httpResponse = requestProcessor.perform(registerRequest);
                    String code = Integer.toString(httpResponse.statuscode);
                    Intent intent1 = new Intent(RESPONSE_CODE);
                    intent1.putExtra("RESPONSE_CODE" , code);
                    intent1.putExtra("CLIENT_ID" , httpResponse.jasonString);
                    sendBroadcast(intent1);
                } catch (IOException e)
                {
                    e.printStackTrace();
                    Intent intent1 = new Intent(RESPONSE_CODE);
                    intent1.putExtra("RESPONSE_CODE" , "REGISTER_FAIL");
                    sendBroadcast(intent1);
                }
                break;

            case "SYNC":
                SyncRequest syncRequest = bundle.getParcelable("SYNC_REQUEST");
                mReceiver = bundle.getParcelable("RECEIVER_WRAPPER");
                int roomid = bundle.getInt("ROOM_ID");
                Log.d(TAG, "now in the RequestService, the messages length in syncRequest is " + Integer.toString(syncRequest.messages.size()));
                RequestProcessor syncProcessor = new RequestProcessor(this);
                try
                {
                    StreamingResponse streamingResponse = syncProcessor.perform(syncRequest , roomid);
                    Intent syncintent = new Intent(MESSAGE_RESPONSE);
                    syncintent.putExtra("RESPONSE_CODE" , "SENDING_SUCCESS");
                    syncintent.putExtra("SYNC_REQUEST" , syncRequest);
                    sendBroadcast(syncintent);
                    List<Peer> clients = syncProcessor.clients;
                    List<Message> messages = syncProcessor.messages;
                    Log.d(TAG, "service: clients length is " + Integer.toString(clients.size()) + " name is " + clients.get(0).name);
                    Log.d(TAG, "service: messages length is " + Integer.toString(messages.size()) + " message is " + messages.get(0).message);
                    Bundle mybundle = new Bundle();
                    mybundle.putParcelableArrayList("CLIENTS" , (ArrayList<? extends Parcelable>) clients);
                    mybundle.putParcelableArrayList("MESSAGES" , (ArrayList<? extends Parcelable>) messages);
                    mybundle.putString("TEXT" , "Hi from service!" );
                    mReceiver.send(100, mybundle);

                } catch (IOException e)
                {
                    e.printStackTrace();
                    Intent msgresponseintent = new Intent(MESSAGE_RESPONSE);
                    msgresponseintent.putExtra("RESPONSE_CODE", "SENDING_FAIL");
                    msgresponseintent.putExtra("SYNC_REQUEST" , syncRequest);
                    sendBroadcast(msgresponseintent );
                }
                break;
            default:
                break;
        }
    }
}
