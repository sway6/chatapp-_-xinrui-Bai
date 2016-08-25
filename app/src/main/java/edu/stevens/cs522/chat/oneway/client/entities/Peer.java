package edu.stevens.cs522.chat.oneway.client.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.chat.oneway.client.contracts.PeerContract;

/**
 * Created by baixinrui on 3/8/16.
 */
public class Peer implements Parcelable
{
    public int id;
    public String name;
    public long clientid;
    public String clientaddress;
    public double client_latitude;
    public double client_longitude;

    public Peer(int id , String name , long clientid , String clientaddress , double client_latitude , double client_longitude)
    {
        this.id = id;
        this.name = name;
        this.clientid = clientid;
        this.clientaddress = clientaddress;
        this.client_latitude = client_latitude;
        this.client_longitude = client_longitude;
    }

    public Peer(Parcel in)
    {
        id = in.readInt();
        name = in.readString();
        clientid = in.readLong();
        clientaddress = in.readString();
        client_latitude = in.readDouble();
        client_longitude = in.readDouble();
    }

    public Peer(Cursor cursor)
    {
        this.id = PeerContract.getID(cursor);
        this.name = PeerContract.getName(cursor);
        this.clientid = PeerContract.getClientid(cursor);
        this.clientaddress = PeerContract.getClientAddress(cursor);
        this.client_latitude = PeerContract.getClientlatitude(cursor);
        this.client_longitude = PeerContract.getClientlongitude(cursor);
    }

    public void writeToProvider(ContentValues values)
    {
        values.put(PeerContract.NAME, name);
        values.put(PeerContract.CLIENT_ID , clientid);
        values.put(PeerContract.CLIENT_ADDRESS , clientaddress);
        values.put(PeerContract.CLIENT_LATITUDE , client_latitude);
        values.put(PeerContract.CLIENT_LONGITUDE , client_longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeLong(clientid);
        dest.writeString(clientaddress);
        dest.writeDouble(client_latitude);
        dest.writeDouble(client_longitude);
    }

    public static final Parcelable.Creator<Peer> CREATOR = new Creator<Peer>()
    {
        @Override
        public Peer createFromParcel(Parcel source) {
            return new Peer(source);
        }

        @Override
        public Peer[] newArray(int size) {
            return new Peer[size];
        }
    };
}
