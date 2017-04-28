package edu.stevens.cs522.chat.oneway.client.managers;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import edu.stevens.cs522.chat.oneway.client.builders.QueryBuilder;
import edu.stevens.cs522.chat.oneway.client.builders.SimpleQueryBuilder;
import edu.stevens.cs522.chat.oneway.client.callbacks.IEntityCreator;
import edu.stevens.cs522.chat.oneway.client.callbacks.IQueryListener;
import edu.stevens.cs522.chat.oneway.client.callbacks.ISimpleQueryListener;
import edu.stevens.cs522.chat.oneway.client.handlers.AsyncContentResolver;

/**
 * Created by baixinrui on 3/8/16.
 */
public abstract class Manager<T> {

    private final Context context;

    private final IEntityCreator<T> creator;

    private final int loaderID;

    private final String tag;

    private ContentResolver syncResolver;

    private AsyncContentResolver asyncResolver;

    protected Manager(Context context, IEntityCreator<T> creator, int loaderID)
    {
        this.context = context;
        this.creator = creator;
        this.loaderID = loaderID;
        this.tag = this.getClass().getCanonicalName();
    }

    protected ContentResolver getSyncResolver()
    {
        if (syncResolver == null)
            syncResolver = context.getContentResolver();
        return syncResolver;
    }

    protected AsyncContentResolver getAsyncResolver()
    {
        if (asyncResolver == null)
            asyncResolver = new AsyncContentResolver(context.getContentResolver());
        return asyncResolver;
    }


    protected void executeSimpleQuery(Uri uri, ISimpleQueryListener<T> listener)
    {

        SimpleQueryBuilder.executeQuery((Activity) context, uri, creator, (ISimpleQueryListener<T>) listener);

    }

    protected void executeQuery(Uri uri, IQueryListener<T> listener)
    {

        QueryBuilder.executeQuery(tag, (Activity) context, uri, loaderID, creator, listener);

    }
}
