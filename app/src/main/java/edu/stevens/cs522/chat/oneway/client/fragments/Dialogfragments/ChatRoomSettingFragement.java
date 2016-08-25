package edu.stevens.cs522.chat.oneway.client.fragments.Dialogfragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import edu.stevens.cs522.chat.oneway.client.R;
import edu.stevens.cs522.chat.oneway.client.callbacks.SettingCommunictor;

/**
 * Created by baixinrui on 3/29/16.
 */
public class ChatRoomSettingFragement extends DialogFragment implements View.OnClickListener
{
    private Button set;
    private Button cancel;
    private SettingCommunictor settingCommunictor;
    private EditText roomname;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        settingCommunictor = (SettingCommunictor)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.roomsettingdialog , null);

        roomname = (EditText)view.findViewById(R.id.chatroom_name);
        set = (Button)view.findViewById(R.id.SETROOM_BUTTON);
        cancel = (Button)view.findViewById(R.id.CANCLESET_BUTTON);
        set.setOnClickListener(this);
        cancel.setOnClickListener(this);
        setCancelable(false);
        return view;
    }

    @Override
    public void onClick(View view)
    {
        if(view.getId() == R.id.SETROOM_BUTTON)
        {
            String chatroomname = roomname.getText().toString();
            settingCommunictor.setting(chatroomname);//using callback to insert the new chatroom to the content provider in chatroom Activity
            roomname.setText("");
            dismiss();
        }
        else if(view.getId() == R.id.CANCLESET_BUTTON)
        {
            dismiss();
        }
    }

}
