package edu.stevens.cs522.chat.oneway.client.fragments.Listfragments;

import android.app.ListFragment;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

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
public class ClientMessageFragement extends ListFragment
{
    private MessageManager messageManager;
    private static final int Message_LOADER_ID = 6;
    private SimpleCursorAdapter adapter;
    private MyCursorAdapter myadapter;
    private String secureKey;

    public static ClientMessageFragement newInstance(int index)
    {
        ClientMessageFragement f = new ClientMessageFragement();

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


    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        int fk = getShownIndex();//query for the messages with the getArguments() index;
        secureKey = getArguments().getString("SECURE_KEY");
        String t = Integer.toString(fk);

        Uri messages_uri = PeerContract.withExtendedPath(PeerContract.CONTENT_URI4, t);

        Uri messages_urikey = PeerContract.uriwithkey(messages_uri , secureKey);

        messageManager = new MessageManager(getActivity(), new IEntityCreator()
        {
            @Override
            public Object create(Cursor cursor)
            {
                Message instance = new Message(cursor);
                return instance;
            }
        } , Message_LOADER_ID , secureKey);

        messageManager.executeAsyncQuery(messages_urikey, new IQueryListener<Message>()
        {
            @Override
            public void handleResults(TypedCursor<Message> message)
            {
                List<Message> instances = new ArrayList<Message>();
                Cursor cursor = message.getCursor();
                if (cursor.moveToFirst())
                {
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
            public void closeResults()
            {

            }
        });
    }
}
