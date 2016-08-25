package edu.stevens.cs522.chat.oneway.client.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by baixinrui on 3/8/16.
 */
public class PeerContract
{
    public static final String AUTHORITY = "edu.stevens.cs522.chat.oneway.cloudclient";

    public static final Uri CONTENT_URI1 = Uri.parse("content://" + AUTHORITY + "/peer");
    public static final Uri CONTENT_URI2 = Uri.parse("content://" + AUTHORITY + "/message");
    public static final Uri CONTENT_URI3 = Uri.parse("content://" + AUTHORITY + "/chatroom");
    public static final Uri CONTENT_URI4 = Uri.parse("content://" + AUTHORITY + "/chatroommessage");

    public static final String CONTENT_PATH_PEER = "peer";
    public static final String CONTENT_PATH_MESSAGE = "message";
    public static final String CONTENT_PATH_CHATROOM = "chatroom";
    public static final String CONTENT_PATH_CHATROOMMESSAGE = "chatroommessage";

    public static final String _ID = "id";
    public static final String NAME = "name";
    public static final String CLIENT_ID = "clientid";
    public static final String CLIENT_ADDRESS = "clientaddress";
    public static final String CLIENT_LATITUDE = "client_latitude";
    public static final String CLIENT_LONGITUDE = "client_longitude";

    public static Uri uriwithkey(Uri uri , String key) {
        Uri.Builder builder = uri.buildUpon();
        uri = builder.appendQueryParameter("SECURE_KEY", key)
                        .build();
        return uri;
    }


    public String CONTENT_PATH(Uri uri)
    {
        return uri.getPath().substring(1);
    }

    public static int getID(Cursor cursor)
    {
        return cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
    }

    public static void putID(ContentValues values, int id)
    {
        values.put(_ID, id);
    }

    public static String getName(Cursor cursor)
    {
        return cursor.getString(cursor.getColumnIndexOrThrow(NAME));
    }

    public static void putName(ContentValues values, String name)
    {
        values.put(NAME, name);
    }

    public static Long getClientid(Cursor cursor)
    {
        return cursor.getLong(cursor.getColumnIndexOrThrow(CLIENT_ID));
    }

    public static void putClientid(ContentValues values, long clientid)
    {
        values.put(CLIENT_ID, clientid);
    }

    public static String getClientAddress(Cursor cursor)
    {
        return cursor.getString(cursor.getColumnIndexOrThrow(CLIENT_ADDRESS));
    }

    public static void putClientAddress(ContentValues values, String clientaddress)
    {
        values.put(CLIENT_ADDRESS, clientaddress);
    }

    public static double getClientlatitude(Cursor cursor)
    {
        return cursor.getDouble(cursor.getColumnIndexOrThrow(CLIENT_LATITUDE));
    }

    public static void putClientlatitude(ContentValues values, double clientlatitude)
    {
        values.put(CLIENT_LATITUDE, clientlatitude);
    }

    public static double getClientlongitude(Cursor cursor)
    {
        return cursor.getDouble(cursor.getColumnIndexOrThrow(CLIENT_LONGITUDE));
    }

    public static void putClientlongitude(ContentValues values, double Clientlongitude)
    {
        values.put(CLIENT_LONGITUDE, Clientlongitude);
    }


    public static Uri withExtendedPath(Uri uri,String path)
    {
        Uri.Builder builder = uri.buildUpon();
        builder.appendPath(path);
        return builder.build();
    }

}
