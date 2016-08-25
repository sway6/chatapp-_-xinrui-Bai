package edu.stevens.cs522.chat.oneway.client.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by baixinrui on 3/8/16.
 */
public class MessageContract
{

    public static final String _ID = "id";
    public static final String MESSAGE = "message";
    public static final String TIME_STAMP = "timestamp";
    public static final String MESSAGE_ID = "messageid";
    public static final String PEER_FK = "peer_fk";
    public static final String SENDER = "sender";
    public static final String CHATROOM = "chatroom";
    public static final String CHATROOM_FK = "chatroom_fk";
    public static final String MESSAGE_LATITUDE = "message_latitude";
    public static final String MESSAGE_LONGITUDE = "message_longitude";


    public String CONTENT_PATH(Uri uri)
    {
        return uri.getPath().substring(1);
    }

    public static int getID(Cursor cursor)
    {
        return cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
    }

    public static void putID(ContentValues values, int id) {
        values.put(_ID, id);
    }

    public static String getMessage(Cursor cursor)
    {
        return cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE));
    }

    public static void putMessage(ContentValues values, String message)
    {
        values.put(MESSAGE, message);
    }

    public static String getSender(Cursor cursor)
    {
        return cursor.getString(cursor.getColumnIndexOrThrow(SENDER));
    }

    public static void putSender(ContentValues values, String sender)
    {
        values.put(SENDER, sender);
    }

    public static String getChatroom(Cursor cursor)
    {
        return cursor.getString(cursor.getColumnIndexOrThrow(CHATROOM));
    }

    public static void putChatroom(ContentValues values, String chatroom)
    {
        values.put(CHATROOM, chatroom);
    }

    public static long getTimeStamp(Cursor cursor)
    {
        return cursor.getLong(cursor.getColumnIndexOrThrow(TIME_STAMP));
    }

    public static void putTimestamp(ContentValues values, long timestamp)
    {
        values.put(TIME_STAMP, timestamp);
    }

    public static long getMessageid(Cursor cursor)
    {
        return cursor.getLong(cursor.getColumnIndexOrThrow(MESSAGE_ID));
    }

    public static void putMessageid(ContentValues values, long messageid)
    {
        values.put(MESSAGE_ID, messageid);
    }

    public static long getClientid(Cursor cursor)
    {
        return cursor.getLong(cursor.getColumnIndexOrThrow(PEER_FK));
    }

    public static void putClientid(ContentValues values, long clientid)
    {
        values.put(PEER_FK, clientid);
    }

    public static long getChatroomid(Cursor cursor)
    {
        return cursor.getLong(cursor.getColumnIndexOrThrow(CHATROOM_FK));
    }

    public static void putChatroomid(ContentValues values, long chatroomid)
    {
        values.put(CHATROOM_FK, chatroomid);
    }

    public static double getMessagelatitude(Cursor cursor)
    {
        return cursor.getDouble(cursor.getColumnIndexOrThrow(MESSAGE_LATITUDE));
    }

    public static void putMessagelatitude(ContentValues values, double messagelatitude)
    {
        values.put(MESSAGE_LATITUDE, messagelatitude);
    }

    public static double getMessagelongitude(Cursor cursor)
    {
        return cursor.getDouble(cursor.getColumnIndexOrThrow(MESSAGE_LONGITUDE));
    }

    public static void putMessagelongitude(ContentValues values, double messagelongitude)
    {
        values.put(MESSAGE_LONGITUDE, messagelongitude);
    }

    public static Uri withExtendedPath(Uri uri,String path)
    {
        Uri.Builder builder = uri.buildUpon();
        builder.appendPath(path);
        return builder.build();
    }

}
