package edu.stevens.cs522.chat.oneway.client.contracts;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by baixinrui on 3/29/16.
 */
public class ChatroomContract
{
    public static final String _ID = "id";
    public static final String ROOM_NAME = "roomname";

    public static int getID(Cursor cursor)
    {
        return cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
    }

    public static void putID(ContentValues values, int id) {
        values.put(_ID, id);
    }

    public static String getRoomName(Cursor cursor)
    {
        return cursor.getString(cursor.getColumnIndexOrThrow(ROOM_NAME));
    }

    public static void putRoomName(ContentValues values, String roomname)
    {
        values.put(ROOM_NAME, roomname);
    }
}
