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
import edu.stevens.cs522.chat.oneway.client.callbacks.SendingCommunicator;

/**
 * Created by baixinrui on 3/29/16.
 */
public class MessageSendingFragement extends DialogFragment implements View.OnClickListener
{
    private Button send;
    private Button cancel;
    private EditText sendingmessage;
    private SendingCommunicator sendingCommunicator;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        sendingCommunicator = (SendingCommunicator)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.messagesendingdialog , null);
        sendingmessage = (EditText)view.findViewById(R.id.message_sending);
        send = (Button)view.findViewById(R.id.SEND_BUTTON);
        cancel = (Button)view.findViewById(R.id.CANCLESEND_BUTTON);
        send.setOnClickListener(this);
        cancel.setOnClickListener(this);
        setCancelable(false);
        return view;
    }

    @Override
    public void onClick(View view)
    {
        if(view.getId() == R.id.SEND_BUTTON)
        {
            String message = sendingmessage.getText().toString();

            sendingCommunicator.sending(message);//using callback to connect ChatroomActivity to insert the new messages to the content provider
            sendingmessage.setText("");
            dismiss();
        }
        else if(view.getId() == R.id.CANCLESEND_BUTTON)
        {
            dismiss();
        }
    }
}


