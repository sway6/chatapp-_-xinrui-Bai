package edu.stevens.cs522.chat.oneway.client.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import edu.stevens.cs522.chat.oneway.client.contracts.ChatroomContract;
import edu.stevens.cs522.chat.oneway.client.contracts.MessageContract;
import edu.stevens.cs522.chat.oneway.client.contracts.PeerContract;

/**
 * Created by baixinrui on 3/8/16.
 */
public class ReceiveContentProvider extends ContentProvider {

    static final String DATABASE_NAME = "serverDB";
    static final String PEER_TABLE = "peer";
    static final String MESSAGE_TABLE = "message";
    static final String CHATROOM_TABLE = "chatroom";
    static final int DATABASE_VERSION = 1;

    public static final int PEERS = 10;
    public static final int PEER_ID = 20;
    public static final int MESSAGES = 30;
    public static final int MESSAGES_ID = 40;
    public static final int ROOMMESSAGES = 50;
    public static final int ROOMMESSAGES_ID = 60;
    public static final int CHATROOMS = 70;
    public static final int CHATROOM_ID = 80;

    private static final UriMatcher uriMatcher;

    final static public String TAG = ReceiveContentProvider.class.getCanonicalName();

    private SQLiteDatabase db;

    private DatabaseHelper dbHelper;

