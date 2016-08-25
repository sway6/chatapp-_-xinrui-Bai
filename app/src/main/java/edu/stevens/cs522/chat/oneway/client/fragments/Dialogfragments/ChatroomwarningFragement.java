package edu.stevens.cs522.chat.oneway.client.fragments.Dialogfragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.stevens.cs522.chat.oneway.client.R;

/**
 * Created by baixinrui on 3/29/16.
 */
public class ChatroomwarningFragement extends DialogFragment implements View.OnClickListener
{
    private Button ok;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.warningfragment , null);
        ok = (Button)view.findViewById(R.id.WARNING_BUTTON);
        ok.setOnClickListener(this);
        setCancelable(false);
        return view;
    }

    @Override
    public void onClick(View view)
    {
        if(view.getId() == R.id.WARNING_BUTTON)
        {
            dismiss();
        }
    }
}
