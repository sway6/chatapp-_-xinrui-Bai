package edu.stevens.cs522.chat.oneway.client.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.stevens.cs522.chat.oneway.client.R;
import edu.stevens.cs522.chat.oneway.client.callbacks.IChatRoomFragmentListener;
import edu.stevens.cs522.chat.oneway.client.callbacks.IEntityCreator;
import edu.stevens.cs522.chat.oneway.client.callbacks.ISimpleQueryListener;
import edu.stevens.cs522.chat.oneway.client.callbacks.ResultReceiverWrapper;
import edu.stevens.cs522.chat.oneway.client.callbacks.SendingCommunicator;
import edu.stevens.cs522.chat.oneway.client.callbacks.SettingCommunictor;
import edu.stevens.cs522.chat.oneway.client.contracts.PeerContract;
import edu.stevens.cs522.chat.oneway.client.entities.ChatRoom;
import edu.stevens.cs522.chat.oneway.client.entities.Message;
import edu.stevens.cs522.chat.oneway.client.entities.Peer;
import edu.stevens.cs522.chat.oneway.client.fragments.Dialogfragments.ChatRoomSettingFragement;
import edu.stevens.cs522.chat.oneway.client.fragments.Dialogfragments.ChatroomwarningFragement;
import edu.stevens.cs522.chat.oneway.client.fragments.Dialogfragments.MessageSendingFragement;
import edu.stevens.cs522.chat.oneway.client.fragments.Listfragments.ChatRoomMessageFragement;
import edu.stevens.cs522.chat.oneway.client.helper.ServiceHelper;
import edu.stevens.cs522.chat.oneway.client.httprequests.SyncRequest;
import edu.stevens.cs522.chat.oneway.client.managers.ChatroomManager;
import edu.stevens.cs522.chat.oneway.client.managers.MessageManager;
import edu.stevens.cs522.chat.oneway.client.managers.PeerManager;
import edu.stevens.cs522.chat.oneway.client.services.RequestService;

/**
 * Created by baixinrui on 3/29/16.
 */
