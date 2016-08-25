package edu.stevens.cs522.chat.oneway.client.builders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import edu.stevens.cs522.chat.oneway.client.callbacks.IContinue;
import edu.stevens.cs522.chat.oneway.client.callbacks.IEntityCreator;
import edu.stevens.cs522.chat.oneway.client.callbacks.ISimpleQueryListener;
import edu.stevens.cs522.chat.oneway.client.handlers.AsyncContentResolver;

/**
 * Created by baixinrui on 3/8/16.
 */
public class SimpleQueryBuilder<T> implements IContinue<Cursor> {

    private IEntityCreator<T> helper;

    private ISimpleQueryListener<T> listener;

    private SimpleQueryBuilder(IEntityCreator<T> helper, ISimpleQueryListener<T> listener )
    {

        this.helper = helper;
        this.listener = listener;
    }

    public static <T> void executeQuery(Context context, Uri uri , IEntityCreator<T> helper,
                                        ISimpleQueryListener<T> listener)
    {

        SimpleQueryBuilder<T> qb = new SimpleQueryBuilder<T>(helper, listener);
        AsyncContentResolver resolver = new AsyncContentResolver(context.getContentResolver());
        resolver.queryAsync(uri, null, null, null, null, qb);

    }
    @Override
    public void kontinue(Cursor cursor)
    {
        List<T> instances = new ArrayList<T>();
        if (cursor.moveToFirst())
        {
            do {
                T instance = helper.create(cursor);
                instances.add(instance);
            } while (cursor.moveToNext());
        }
        cursor.close();
        listener.handleResults(instances);
    }

}
