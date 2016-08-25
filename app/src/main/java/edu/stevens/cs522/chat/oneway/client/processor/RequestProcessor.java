package edu.stevens.cs522.chat.oneway.client.processor;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.JsonReader;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.stevens.cs522.chat.oneway.client.callbacks.StreamingOutput;
import edu.stevens.cs522.chat.oneway.client.entities.Message;
import edu.stevens.cs522.chat.oneway.client.entities.Peer;
import edu.stevens.cs522.chat.oneway.client.httprequests.RegisterRequest;
import edu.stevens.cs522.chat.oneway.client.httprequests.SyncRequest;
import edu.stevens.cs522.chat.oneway.client.httpresponses.HttpResponse;
import edu.stevens.cs522.chat.oneway.client.httpresponses.StreamingResponse;

/**
 * Created by baixinrui on 3/9/16.
 */
public class RequestProcessor implements StreamingOutput
{
    final static private String TAG = RequestProcessor.class.getSimpleName();
    private RestMethod restMethod;
    private HttpResponse response;
    private StreamingResponse streamingResponse;
    public List<Peer> clients = new ArrayList<Peer>();
    public List<Message> messages = new ArrayList<Message>();
    private int roomid;
    private HashMap<String , Integer> map = new HashMap<String , Integer>();
    private Context context;
    private Geocoder geocoder;


    public RequestProcessor()
    {
        restMethod = new RestMethod();
    }

    public RequestProcessor(Context context)
    {
        restMethod = new RestMethod();
        this.context = context;
    }

    public HttpResponse perform(RegisterRequest request) throws IOException
    {
        response = restMethod.performRegister(request);

        return response;
    }

//deal with the response from chatserver
    public StreamingResponse perform(SyncRequest request , int roomid) throws IOException
    {
        Log.d(TAG, "now in the processor to perfrom SyncRequest");
        this.roomid = roomid;
        streamingResponse = restMethod.performSync(request);

        JsonReader rd = new JsonReader(
                new BufferedReader(
                        new InputStreamReader(streamingResponse.connection.getInputStream())));

        response = streamingResponse.response;

        if(response.isValid())
        {
            rd.beginObject();
            while(rd.hasNext())
            {
                String name = rd.nextName();
                if(name.equals("clients"))
                {
                    clients = readClients(rd);
                }
                else if(name.equals("messages"))
                {
                    messages = readMessages(rd);
                }
            }
            rd.endObject();

        }
        return null;
    }

    public void initialMap()
    {
        for(int i = 0  ; i < clients.size() ; i++)
        {
            map.put(clients.get(i).name , i+1);
        }
    }

    public List readClients(JsonReader reader) throws IOException
    {
        List<Peer> clients = new ArrayList<Peer>();
        int i = 1;
        reader.beginArray();
        while(reader.hasNext())
        {
            Peer client = readClient(reader , i++);
            clients.add(client);
        }
        reader.endArray();
        return clients;
    }

    public Peer readClient(JsonReader reader , int i) throws IOException {
        String user = "";
        double latitute = 0;
        double longtitude = 0;
        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if(name.equals("sender")) {
                user = reader.nextString();
            }else if(name.equals("latitude")) {
                latitute = reader.nextDouble();
            }else if(name.equals("longitude")) {
                longtitude = reader.nextDouble();
            }
        }
        reader.endObject();
        geocoder = new Geocoder(context , Locale.getDefault());
        List<Address> address = geocoder.getFromLocation(latitute , longtitude , 1);
        Log.d(TAG, "Processor: the location to be geocode is latitude: " + Double.toString(latitute) + " " + Double.toString(longtitude));

        if(address == null || address.size() == 0) {
            return new Peer(0 , user , i , " " , latitute , longtitude);
        }
        Address ad = address.get(0);
        String adtext = ad.getAddressLine(0) + " " + ad.getLocality() + " " + ad.getCountryName();
        return new Peer(0 , user , i , adtext , latitute , longtitude);//the cahtroom_fk should be passed through the action click on the fragment;
    }

    public List readMessages(JsonReader reader) throws IOException
    {
        List<Message> mes = new ArrayList<Message>();
        reader.beginArray();
        while(reader.hasNext())
        {
            Message mss = readMessage(reader);
            mes.add(mss);
        }
        reader.endArray();
        return mes;
    }

    public Message readMessage(JsonReader reader) throws IOException {
        initialMap();
        String chatroom = "";
        long timestamp = 0;
        double latitute = 0;
        double longtitude = 0;
        long seqnum = 0;
        String sender = "";
        String text = "";
        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if(name.equals("chatroom")) {
                chatroom = reader.nextString();
            }else if(name.equals("timestamp")) {
                timestamp = reader.nextLong();
            }else if(name.equals("latitude")) {
                latitute = reader.nextDouble();
            }else if(name.equals("longitude")) {
                longtitude = reader.nextDouble();
            }else if(name.equals("seqnum")) {
                seqnum = reader.nextLong();
            }else if(name.equals("sender")) {
                sender = reader.nextString();
            }else if(name.equals("text")) {
                text = reader.nextString();
            }
        }
        reader.endObject();
        return new Message(0 , text , sender , chatroom , timestamp , seqnum , map.get(sender) , roomid , latitute , longtitude);
//        public Message(int id , String message , String sender , String chatroom, long timestamp ,
//        long messageid , long peer_fk , long chatroom_fk , double message_latitude , double message_longitude)
    }

    @Override
    //writ the messages to the server, and the logic is implements in the SyncRequest.getRequestEntity();
    public void write(HttpURLConnection connection , SyncRequest request) throws IOException
    {
        String requestEntity = request.getRequestEntity();
        addAsyncProperty(connection, request);

        if(requestEntity != null) {
            connection.setDoOutput(true);
            connection.setChunkedStreamingMode(0);
            byte[] outputEntity = requestEntity.getBytes("UTF-8");
            OutputStream out = new BufferedOutputStream(connection.getOutputStream());
            out.write(outputEntity);
            out.flush();
            out.close();
        }
    }

    public void addAsyncProperty(URLConnection connection , SyncRequest request)
    {
        Map<String,String> headers = request.getRequestHeaders();
        for (Map.Entry<String,String> header : headers.entrySet())
        {
            connection.addRequestProperty(header.getKey(), header.getValue());
        }
    }

}
