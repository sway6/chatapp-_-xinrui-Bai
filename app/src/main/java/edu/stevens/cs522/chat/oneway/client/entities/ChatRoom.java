package edu.stevens.cs522.chat.oneway.client.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.chat.oneway.client.contracts.ChatroomContract;

/**
 * Created by baixinrui on 3/29/16.
 */
public class ChatRoom implements Parcelable
{
    public int id;
    public String roomname;

    public ChatRoom(int id, String roomname)
    {
        this.id = id;
        this.roomname = roomname;
    }

    public ChatRoom(Parcel in)
    {
        id = in.readInt();
        roomname = in.readString();
    }

    public ChatRoom(Cursor cursor)
    {
        this.id = ChatroomContract.getID(cursor);
        this.roomname = ChatroomContract.getRoomName(cursor);
    }

    public void writeToProvider(ContentValues values)
    {
        values.put(ChatroomContract.ROOM_NAME, roomname);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeString(roomname);
    }

    public static final Parcelable.Creator<ChatRoom> CREATOR = new Creator<ChatRoom>()
    {
        @Override
        public ChatRoom createFromParcel(Parcel source)
        {
            return new ChatRoom(source);
        }

        @Override
        public ChatRoom[] newArray(int size)
        {
            return new ChatRoom[size];
        }
    };
}
