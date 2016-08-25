package edu.stevens.cs522.chat.oneway.client.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.chat.oneway.client.contracts.MessageContract;

/**
 * Created by baixinrui on 3/8/16.
 */
public class Message implements Parcelable
{

    public int id;
    public String message;
    public String sender;
    public String chatroom;
    public long timestamp;
    public long messageid;
    public long peer_fk;
    public long chatroom_fk;
    public double message_latitude;
    public double message_longitude;

    public Message(int id , String message , String sender , String chatroom, long timestamp ,
                   long messageid , long peer_fk , long chatroom_fk , double message_latitude , double message_longitude)
    {
        this.id = id;
        this.message = message;
        this.sender = sender;
        this.chatroom = chatroom;
        this.timestamp = timestamp;
        this.messageid = messageid;
        this.peer_fk = peer_fk;
        this.chatroom_fk = chatroom_fk;
        this.message_latitude = message_latitude;
        this.message_longitude = message_longitude;
    }

    public Message(Parcel in)
    {
        id = in.readInt();
        message = in.readString();
        sender = in.readString();
        chatroom = in.readString();
        timestamp = in.readLong();
        messageid = in.readLong();
        peer_fk = in.readLong();
        chatroom_fk = in.readLong();
        message_latitude = in.readDouble();
        message_longitude = in.readDouble();
    }

    public Message(Cursor cursor)
    {
        this.id = MessageContract.getID(cursor);
        this.message = MessageContract.getMessage(cursor);
        this.sender = MessageContract.getSender(cursor);
        this.chatroom = MessageContract.getChatroom(cursor);
        this.timestamp = MessageContract.getTimeStamp(cursor);
        this.messageid = MessageContract.getMessageid(cursor);
        this.peer_fk = MessageContract.getClientid(cursor);
        this.chatroom_fk = MessageContract.getChatroomid(cursor);
        this.message_latitude = MessageContract.getMessagelatitude(cursor);
        this.message_longitude = MessageContract.getMessagelongitude(cursor);
    }

    public void writeToProvider(ContentValues values)
    {
        values.put(MessageContract.MESSAGE, message);
        values.put(MessageContract.SENDER , sender);
        values.put(MessageContract.CHATROOM , chatroom);
        values.put(MessageContract.TIME_STAMP , timestamp);
        values.put(MessageContract.MESSAGE_ID , messageid);
        values.put(MessageContract.PEER_FK , peer_fk);
        values.put(MessageContract.CHATROOM_FK , chatroom_fk);
        values.put(MessageContract.MESSAGE_LATITUDE , message_latitude);
        values.put(MessageContract.MESSAGE_LONGITUDE , message_longitude);
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
        dest.writeString(message);
        dest.writeString(sender);
        dest.writeString(chatroom);
        dest.writeLong(timestamp);
        dest.writeLong(messageid);
        dest.writeLong(peer_fk);
        dest.writeLong(chatroom_fk);
        dest.writeDouble(message_latitude);
        dest.writeDouble(message_longitude);
    }

    public static final Parcelable.Creator<Message> CREATOR = new Creator<Message>()
    {
        @Override
        public Message createFromParcel(Parcel source)
        {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size)
        {
            return new Message[size];
        }
    };

}
