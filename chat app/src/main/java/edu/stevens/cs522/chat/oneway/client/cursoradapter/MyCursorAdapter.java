package edu.stevens.cs522.chat.oneway.client.cursoradapter;

import android.content.Context;
import android.database.Cursor;
//import net.sqlcipher.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import edu.stevens.cs522.chat.oneway.client.R;
import edu.stevens.cs522.chat.oneway.client.contracts.MessageContract;

/**
 * Created by baixinrui on 4/3/16.
 */
public class MyCursorAdapter extends CursorAdapter
{
    private LayoutInflater cursorInflater;

    public MyCursorAdapter(Context context, Cursor c , int flags)
    {
        super(context, c , flags);
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        return cursorInflater.inflate(R.layout.message_row, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        TextView textViewRoom = (TextView) view.findViewById(R.id.message_row_room);
        String room = cursor.getString( cursor.getColumnIndex(MessageContract.CHATROOM) );
        textViewRoom.setText(room);
        TextView textViewMessage = (TextView) view.findViewById(R.id.message_row_message);
        String sender = cursor.getString( cursor.getColumnIndex(MessageContract.SENDER) );
        String message = cursor.getString( cursor.getColumnIndex(MessageContract.MESSAGE) );
        double latitude = cursor.getDouble(cursor.getColumnIndex(MessageContract.MESSAGE_LATITUDE));
        double longitude = cursor.getDouble(cursor.getColumnIndex(MessageContract.MESSAGE_LONGITUDE));
        String meslatitude = Double.toString(latitude);
        String meslongitude = Double.toString(longitude);
        long timestamp = cursor.getLong(cursor.getColumnIndex(MessageContract.TIME_STAMP));
        String time = Long.toString(timestamp);
        String information = sender + ": " + message + "  " + "(" + meslatitude + " , " + meslongitude + ") " +"                (time: " + time + " )";
        textViewMessage.setText(information);
    }

}
