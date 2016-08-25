package edu.stevens.cs522.chat.oneway.client.managers;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import edu.stevens.cs522.chat.oneway.client.callbacks.IContinue;
import edu.stevens.cs522.chat.oneway.client.callbacks.IEntityCreator;
import edu.stevens.cs522.chat.oneway.client.callbacks.IQueryListener;
import edu.stevens.cs522.chat.oneway.client.callbacks.ISimpleQueryListener;
import edu.stevens.cs522.chat.oneway.client.contracts.PeerContract;
import edu.stevens.cs522.chat.oneway.client.entities.ChatRoom;
import edu.stevens.cs522.chat.oneway.client.handlers.AsyncContentResolver;

/**
 * Created by baixinrui on 3/29/16.
 */
public class ChatroomManager extends Manager<ChatRoom>
{
    private String key;
    private AsyncContentResolver asyncResolver = getAsyncResolver();

    public ChatroomManager(Context context, IEntityCreator<ChatRoom> creator, int loaderID , String key)
    {
        super(context, creator, loaderID);
        this.key = key;
    }

    public void executeQuery(Uri uri , ISimpleQueryListener<ChatRoom> listener)
    {
        executeSimpleQuery(uri, listener);
    }

    public void executeAsyncQuery(Uri uri , IQueryListener<ChatRoom> listener)
    {
        executeQuery(uri, listener);
    }

    public void deletingAsync(Uri uri , String selection , String[] selectionArgs)
    {
        asyncResolver.deleteAsync(uri, selection, selectionArgs);
    }

    public void  persistChatroomAsync(final ChatRoom chatRoom)
    {
        ContentValues chatroomvalues = new ContentValues();
        chatRoom.writeToProvider(chatroomvalues);
        Uri chatroom_key = PeerContract.uriwithkey(PeerContract.CONTENT_URI3 , key);
        asyncResolver.insertAsync(chatroom_key, chatroomvalues, new IContinue<Uri>()
        {
            @Override
            public void kontinue(Uri value)
            {

            }
        });

    }

}

