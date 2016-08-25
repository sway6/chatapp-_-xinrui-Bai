package edu.stevens.cs522.chat.oneway.client.callbacks;

import java.util.List;

/**
 * Created by baixinrui on 3/8/16.
 */
public interface ISimpleQueryListener<T>
{
    public void handleResults(List<T> results);

}