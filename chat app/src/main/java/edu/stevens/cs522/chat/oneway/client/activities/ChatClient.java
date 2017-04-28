/*********************************************************************

 Client for sending chat messages to the server.

 Copyright (c) 2012 Stevens Institute of Technology

 **********************************************************************/
package edu.stevens.cs522.chat.oneway.client.activities;

import java.util.UUID;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import edu.stevens.cs522.chat.oneway.client.R;
import edu.stevens.cs522.chat.oneway.client.callbacks.ResultReceiverWrapper;
import edu.stevens.cs522.chat.oneway.client.helper.ServiceHelper;
import edu.stevens.cs522.chat.oneway.client.httprequests.RegisterRequest;

/*
 * @author dduggan
 * 
 */
public class ChatClient extends Activity implements OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    final static private String TAG = ChatClient.class.getSimpleName();
    public static final String RESPONSE_CODE = "edu.stevens.cs522.chat.oneway.client.RESPONSE_CODE";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 101;
    private responsBroadcastReceiver receiver;
    private EditText destinationurl;
    private EditText username;
    private Button setbutton;
    private UUID clientuuid;
    private String uuidstring;
    private String clientid;
    private ResultReceiverWrapper mReceiverWraper;
    private double clientlatitude;
    private double clientlongitude;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private String qrcode;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        mReceiverWraper = null;

        qrcode = getIntent().getStringExtra("SCAN_CODE");
        Log.d(TAG, "onCreate: key is " + qrcode);

        destinationurl = (EditText) findViewById(R.id.destination_url);
        username = (EditText) findViewById(R.id.user_name);
        setbutton = (Button) findViewById(R.id.SET_BUTTON);
        setbutton.setOnClickListener(this);

        IntentFilter filter = new IntentFilter(RESPONSE_CODE);

        receiver = new responsBroadcastReceiver();
        registerReceiver(receiver, filter);
        checkPlayServices(this);

        createLocationRequest();
        updateValuesFromBundle(savedInstanceState);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {//get the state which has been saved.
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains("LOCATION_KEY")) {
                mCurrentLocation = savedInstanceState.getParcelable("LOCATION_KEY");
            }
            if(mCurrentLocation != null) {
                updatecoordinate();
            }
        }
    }

    public static boolean checkPlayServices(Activity context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, context, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.e(TAG, "This device is not supported.");
                context.finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "ChatClient onStart() has been called");
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    public void onClick(View v) {
        Log.d(TAG, "we now in the setbutton click method");
        clientuuid = UUID.randomUUID();
        uuidstring = clientuuid.toString();
        SharedPreferences.Editor editor = getSharedPreferences("MY_PREFERENCE", MODE_PRIVATE).edit();
        String url = destinationurl.getText().toString();
        String name = username.getText().toString();
        editor.putString("DESTINATION_URL", url);
        editor.putString("USER_NAME", name);
        editor.putString("UUID_STRING", uuidstring);
        editor.putLong("CLIENT_ID", 0);
        editor.putFloat("CLIENT_LATITUDE", (float) clientlatitude);
        editor.putFloat("CLIENT_LONGITUDE", (float) clientlongitude);
        editor.commit();

        RegisterRequest registerRequest = new RegisterRequest(uuidstring, url, name, clientlatitude, clientlongitude);

        ServiceHelper serviceHelper = new ServiceHelper(this, mReceiverWraper);

        serviceHelper.startRequestService(registerRequest);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mGoogleApiClient.isConnected()) {
            Log.d(TAG, "ChatClient: onConnected has been called and client has connected");
        } else {
            Log.d(TAG, "ChatClient: onConnected has been called and client has not been connected");
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    99);
            // TODO: Consider calling
            Log.d(TAG, "Chatclient onConnected filed!");
        }
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void createLocationRequest()
    {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location)
    {
        mCurrentLocation = location;
        updatecoordinate();
    }

    public void updatecoordinate()
    {
        clientlatitude = mCurrentLocation.getLatitude();
        clientlongitude = mCurrentLocation.getLongitude();
        Log.d(TAG, "ChatClient: the coordibate of client is: " + Double.toString(clientlatitude) + " " + Double.toString(clientlongitude));
    }

    public class responsBroadcastReceiver extends BroadcastReceiver//receive the broadcast from receiver service
    {
        public void onReceive(Context context,Intent intent)
        {
            String rescode = intent.getStringExtra("RESPONSE_CODE");
            if(!rescode.equals("REGISTER_FAIL"))
            {
                clientid = intent.getStringExtra("CLIENT_ID");
                Long lclientid = Long.parseLong(clientid);
                SharedPreferences.Editor editor = getSharedPreferences("MY_PREFERENCE" , MODE_PRIVATE).edit();
                editor.putLong("CLIENT_ID" , lclientid);
                Toast.makeText(getBaseContext(), "Response Code: " + rescode + " client id: " + clientid , Toast.LENGTH_LONG).show();
                Intent newintent = new Intent(ChatClient.this , ChatroomActivity.class);
                newintent.putExtra("CLIENT_ID" , clientid);
                newintent.putExtra("SECURE_KRY" , qrcode);
                Log.d(TAG, "key in chatclient is "+ qrcode);
                ChatClient.this.startActivity(newintent);
            }
            else
            {
                Toast.makeText(getBaseContext() , "Register failed, please change your username" , Toast.LENGTH_LONG).show();
            }
        }
    }

    public String getKey() {
        return this.qrcode;
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    protected void onStop()
    {
        mGoogleApiClient.disconnect();
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

    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putParcelable("LOCATION_KEY", mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

}