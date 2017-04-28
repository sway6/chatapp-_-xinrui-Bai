package edu.stevens.cs522.chat.oneway.client.fragments.Listfragments;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.stevens.cs522.chat.oneway.client.R;
import edu.stevens.cs522.chat.oneway.client.activities.ClientsActivity;
import edu.stevens.cs522.chat.oneway.client.activities.ClientsmesActivity;
import edu.stevens.cs522.chat.oneway.client.callbacks.IEntityCreator;
import edu.stevens.cs522.chat.oneway.client.callbacks.IQueryListener;
import edu.stevens.cs522.chat.oneway.client.contracts.PeerContract;
import edu.stevens.cs522.chat.oneway.client.entities.Peer;
import edu.stevens.cs522.chat.oneway.client.managers.PeerManager;
import edu.stevens.cs522.chat.oneway.client.managers.TypedCursor;

/**
 * Created by baixinrui on 3/29/16.
 */
public class ClientFragement extends ListFragment
{
    final static private String TAG = ClientFragement.class.getSimpleName();
    private boolean mDualPane;
    private int mCurCheckPosition = 0;
    private SimpleCursorAdapter adapter;
    private static final int PEERS_LOADER_ID = 2;
    private PeerManager peerManager;
    private String securekey;

    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "ClientFragement onActivityCreated accessed successfully!");

        securekey = ((ClientsActivity)getActivity()).getKey();

        peerManager = new PeerManager(getActivity(), new IEntityCreator<Peer>()
        {
            @Override
            public Peer create(Cursor cursor)
            {
                Peer instance = new Peer(cursor);
                return instance;
            }
        } , PEERS_LOADER_ID , securekey);

        String[] from = new String[] {PeerContract.NAME , PeerContract.CLIENT_ADDRESS};
        int[] to = new int[]{android.R.id.text1 , android.R.id.text2};
        adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_2, null , from , to , 0);

//        Uri peer_uri = PeerContract.CONTENT_URI1;

        Uri peer_uri = PeerContract.uriwithkey(PeerContract.CONTENT_URI1 , securekey);

        peerManager.executeAsyncQuery(peer_uri, new IQueryListener<Peer>()
        {
            @Override
            public void handleResults(TypedCursor<Peer> peer)
            {
                List<Peer> instances = new ArrayList<Peer>();
                if (peer.moveToFirst())
                {
                    adapter.swapCursor(peer.getCursor());
                }
            }

            @Override
            public void closeResults() {

            }
        });
        setListAdapter(adapter);

        View messagesFrame = getActivity().findViewById(R.id.client_messages);
        mDualPane = messagesFrame != null && messagesFrame.getVisibility() == View.VISIBLE;

        if (savedInstanceState != null)
        {
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }

        if (mDualPane)
        {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            showDetails(mCurCheckPosition);
        }
    }

    public void onListItemClick(ListView l, View v, int position, long id)
    {
        Toast.makeText(getActivity(), "onListItemClick position is" + position, Toast.LENGTH_LONG).show();
        showDetails(position+1);
    }

    public void showDetails(int index)
    {
        mCurCheckPosition = index;

        if (mDualPane)
        {
            getListView().setItemChecked(index, true);
            ClientMessageFragement clientmessages = (ClientMessageFragement)
                    getFragmentManager().findFragmentById(R.id.client_messages);
            if (clientmessages == null || clientmessages.getShownIndex() != index)
            {
                clientmessages = ClientMessageFragement.newInstance(index);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.client_messages, clientmessages);

                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

        }
        else
        {
            Intent intent = new Intent(getActivity() , ClientsmesActivity.class);
            intent.putExtra("index", index);
            intent.putExtra("SECURE_KRY" , securekey);
            startActivity(intent);
        }
    }
}
