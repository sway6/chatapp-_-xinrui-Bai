package edu.stevens.cs522.chat.oneway.client.fragments.Listfragments;

import android.app.Activity;
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
import edu.stevens.cs522.chat.oneway.client.activities.ChatmesActivity;
import edu.stevens.cs522.chat.oneway.client.activities.ChatroomActivity;
import edu.stevens.cs522.chat.oneway.client.callbacks.IChatRoomFragmentListener;
import edu.stevens.cs522.chat.oneway.client.callbacks.IEntityCreator;
import edu.stevens.cs522.chat.oneway.client.callbacks.IQueryListener;
import edu.stevens.cs522.chat.oneway.client.callbacks.ISimpleQueryListener;
import edu.stevens.cs522.chat.oneway.client.contracts.ChatroomContract;
import edu.stevens.cs522.chat.oneway.client.contracts.PeerContract;
import edu.stevens.cs522.chat.oneway.client.entities.ChatRoom;
import edu.stevens.cs522.chat.oneway.client.managers.ChatroomManager;
import edu.stevens.cs522.chat.oneway.client.managers.TypedCursor;

/**
 * Created by baixinrui on 3/29/16.
 */
public class ChatRoomFragement extends ListFragment
{
    final static private String TAG = ChatRoomFragement.class.getSimpleName();
    private boolean mDualPane;
    private int mCurCheckPosition = 0;
    private static final int CHATROOM_LOADER_ID = 5;
    private SimpleCursorAdapter adapter;
    private ChatroomManager chatroomManager;
    private IChatRoomFragmentListener roomidcallback;
    private String securitykey;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        try 
        {
            roomidcallback = (IChatRoomFragmentListener) activity;
        } 
        catch (ClassCastException e) 
        {
            throw new ClassCastException(activity.toString()
                    + " must implement IChatRoomFragmentListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "ChatRoomFragement has been called successfully");
        securitykey = ((ChatroomActivity)getActivity()).getkey();

        chatroomManager = new ChatroomManager(getActivity(), new IEntityCreator<ChatRoom>() {
            @Override
            public ChatRoom create(Cursor cursor) {
                ChatRoom instance = new ChatRoom(cursor);
                return instance;
            }
        }, CHATROOM_LOADER_ID , securitykey);

        String[] from = new String[] {ChatroomContract.ROOM_NAME};
        int[] to = new int[]{android.R.id.text1};
        adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, null , from , to , 0);

        Log.d(TAG, "chatroom fragment key is " + securitykey);

        chatroomManager.executeAsyncQuery(PeerContract.uriwithkey(PeerContract.CONTENT_URI3 , securitykey), new IQueryListener<ChatRoom>() {
            @Override
            public void handleResults(TypedCursor<ChatRoom> chatroom) {
                List<ChatRoom> instances = new ArrayList<ChatRoom>();
                if (chatroom.moveToFirst()) {
                    adapter.swapCursor(chatroom.getCursor());
                }
            }

            @Override
            public void closeResults() {

            }
        });

        setListAdapter(adapter);

        View messagesFrame = getActivity().findViewById(R.id.chatroom_messages);
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
        int temp = position + 1;
        Toast.makeText(getActivity(), "onListItemClick position is" + temp, Toast.LENGTH_LONG).show();
        String t = Integer.toString(position + 1);
        Uri chatroom_uri = PeerContract.withExtendedPath(PeerContract.CONTENT_URI3, t);

        Uri chatroom_urikey = PeerContract.uriwithkey(chatroom_uri , securitykey);

        chatroomManager = new ChatroomManager(getActivity(), new IEntityCreator<ChatRoom>()
        {
            @Override
            public ChatRoom create(Cursor cursor)
            {
                ChatRoom instance = new ChatRoom(cursor);
                return instance;
            }
        }, CHATROOM_LOADER_ID , securitykey);

        chatroomManager.executeQuery(chatroom_urikey, new ISimpleQueryListener<ChatRoom>() //get the chatroom name and callback it to the Chatroom Activity
        {
            @Override
            public void handleResults(List<ChatRoom> results)
            {
                if(results.size() != 0)
                {
                    String chatname = results.get(0).roomname;
                    roomidcallback.getroomname(chatname);//query the chatroom name using the position you click and callback to chatroomActivity
                }
            }
        });

        showDetails(position + 1);
    }

    public void showDetails(int index)
    {
        mCurCheckPosition = index;

        roomidcallback.getroomid(index);//send roomid to chatroomActivity;

        if (mDualPane)
        {
            getListView().setItemChecked(index, true);
            ChatRoomMessageFragement roommessages = (ChatRoomMessageFragement)
                    getFragmentManager().findFragmentById(R.id.chatroom_messages);
            if (roommessages == null || roommessages.getShownIndex() != index)
            {
                roommessages = ChatRoomMessageFragement.newInstance(index);
                Bundle bundle = roommessages.getArguments();
                bundle.putString("SECURE_KRY", securitykey);
                roommessages.setArguments(bundle);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.chatroom_messages, roommessages);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

        }
        else
        {
            Intent intent = new Intent(getActivity() , ChatmesActivity.class);
            intent.putExtra("index", index);
            intent.putExtra("SECURE_KRY" , securitykey);
            startActivity(intent);
        }
    }
}

