package edu.stevens.cs522.chat.oneway.client.activities;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

import edu.stevens.cs522.chat.oneway.client.fragments.Listfragments.ChatRoomMessageFragement;

/**
 * Created by baixinrui on 3/29/16.
 */
public class ChatmesActivity extends Activity
{
    private int index;
    private String securekey;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Toast.makeText(this, "DetailsActivity", Toast.LENGTH_SHORT).show();

        securekey = getIntent().getStringExtra("SECURE_KRY");

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            finish();
            return;
        }

        if (savedInstanceState == null)
        {
            // During initial setup, plug in the details fragment.

            // create fragment
            ChatRoomMessageFragement messages = new ChatRoomMessageFragement();

            messages.setArguments(getIntent().getExtras());//pass the id original from chatroom fragment to the charmes fragement;

            getFragmentManager().beginTransaction()
                    .add(android.R.id.content, messages).commit();
        }
    }
}
