package edu.stevens.cs522.chat.oneway.client.builders;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import edu.stevens.cs522.chat.oneway.client.callbacks.IEntityCreator;
import edu.stevens.cs522.chat.oneway.client.callbacks.IQueryListener;
import edu.stevens.cs522.chat.oneway.client.contracts.MessageContract;
import edu.stevens.cs522.chat.oneway.client.managers.TypedCursor;

/**
 * Created by baixinrui on 3/8/16.
 */
public class QueryBuilder<T> implements LoaderManager.LoaderCallbacks<Cursor> {

    private IEntityCreator<T> creator;
    private IQueryListener<T> listener;
    private String tag;
    private Context context;
    private Uri uri;
    private int loaderID;

    private static final String[] projection = new String[]
            {MessageContract.MESSAGE , MessageContract.SENDER , MessageContract.CHATROOM , MessageContract.TIME_STAMP,
            MessageContract.MESSAGE_LATITUDE , MessageContract.MESSAGE_LONGITUDE};//may be some mistake;

    private QueryBuilder(String tag , Context context , Uri uri ,
                         int loaderID , IEntityCreator<T> creator, IQueryListener<T> listener)
    {
        this.tag = tag;
        this.context = context;
        this.uri = uri;
        this.loaderID = loaderID;
        this.creator = creator;
        this.listener = listener;

    }

    public static <T> void executeQuery(String tag, Activity context, Uri uri, int loaderID,
                                        IEntityCreator<T> creator, IQueryListener<T> listener)
    {

        QueryBuilder<T> qb = new QueryBuilder<T>(tag, context, uri, loaderID, creator, listener);
        LoaderManager lm = context.getLoaderManager();
        lm.initLoader(loaderID, null, qb);
        lm.restartLoader(loaderID, null, qb);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        if (id == loaderID)
        {
            return new CursorLoader(context, uri, projection, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        if (loader.getId() == loaderID)
        {
            listener.handleResults(new TypedCursor(cursor, creator));
        }
        else
        {
            throw new IllegalStateException("Unexpected loader callback");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        if (loader.getId() == loaderID)
        {
            listener.closeResults();
        } else
        {
            throw new IllegalStateException ("Unexpected loader callback");
        }
    }
}