public class ChatroomActivity extends Activity implements SettingCommunictor, SendingCommunicator,
        IChatRoomFragmentListener, ResultReceiverWrapper.IReceiver, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    final static private String TAG = ChatroomActivity.class.getSimpleName();
    public static final String MESSAGE_RESPONSE = "edu.stevens.cs522.chat.oneway.client.MESSAGE_RESPONSE";
    private ChatroomManager chatroomManager;
    private PeerManager peerManager;
    private MessageManager messageManager;
    private ResultReceiverWrapper mReceiverWraper;
    private msgBroadcastReceiver receiver;
    private static final int CHATROOM_LOADER_ID = 13;
    private static final int Message_LOADER_ID = 23;
    private static final int Peer_LOADER_ID = 33;
    private int roomid = 0;
    private String chatroomname;
    private SharedPreferences preferences;
    private String messagetext;
    private String clientid;
    private String uuidstring;
    private String serverurl;
    private String username;
    private double clientlatitude;
    private double clientlongitude;
    private double messagelatitude;
    private double messgaelongitude;
    private long timestamp;
    private GoogleApiClient mesGoogleApiClient;
    private Location mesCurrentLocation;
    private LocationRequest mesLocationRequest;
    private String securitykey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatroomfragment_layout);

        mReceiverWraper = new ResultReceiverWrapper(new Handler());//to be sent to the ReceiverAsync service to get message
        mReceiverWraper.setReceiver(this);

        peerManager = new PeerManager(this, new IEntityCreator<Peer>() {
            @Override
            public Peer create(Cursor cursor) {
                return null;
            }
        }, Peer_LOADER_ID , securitykey);

        messageManager = new MessageManager(this, new IEntityCreator<Message>() {
            @Override
            public Message create(Cursor cursor) {
                Message instance = new Message(cursor);
                return instance;
            }
        }, Message_LOADER_ID , securitykey);

        Intent intent = getIntent();
        clientid = intent.getStringExtra("CLIENT_ID");
        securitykey = intent.getStringExtra("SECURE_KRY");

        preferences = getSharedPreferences("MY_PREFERENCE", MODE_PRIVATE);
        uuidstring = preferences.getString("UUID_STRING", null);
        serverurl = preferences.getString("DESTINATION_URL", null);
        username = preferences.getString("USER_NAME", null);
        clientlatitude = preferences.getFloat("CLIENT_LATITUDE", 0);
        clientlongitude = preferences.getFloat("CLIENT_LONGITUDE", 0);
        Log.d(TAG, "Chatroom: " + "clientlatitude is " + Double.toString(clientlatitude) + " clientlongitude " + Double.toString(clientlongitude));

        IntentFilter filter = new IntentFilter(MESSAGE_RESPONSE);
        receiver = new msgBroadcastReceiver();
        registerReceiver(receiver, filter);

        createLocationRequest();
        updateValuesFromBundle(savedInstanceState);

        if (mesGoogleApiClient == null) {
            mesGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {//get the state which has been saved.
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains("MES_LOCATION")) {
                mesCurrentLocation = savedInstanceState.getParcelable("MES_LOCATION");
            }
            if (mesCurrentLocation != null) {
                updatemescoordinate();
            }
        }
    }

    @Override
    protected void onStart() {
        mesGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mesGoogleApiClient.isConnected()) {
            doLocationUpdates();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.addpreference_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.peer_name:
                Intent intent = new Intent(this, ClientsActivity.class);
                intent.putExtra("CHATROOM_ID", roomid);
                intent.putExtra("SECURE_KRY" , securitykey);
                startActivity(intent);
                break;

            case R.id.chatroom_setting:
                FragmentManager setmanager = getFragmentManager();
                ChatRoomSettingFragement roomsetting = new ChatRoomSettingFragement();
                roomsetting.show(setmanager, "roomsetting");
                break;

            case R.id.message_sending:
                FragmentManager sendmanager = getFragmentManager();
                MessageSendingFragement sendingFragement = new MessageSendingFragement();
                sendingFragement.show(sendmanager, "messagesending");
                break;

            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void setting(final String chatname)//insert the new chatroom into content provider
    {
        chatroomManager = new ChatroomManager(this, new IEntityCreator<ChatRoom>() {
            @Override
            public ChatRoom create(Cursor cursor) {
                ChatRoom instance = new ChatRoom(cursor);
                return instance;
            }
        }, CHATROOM_LOADER_ID , securitykey);

        chatroomManager.executeQuery(PeerContract.uriwithkey(PeerContract.CONTENT_URI3 , securitykey), new ISimpleQueryListener<ChatRoom>() {
            @Override
            public void handleResults(List<ChatRoom> results) {
                int i = 0;
                for (i = 0; i < results.size(); i++) {
                    if (results.get(i).roomname.equals(chatname)) {
                        break;
                    }
                }
                if (i == results.size()) {
                    ChatRoom chatroom = new ChatRoom(0, chatname);
                    chatroomManager.persistChatroomAsync(chatroom);
                } else {
                    FragmentManager setmanager = getFragmentManager();
                    ChatroomwarningFragement warning = new ChatroomwarningFragement();
                    warning.show(setmanager, "warning");
                }
            }
        });

    }

    @Override

    //get the messages to be sent and using this messagelist to initialize the messagelist which is used to new the Syncrequest
    public void sending(String message) {
        // TODO: 3/30/16 insert messages into the contentprvider(do same things in the MessageSending Activity)
        Log.d(TAG, "ChatroomActivity sending callback accessed successfully");
        messagetext = message;
        timestamp = System.currentTimeMillis() / 1000;
        long clientID = Long.parseLong(clientid);

        Message newmessage = new Message(0, messagetext, username, chatroomname, timestamp, 0,
                clientID, roomid, messagelatitude, messgaelongitude);//the initial message to be insert into the content provider(whose sequencenumber is 0)
        messageManager.persistMessageAsync(newmessage);

        MessageManager msManager = new MessageManager(this, new IEntityCreator<Message>() {
            @Override
            public Message create(Cursor cursor) {
                Message instance = new Message(cursor);
                return instance;
            }
        }, Message_LOADER_ID , securitykey);

        msManager.executeQuery(PeerContract.uriwithkey(PeerContract.CONTENT_URI2 , securitykey), new ISimpleQueryListener<Message>() {
            @Override
            public void handleResults(List<Message> results) {
                long t = 0;
                String sequencenum = null;
                List<Message> msg = new ArrayList<Message>();
                for (int i = 0; i < results.size(); i++) {
                    if (results.get(i).messageid != 0) {
                        t = results.get(i).messageid;
                    } else {
                        sequencenum = Long.toString(t);
                        msg.add(results.get(i));
                    }
                }

                SyncRequest syncrequest = new SyncRequest(serverurl, clientid, uuidstring, sequencenum, clientlatitude, clientlongitude, msg);
                ServiceHelper serviceHelper = new ServiceHelper(ChatroomActivity.this, mReceiverWraper);
                Log.d(TAG, "now in the Messagesending activity and message length in syncrequest is " + Integer.toString(syncrequest.messages.size()));
                Log.d(TAG, "now in the Messagesending activity and message to be send in syncrequest is " + syncrequest.messages.get(0).message);
                serviceHelper.startRequestService(syncrequest, roomid);

            }
        });
    }

    @Override
    public void getroomid(int index)//callback used by ChatRoomFragement
    {
        Log.d(TAG, "ChatroomActivity roomid from ChatRoomFragement is " + Integer.toString(index));
        roomid = index;
    }

    @Override
    public void getroomname(String roomname)//callback used by ChatRoomFragement
    {
        Log.d(TAG, "ChatroomActivity roomname from ChatRoomFragement is " + roomname);
        chatroomname = roomname;
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) throws SQLException//used to get messages from wrapper
    {
        Log.d(TAG, "reach onReceiveResult successfully!");
        List<Peer> clients = resultData.getParcelableArrayList("CLIENTS");
        List<Message> messages = resultData.getParcelableArrayList("MESSAGES");
        String t = resultData.getString("TEXT");
        Log.d(TAG, "activity: " + t);
        Log.d(TAG, "activity: clients length is " + Integer.toString(clients.size()) + " name is " + clients.get(0).name);
        Log.d(TAG, "activity: messages length is " + Integer.toString(messages.size()) + " message is " + messages.get(0).message);
        insertAllpeer(clients);
        insertAllMes(messages);

    }

    public void insertAllpeer(List<Peer> clients) {

        peerManager.deletingAsync(PeerContract.uriwithkey(PeerContract.CONTENT_URI1 , securitykey), null, null);
        Log.d(TAG, "clients length in MessageSending: " + Integer.toString(clients.size()));

        for (int i = 0; i < clients.size(); i++) {
            peerManager.persistpeer(clients.get(i));
        }
    }

    public void insertAllMes(List<Message> messages) {

        messageManager.deletingAsync(PeerContract.uriwithkey(PeerContract.CONTENT_URI2 , securitykey), null, null);
        Log.d(TAG, "messages length in MessageSending: " + Integer.toString(messages.size()));

        for (int i = 0; i < messages.size(); i++) {
            messageManager.persistMessageAsync(messages.get(i));
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "ChatroomActivity onConnected access successfully");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Chatroom onConnected filed!");
            return;
        }
        Log.d(TAG, "Chatroom onconnected successfully!");
        mesCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mesGoogleApiClient);
        doLocationUpdates();
    }

    protected void doLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mesGoogleApiClient, mesLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void createLocationRequest()
    {
        mesLocationRequest = LocationRequest.create();
        mesLocationRequest.setInterval(10000);
        mesLocationRequest.setFastestInterval(5000);
        mesLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        mesCurrentLocation = location;
        updatemescoordinate();
    }

    public void updatemescoordinate()
    {
        messagelatitude = mesCurrentLocation.getLatitude();
        messgaelongitude = mesCurrentLocation.getLongitude();
    }

    public class msgBroadcastReceiver extends BroadcastReceiver//receive the broadcast from receiver service
    {
        public void onReceive(Context context,Intent intent)
        {
            Log.d(TAG, "MessageSending broadcast receive successfully");
            AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            SyncRequest syncRequest = intent.getParcelableExtra("SYNC_REQUEST");

            Intent ishintent = new Intent(ChatroomActivity.this, RequestService.class);
            Bundle bundle = new Bundle();
            bundle.putString("REQUEST_TYPE", "SYNC");
            bundle.putParcelable("SYNC_REQUEST", syncRequest);
            bundle.putParcelable("RECEIVER_WRAPPER" , mReceiverWraper);
            ishintent.putExtras(bundle);
            PendingIntent pintent = PendingIntent.getService(ChatroomActivity.this, 0, ishintent, PendingIntent.FLAG_CANCEL_CURRENT);

            String rescode = intent.getStringExtra("RESPONSE_CODE");
            if(!rescode.equals("SENDING_FAIL"))
            {
                Toast.makeText(getBaseContext(), "Sending Successfully!", Toast.LENGTH_LONG).show();
                alarm.cancel(pintent);

            }else
            {
                Toast.makeText(getBaseContext() , "Sending......" , Toast.LENGTH_LONG).show();
                //using alarm manager!!! Do syncrequest every two seconds
                alarm.cancel(pintent);
                alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 2000, pintent);
            }
        }
    }

    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();
        View messagesFrame = this.findViewById(R.id.chatroom_messages);

        if (messagesFrame != null && roomid != 0)
        {
            roomid = 0;
            ChatRoomMessageFragement roommessages = ChatRoomMessageFragement.newInstance(0);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.chatroom_messages, roommessages);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }
        else
        {
            super.onBackPressed();
        }

    }
    public String getkey() {
        return securitykey;
    }


    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mesGoogleApiClient, this);
    }

    protected void onStop()
    {
        mesGoogleApiClient.disconnect();
        super.onStop();
    }

    public void onDestroy()
    {

        try
        {
            if(receiver!=null)
                unregisterReceiver(receiver);
        }
        catch(Exception e)
        {

        }
        super.onDestroy();

    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable("MES_LOCATION", mesCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }
}
