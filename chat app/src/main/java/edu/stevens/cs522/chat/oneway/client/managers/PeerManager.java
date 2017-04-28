package edu.stevens.cs522.chat.oneway.client.managers;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import edu.stevens.cs522.chat.oneway.client.callbacks.IContinue;
import edu.stevens.cs522.chat.oneway.client.callbacks.IEntityCreator;
import edu.stevens.cs522.chat.oneway.client.callbacks.IQueryListener;
import edu.stevens.cs522.chat.oneway.client.callbacks.ISimpleQueryListener;
import edu.stevens.cs522.chat.oneway.client.contracts.MessageContract;
import edu.stevens.cs522.chat.oneway.client.contracts.PeerContract;
import edu.stevens.cs522.chat.oneway.client.entities.Message;
import edu.stevens.cs522.chat.oneway.client.entities.Peer;
import edu.stevens.cs522.chat.oneway.client.handlers.AsyncContentResolver;

/**
 * Created by baixinrui on 3/8/16.
 */
public class PeerManager extends Manager<Peer>
{

    private AsyncContentResolver asyncResolver = getAsyncResolver();

    private String key;

    final static Uri CONTENT_URI = PeerContract.CONTENT_URI1;

    public PeerManager(Context context, IEntityCreator<Peer> creator, int loaderID , String key)
    {
        super(context, creator, loaderID);
        this.key = key;
    }

    public void persistpeer(final Peer peer)
    {
        ContentValues values = new ContentValues();
        peer.writeToProvider(values);
        Uri peers_key = PeerContract.uriwithkey(CONTENT_URI , key);
        asyncResolver.insertAsync(peers_key, values, new IContinue<Uri>() {
            @Override
            public void kontinue(Uri value) {

            }
        });
    }

    public void persistAsync(final Peer peer , final Message message)
    {
        ContentValues values = new ContentValues();
        peer.writeToProvider(values);
        Uri peers_key = PeerContract.uriwithkey(CONTENT_URI , key);
        final Uri messages_key = PeerContract.uriwithkey(PeerContract.CONTENT_URI2 , key);
        asyncResolver.insertAsync(peers_key, values, new IContinue<Uri>()
        {
            @Override
            public void kontinue(Uri uri)
            {
                long key = Long.valueOf(uri.getLastPathSegment());
                ContentValues messagevalues = new ContentValues();
                message.writeToProvider(messagevalues);
                messagevalues.put(MessageContract.PEER_FK, key);
                asyncResolver.insertAsync(messages_key, messagevalues, new IContinue<Uri>()
                {
                    @Override
                    public void kontinue(Uri value) {

                    }
                });
            }
        });

    }

    public void persistMessageAsync(final Message message , int key)
    {
        ContentValues messagevalues = new ContentValues();
        message.writeToProvider(messagevalues);
        messagevalues.put(MessageContract.PEER_FK, key);
        Uri messages_key = PeerContract.uriwithkey(PeerContract.CONTENT_URI2 , this.key);
        asyncResolver.insertAsync(messages_key, messagevalues, new IContinue<Uri>()
        {
            @Override
            public void kontinue(Uri value) {

            }
        });
    }

    public void executeQuery(Uri uri , ISimpleQueryListener<Peer> listener)
    {
        executeSimpleQuery(uri, listener);
    }

    public void executeAsyncQuery(Uri uri , IQueryListener<Peer> listener)
    {
        executeQuery(uri , listener);
    }

    public void deletingAsync(Uri uri , String selection , String[] selectionArgs)
    {
        asyncResolver.deleteAsync(uri , selection , selectionArgs);
    }
}
