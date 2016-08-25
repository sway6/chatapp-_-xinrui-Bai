package edu.stevens.cs522.chat.oneway.client.callbacks;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import java.sql.SQLException;

/**
 * Created by baixinrui on 3/8/16.
 */

//used to receive response from the intent service to the chatclient and then do the record update in the content provider
public class ResultReceiverWrapper extends ResultReceiver
{
    final static private String TAG = ResultReceiverWrapper.class.getSimpleName();

    private IReceiver mReceiver;

    public interface IReceiver
    {
        public void onReceiveResult(int resultCode, Bundle resultData) throws SQLException;
    }


    public ResultReceiverWrapper(Handler handler)
    {
        super(handler);
    }

    public void setReceiver(IReceiver receiver)
    {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData)
    {
        if (mReceiver != null)
        {
            try {
                mReceiver.onReceiveResult(resultCode, resultData);
            } catch (SQLException e) {
                e.printStackTrace();
                Log.d(TAG, "we now in the ResultReceierWrapper onReceiveResult");
            }
        }
    }
}
