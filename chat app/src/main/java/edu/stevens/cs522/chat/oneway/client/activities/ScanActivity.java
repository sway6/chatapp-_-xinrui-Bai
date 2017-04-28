package edu.stevens.cs522.chat.oneway.client.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import edu.stevens.cs522.chat.oneway.client.CaptureClient.CaptureClient;
import edu.stevens.cs522.chat.oneway.client.R;

/**
 * Created by baixinrui on 4/30/16.
 */
public class ScanActivity extends Activity implements View.OnClickListener {

    final static private String TAG = ScanActivity.class.getSimpleName();
    private Button scanbutton;
    private String qrcode = null;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scancode);
        scanbutton = (Button) findViewById(R.id.SCAN_BUTTON);
        scanbutton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        CaptureClient.launch(this, 1, "scan code");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == CaptureClient.CAPTURE_OK){
                qrcode = data.getStringExtra(CaptureClient.RESULT_KEY);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                Toast.makeText(getBaseContext(), "failed to scan QR code ", Toast.LENGTH_LONG).show();
            }
        }

        if(qrcode != null)  {
            Log.d(TAG, "onActivityResult: securitykey is " + qrcode);
            Toast.makeText(getBaseContext(), "key is " + qrcode, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, ChatClient.class);
            intent.putExtra("SCAN_CODE", qrcode);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getBaseContext(), "failed to scan QR code ", Toast.LENGTH_LONG).show();
        }

    }
}
