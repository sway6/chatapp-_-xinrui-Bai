package edu.stevens.cs522.chat.oneway.client.managers;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import edu.stevens.cs522.chat.oneway.client.callbacks.IContinue;
import edu.stevens.cs522.chat.oneway.client.callbacks.IEntityCreator;
import edu.stevens.cs522.chat.oneway.client.callbacks.IQueryListener;
import edu.stevens.cs522.chat.oneway.client.callbacks.ISimpleQueryListener;
import edu.stevens.cs522.chat.oneway.client.contracts.PeerContract;
import edu.stevens.cs522.chat.oneway.client.entities.Message;
import edu.stevens.cs522.chat.oneway.client.handlers.AsyncContentResolver;

/**
 * Created by baixinrui on 3/8/16.
 */
public class MessageManager extends Manager<Message>
{
    private AsyncContentResolver asyncResolver = getAsyncResolver();
    private String key;

    public MessageManager(Context context, IEntityCreator<Message> creator, int loaderID , String key)
    {
        super(context, creator, loaderID);
        this.key = key;
    }

    public void executeQuery(Uri uri , ISimpleQueryListener<Message> listener)
    {
        executeSimpleQuery(uri, listener);
    }

    public void executeAsyncQuery(Uri uri , IQueryListener<Message> listener)
    {
        executeQuery(uri, listener);
    }

    public void deletingAsync(Uri uri , String selection , String[] selectionArgs)
    {
        asyncResolver.deleteAsync(uri, selection, selectionArgs);
    }

    public void persistMessageAsync(final Message message)
    {
        ContentValues messagevalues = new ContentValues();
        message.writeToProvider(messagevalues);
        Uri messages_key = PeerContract.uriwithkey(PeerContract.CONTENT_URI2 , key);
        asyncResolver.insertAsync(messages_key, messagevalues, new IContinue<Uri>()
        {
            @Override
            public void kontinue(Uri value) {

            }
        });
    }
}
