package edu.stevens.cs522.chat.oneway.client.handlers;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import edu.stevens.cs522.chat.oneway.client.callbacks.IContinue;

/**
 * Created by baixinrui on 3/8/16.
 */
public class AsyncContentResolver extends AsyncQueryHandler
{

    public AsyncContentResolver(ContentResolver cr) {
        super(cr);
    }

    public void queryAsync(Uri uri, String[] projection , String selection , String[] selectionArgs ,
                           String sortOrder , IContinue<Cursor> callback)
    {
        this.startQuery(0, callback, uri, projection, selection, selectionArgs, sortOrder);
    }

    public void insertAsync(Uri uri , ContentValues values , IContinue<Uri> callback)
    {
        this.startInsert(0, callback, uri, values);
    }

    public void updateAsync(Uri uri, ContentValues values , String selection , String[] selectionArgs)
    {
        this.startUpdate(0, null, uri, values, selection, selectionArgs);
    }

    public void deleteAsync(Uri uri, String selection , String[] selectionArgs)
    {
        this.startDelete(0, null, uri, selection, selectionArgs);
    }

    @Override
    public void onQueryComplete(int token, Object cookie, Cursor c)
    {
        if (cookie != null) {
            @SuppressWarnings("unchecked")
            IContinue<Cursor> callback = (IContinue<Cursor>) cookie;
            callback.kontinue(c);
        }
    }

    @Override
    public void onInsertComplete(int token, Object cookie, Uri uri)
    {
        if (cookie != null)
        {
            @SuppressWarnings("unchecked")
            IContinue<Uri> callback = (IContinue<Uri>) cookie;
            callback.kontinue(uri);
        }
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result)
    {
        // update() completed
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result)
    {
        // delete() completed
    }
}
