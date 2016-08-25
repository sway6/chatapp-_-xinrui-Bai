package edu.stevens.cs522.chat.oneway.client.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import edu.stevens.cs522.chat.oneway.client.R;

/**
 * Created by baixinrui on 3/29/16.
 */
public class ClientsActivity extends Activity
{
    final static private String TAG = ClientsActivity.class.getSimpleName();
    private SharedPreferences preferences;
    private int roomid;
    private String username;
    private String securekey;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clientmessagefragment_layout);
        preferences = getSharedPreferences("MY_PREFERENCE", MODE_PRIVATE);
        roomid = getIntent().getIntExtra("CHATROOM_ID", 0);
        securekey = getIntent().getStringExtra("SECURE_KRY");
        username = preferences.getString("USER_NAME", null);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.clientlocation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.client_location:
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.example.baixinrui.chatmap");
                launchIntent.putExtra("USER_NAME" , username);
                launchIntent.putExtra("SECURE_KRY" , securekey);
                startActivity(launchIntent);
                break;

            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public String getKey() {
        return securekey;
    }

}

