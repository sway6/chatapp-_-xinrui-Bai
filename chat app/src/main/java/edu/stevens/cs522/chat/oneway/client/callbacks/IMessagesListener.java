package edu.stevens.cs522.chat.oneway.client.callbacks;

import java.util.List;

import edu.stevens.cs522.chat.oneway.client.entities.Message;

/**
 * Created by baixinrui on 3/20/16.
 */
public interface IMessagesListener
{
    public void insertAllMes(List<Message> messages);
}
