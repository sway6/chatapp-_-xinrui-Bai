package edu.stevens.cs522.chat.oneway.client.callbacks;

import edu.stevens.cs522.chat.oneway.client.managers.TypedCursor;

/**
 * Created by baixinrui on 3/8/16.
 */
public interface IQueryListener<T>
{
    public void handleResults(TypedCursor<T> results);

    public void closeResults();

}
