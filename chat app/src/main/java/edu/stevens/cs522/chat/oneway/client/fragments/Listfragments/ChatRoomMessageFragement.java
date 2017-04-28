package edu.stevens.cs522.chat.oneway.client.fragments.Listfragments;

import android.app.ListFragment;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.stevens.cs522.chat.oneway.client.callbacks.IEntityCreator;
import edu.stevens.cs522.chat.oneway.client.callbacks.IQueryListener;
import edu.stevens.cs522.chat.oneway.client.contracts.PeerContract;
import edu.stevens.cs522.chat.oneway.client.cursoradapter.MyCursorAdapter;
import edu.stevens.cs522.chat.oneway.client.entities.Message;
import edu.stevens.cs522.chat.oneway.client.managers.MessageManager;
import edu.stevens.cs522.chat.oneway.client.managers.TypedCursor;

/**
 * Created by baixinrui on 3/29/16.
 */
public class ChatRoomMessageFragement extends ListFragment
{
    final static private String TAG = ChatRoomMessageFragement.class.getSimpleName();
    private MessageManager messageManager;
    private static final int Message_LOADER_ID = 101;
    private MyCursorAdapter myadapter;
    private String securekey;

    public static ChatRoomMessageFragement newInstance(int index)
    {
        ChatRoomMessageFragement f = new ChatRoomMessageFragement();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }

    public int getShownIndex()
    {
        return getArguments().getInt("index", 0);
    }

    public void onActivityCreated(Bundle savedInstanceState)//using uri3+id to query for messages belongs to sepcific chatroom
    {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "ChatRoomMessageFragement onActivityCreated is accessed successfully");

        int fk = getShownIndex();
        securekey = getArguments().getString("SECURE_KRY");//maybe some mistack, there are two bundles in the arguement.
        String t = Integer.toString(fk);
        Log.d(TAG, "ChatRoom message fragment: fk: " + fk + " key: " + securekey );
        Uri messages_uri = PeerContract.withExtendedPath(PeerContract.CONTENT_URI2, t);
        Uri messages_urikey = PeerContract.uriwithkey(messages_uri , securekey);

        Log.d(TAG, "ChatRoomMessageFragement index to query the messages is " + t);

        messageManager = new MessageManager(getActivity(), new IEntityCreator<Message>() {
            @Override
            public Message create(Cursor cursor) {
                Message instance = new Message(cursor);
                return instance;
            }
        },Message_LOADER_ID , securekey);

        messageManager.executeAsyncQuery(messages_urikey, new IQueryListener<Message>() {
            @Override
            public void handleResults(TypedCursor<Message> message) {
                List<Message> instances = new ArrayList<Message>();
                Cursor cursor = message.getCursor();
                if (message.moveToFirst()) {
                    myadapter = new MyCursorAdapter(
                            getActivity(),
                            cursor,
                            0);
                    setListAdapter(myadapter);
                }
                else
                {
                    myadapter = new MyCursorAdapter(
                            getActivity(),
                            cursor,
                            0);
                    setListAdapter(myadapter);
                }
            }


            @Override
            public void closeResults() {

            }
        });

    }
}
