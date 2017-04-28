package edu.stevens.cs522.chat.oneway.client.httpresponses;

import android.os.Parcelable;

import java.io.IOException;

/**
 * Created by baixinrui on 3/8/16.
 */
public abstract class Response implements Parcelable
{
    public abstract boolean isValid() throws IOException;
}