    static
    {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PeerContract.AUTHORITY , PeerContract.CONTENT_PATH_PEER , PEERS);
        uriMatcher.addURI(PeerContract.AUTHORITY , PeerContract.CONTENT_PATH_PEER + "/#" , PEER_ID);
        uriMatcher.addURI(PeerContract.AUTHORITY , PeerContract.CONTENT_PATH_MESSAGE , MESSAGES);
        uriMatcher.addURI(PeerContract.AUTHORITY , PeerContract.CONTENT_PATH_MESSAGE + "/#" , MESSAGES_ID);
        uriMatcher.addURI(PeerContract.AUTHORITY , PeerContract.CONTENT_PATH_CHATROOMMESSAGE , ROOMMESSAGES);
        uriMatcher.addURI(PeerContract.AUTHORITY , PeerContract.CONTENT_PATH_CHATROOMMESSAGE + "/#" , ROOMMESSAGES_ID);
        uriMatcher.addURI(PeerContract.AUTHORITY , PeerContract.CONTENT_PATH_CHATROOM , CHATROOMS);
        uriMatcher.addURI(PeerContract.AUTHORITY , PeerContract.CONTENT_PATH_CHATROOM + "/#" , CHATROOM_ID);
    }

    static final String CREATE_TABLE_CHATROOM = "CREATE TABLE " + CHATROOM_TABLE + " (" +
            ChatroomContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ChatroomContract.ROOM_NAME + " TEXT NOT NULL);";

    static final String CREATE_TABLE_PEER = "CREATE TABLE " + PEER_TABLE + " (" +
            PeerContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PeerContract.CLIENT_ID + " INTEGER, " +
            PeerContract.CLIENT_ADDRESS + " TEXT, " +
            PeerContract.CLIENT_LATITUDE + " DOUBLE, " +
            PeerContract.CLIENT_LONGITUDE + " DOUBLE, " +
            PeerContract.NAME + " TEXT NOT NULL);";

    static final String CREATE_TABLE_MESSAGE = "CREATE TABLE " + MESSAGE_TABLE + " (" +
            MessageContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MessageContract.MESSAGE + " TEXT NOT NULL, " +
            MessageContract.SENDER + " TEXT NOT NULL, " +
            MessageContract.CHATROOM + " TEXT NOT NULL, " +
            MessageContract.TIME_STAMP + " INTEGER, " +
            MessageContract.MESSAGE_LATITUDE + " DOUBLE, " +
            MessageContract.MESSAGE_LONGITUDE + " DOUBLE, " +
            MessageContract.MESSAGE_ID + " INTEGER, " +
            MessageContract.CHATROOM_FK + " INTEGER, " +
            MessageContract.PEER_FK + " INTEGER); ";

    private static class DatabaseHelper extends SQLiteOpenHelper
    {

        public DatabaseHelper(Context context)
        {
            super(context , DATABASE_NAME , null , DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_TABLE_CHATROOM);
            db.execSQL(CREATE_TABLE_PEER);
            db.execSQL(CREATE_TABLE_MESSAGE);
        }

        public void onUpgrade(SQLiteDatabase db , int oldVersion , int newVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS " + CHATROOM_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + PEER_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_TABLE);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate()
    {
        Context context = getContext();
        SQLiteDatabase.loadLibs(context);
        dbHelper = new DatabaseHelper(context);
        return (db == null)? false:true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,String[] selectionArgs, String sortOrder)
    {
        String k = uri.getQueryParameter("SECURE_KEY");
        char[] databaseKey = null;
        if(k == null)
        {
            throw new IllegalArgumentException("There is no key for content provider");
        }
        else
        {
            databaseKey = k.toCharArray();
        }

        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase(databaseKey);
            switch (uriMatcher.match(uri))
            {
                case CHATROOMS:
                    String roomquery =
                            " SELECT " + CHATROOM_TABLE + "." + ChatroomContract._ID + " AS _id, " +
                                    ChatroomContract.ROOM_NAME + " From " + CHATROOM_TABLE;
                    Cursor roomcursor = db.rawQuery(roomquery , null);
                    roomcursor.setNotificationUri(getContext().getContentResolver(), uri);
                    return roomcursor;

                case CHATROOM_ID:
                    String roomquery2 =
                            " SELECT " + CHATROOM_TABLE + "." + ChatroomContract._ID + " AS _id, " +
                                    ChatroomContract.ROOM_NAME +
                                    " From " + CHATROOM_TABLE +
                                    " where _id = " + uri.getPathSegments().get(1);
                    Cursor roomcursor2 = db.rawQuery(roomquery2 , null);
                    roomcursor2.setNotificationUri(getContext().getContentResolver(), uri);
                    return roomcursor2;

                case PEERS:
                    String rawquery =
                            " SELECT " + PEER_TABLE + "." + PeerContract._ID + " AS _id, " +
                                    PeerContract.CLIENT_ID + " , " +
                                    PeerContract.NAME + " , " + PeerContract.CLIENT_ADDRESS + " , " +
                                    PeerContract.CLIENT_LATITUDE + " , " + PeerContract.CLIENT_LONGITUDE +
                                    " From " + PEER_TABLE ;

                    Cursor c = db.rawQuery(rawquery , null);
                    c.setNotificationUri(getContext().getContentResolver(), uri);
                    return c;

                case PEER_ID:
                    String rawquery2 =
                            " SELECT " + PEER_TABLE + "." + PeerContract._ID + " AS _id, " +
                                    PeerContract.CLIENT_ID + " , " + PeerContract.CLIENT_ADDRESS + " , " +
                                    PeerContract.CLIENT_LATITUDE + " , " + PeerContract.CLIENT_LONGITUDE +
                                    PeerContract.NAME + " From " + PEER_TABLE +
                                    " where chatroom_fk = " + uri.getPathSegments().get(1);
                    Cursor c1 = db.rawQuery(rawquery2 , null);
                    c1.setNotificationUri(getContext().getContentResolver(), uri);
                    return c1;

                case MESSAGES:
                    String messagerawquery =
                            " SELECT " + MESSAGE_TABLE + "." + MessageContract._ID + " AS _id, " +
                                    MessageContract.MESSAGE + " , " + MessageContract.SENDER + " , " +
                                    MessageContract.TIME_STAMP + " , " + MessageContract.MESSAGE_ID + " , " +
                                    MessageContract.CHATROOM + " , " + MessageContract.PEER_FK + " , " +
                                    MessageContract.CHATROOM_FK + " , " + MessageContract.MESSAGE_LATITUDE + " , " +
                                    MessageContract.MESSAGE_LONGITUDE + " From " + MESSAGE_TABLE;
                    Cursor cmessage = db.rawQuery(messagerawquery , null);
                    cmessage.setNotificationUri(getContext().getContentResolver() , uri);
                    return cmessage;

                case MESSAGES_ID:
                    String messagerawquery2 =
                            " SELECT " + CHATROOM_TABLE + "." + ChatroomContract._ID + " AS _id, " +
                                    ChatroomContract.ROOM_NAME + " , " + MessageContract.MESSAGE + " , " +
                                    MessageContract.SENDER + " , " + MessageContract.TIME_STAMP + " , " +
                                    MessageContract.CHATROOM + " , " + MessageContract.MESSAGE_LATITUDE + " , " +
                                    MessageContract.MESSAGE_LONGITUDE +
                                    " From " + CHATROOM_TABLE + " INNER JOIN " + MESSAGE_TABLE +
                                    " on " + "_id" + " = " + MessageContract.CHATROOM_FK +
                                    " where chatroom_fk = " + uri.getPathSegments().get(1);
                    Cursor c2 = db.rawQuery(messagerawquery2 , null);
                    c2.setNotificationUri(getContext().getContentResolver(), uri);
                    return c2;

                case ROOMMESSAGES:
                    String roommesquery = "";
                    Cursor roommescursor = db.rawQuery(roommesquery , null);
                    roommescursor.setNotificationUri(getContext().getContentResolver(), uri);
                    return roommescursor;

                case ROOMMESSAGES_ID:
                    String roommesquery2 =
                            " SELECT " + PEER_TABLE + "." + PeerContract._ID + " AS _id, " +
                                    PeerContract.NAME + " , " + PeerContract.CLIENT_ID + " , " +
                                    PeerContract.CLIENT_ADDRESS + " , " +
                                    MessageContract.MESSAGE + " , " + MessageContract.CHATROOM + " , " +
                                    MessageContract.TIME_STAMP + " , " + MessageContract.SENDER + " , " +
                                    MessageContract.MESSAGE_LATITUDE + " , " + MessageContract.MESSAGE_LONGITUDE +
                                    " From " + PEER_TABLE + " INNER JOIN " + MESSAGE_TABLE +
                                    " on " + PeerContract.CLIENT_ID + " = " + MessageContract.PEER_FK +
                                    " where peer_fk = " + uri.getPathSegments().get(1);
                    Cursor roommescursor2 = db.rawQuery(roommesquery2 , null);
                    roommescursor2.setNotificationUri(getContext().getContentResolver(), uri);

                    return roommescursor2;

                default:
                    Log.d(TAG, "Unknown URI: " + uri);
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }

        } finally {
            for (int i = 0; i < databaseKey.length; i++) {
                databaseKey[i] = ' ';
            }
        }

    }

    @Override
    public String getType (Uri _uri) {
        switch (uriMatcher.match(_uri))
        {
            case CHATROOMS:
                return "vnd.android.cursor.dir/vnd.cs522chat.chatrooms";
            case CHATROOM_ID:
                return "vnd.android.cursor.dir/vnd.cs522chat.chatrooms";
            case PEERS:
                return "vnd.android.cursor.dir/vnd.cs522chat.peers";
            case PEER_ID:
                return "vnd.android.cursor.item/vnd.cs522chat.peers";
            case MESSAGES:
                return "vnd.android.cursor.dir/vnd.cs522chat.messages";
            case MESSAGES_ID:
                return "vnd.android.cursor.item/vnd.cs522chat.messages";
            default: throw new IllegalArgumentException("Unsupported URI: " + _uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) throws SQLException
    {
        String k = uri.getQueryParameter("SECURE_KEY");
        char[] databaseKey = null;
        if(k == null)
        {
            throw new IllegalArgumentException("There is no key for content provider");
        }
        else
        {
            databaseKey = k.toCharArray();
        }
        try {
            SQLiteDatabase db =
                    dbHelper.getWritableDatabase(databaseKey);
            Uri _uri = null;
            switch (uriMatcher.match(uri))
            {
                case CHATROOMS:
                    long _ID1 = db.insert(CHATROOM_TABLE, "", values);
                    //---if added successfully---
                    if (_ID1 > 0) {
                        _uri = ContentUris.withAppendedId(PeerContract.CONTENT_URI3, _ID1);
                        getContext().getContentResolver().notifyChange(_uri, null);
                    }
                    break;

                case PEERS:
                    long _ID2 = db.insert(PEER_TABLE, "", values);
                    //---if added successfully---
                    if (_ID2 > 0) {
                        _uri = ContentUris.withAppendedId(PeerContract.CONTENT_URI1, _ID2);
                        getContext().getContentResolver().notifyChange(_uri, null);
                    }
                    break;

                case MESSAGES:
                    long _ID3 = db.insert(MESSAGE_TABLE, "", values);
                    //---if added successfully---
                    if (_ID3 > 0) {
                        _uri = ContentUris.withAppendedId(PeerContract.CONTENT_URI2, _ID3);
                        getContext().getContentResolver().notifyChange(_uri, null);
                    }
                    break;

                default: throw new SQLException("Failed to insert row into " + uri);
            }
            return _uri;

        } finally {
            for (int i=0; i<databaseKey.length; i++) {
                databaseKey[i] = ' ';
            }
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        String k = uri.getQueryParameter("SECURE_KEY");
        char[] databaseKey = null;
        if(k == null)
        {
            throw new IllegalArgumentException("There is no key for content provider");
        }
        else
        {
            databaseKey = k.toCharArray();
        }

        try {
            SQLiteDatabase db =
                    dbHelper.getReadableDatabase(databaseKey);

            int count = 0;

            switch (uriMatcher.match(uri))
            {
                case CHATROOMS:
                    count = db.delete(CHATROOM_TABLE, selection, selectionArgs);
                    break;

                case CHATROOM_ID:
                    String chatroomid = uri.getPathSegments().get(1);
                    db.delete(MESSAGE_TABLE , MessageContract.CHATROOM_FK  + " = " + chatroomid , null);
                    count = db.delete( CHATROOM_TABLE, ChatroomContract._ID +  " = " + chatroomid +
                            (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                    break;

                case PEERS:
                    count = db.delete(PEER_TABLE, selection, selectionArgs);
                    break;

                case PEER_ID:
                    String peerid = uri.getPathSegments().get(1);
                    db.delete(MESSAGE_TABLE , MessageContract.PEER_FK  + "=" + peerid , null);
                    count = db.delete( PEER_TABLE, PeerContract._ID +  " = " + peerid +
                            (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                    break;

                case MESSAGES:
                    count = db.delete( MESSAGE_TABLE, MessageContract.MESSAGE_ID +  " = " + "0" +
                            (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                    break;

                case MESSAGES_ID:
                    String messageid = uri.getPathSegments().get(1);
                    count = db.delete( MESSAGE_TABLE, MessageContract.MESSAGE_ID +  " = " + messageid +
                            (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                    break;

                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }

            getContext().getContentResolver().notifyChange(uri, null);
            return count;

        } finally {
            for (int i=0; i<databaseKey.length; i++) {
                databaseKey[i] = ' '; }
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        int count = 0;

        switch (uriMatcher.match(uri))
        {
            case CHATROOMS:
                count = db.update(CHATROOM_TABLE, values, selection, selectionArgs);
                break;

            case CHATROOM_ID:
                count = db.update(CHATROOM_TABLE, values, ChatroomContract._ID + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);

            case PEERS:
                count = db.update(PEER_TABLE, values, selection, selectionArgs);
                break;

            case PEER_ID:
                count = db.update(PEER_TABLE, values, PeerContract._ID + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);

            case MESSAGES:
                count = db.update(MESSAGE_TABLE, values, selection, selectionArgs);
                break;

            case MESSAGES_ID:
                count = db.update(MESSAGE_TABLE, values, MessageContract._ID + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
