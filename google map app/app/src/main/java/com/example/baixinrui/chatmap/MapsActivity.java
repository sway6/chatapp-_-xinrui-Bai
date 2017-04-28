package com.example.baixinrui.chatmap;

import android.app.LoaderManager;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import entity.Client;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{
    final static private String TAG = MapsActivity.class.getSimpleName();
    public static final String AUTHORITY = "edu.stevens.cs522.chat.oneway.cloudclient";
    public static final Uri CONTENT_URI1 = Uri.parse("content://" + AUTHORITY + "/peer");
    private GoogleMap mMap;
    private String current_user;
    private String key;
    private List<Client> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        current_user = getIntent().getStringExtra("USER_NAME");
        key = getIntent().getStringExtra("SECURE_KRY");
        list = new ArrayList<Client>();
        try {
            getlocation();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    public Uri uriwithkey(Uri uri , String key) {
        Uri.Builder builder = uri.buildUpon();
        uri = builder.appendQueryParameter("SECURE_KEY", key)
                .build();
        return uri;
    }

    private void getlocation() throws RemoteException {
        Uri uri_key = uriwithkey(CONTENT_URI1 , key);
        ContentProviderClient myCR = getContentResolver().acquireContentProviderClient(uri_key);
        String[] projection = new String[]{"client_latitude", "client_longitude" , "name" , "clientaddress"};
        Cursor cursor =
                myCR.query(uri_key,
                        projection,
                        null,
                        null,
                        null);

        if (cursor.moveToFirst()) {
            do {
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("client_latitude"));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("client_longitude"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String adddress = cursor.getString(cursor.getColumnIndexOrThrow("clientaddress"));
                Client c = new Client(latitude , longitude , name , adddress);
                Log.d(TAG, "client: " + name +  " " + Double.toString(latitude) + " , " + Double.toString(longitude) + " " + adddress);
                list.add(c);
            } while (cursor.moveToNext());
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        for(int i = 0 ; i < list.size() ; i++) {
            Client c = list.get(i);
            LatLng location = new LatLng(c.latitude , c.longitude);
            mMap.addMarker(new MarkerOptions().position(location).title(c.name + " :" + c.address));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));

        }
    }

}
