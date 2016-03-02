package com.cloudant.dcbdemo;

import android.support.annotation.NonNull;

import com.cloudant.sync.datastore.BasicDocumentRevision;
import com.cloudant.sync.datastore.DocumentBodyFactory;
import com.cloudant.sync.datastore.MutableDocumentRevision;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Rhys Short on 28/01/2016.
 */
public class Message implements Comparable<Message>
{

    private String timestamp;
    private Date date = null;
    private String message;
    private String user;


    public Message(@NonNull String user, @NonNull String message){
        //timestamp will be generated here
        this.user = user;
        this.message = message;

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", Locale.ENGLISH);

        timestamp = format.format(date);


    }

    public Message(@NonNull BasicDocumentRevision revision){
        Map<String,Object> body = revision.getBody().asMap();
        //for now cast will need to polish
        timestamp = (String)body.get("timestamp");
        message = (String)body.get("message");
        user = (String)body.get("user");

    }

    public MutableDocumentRevision toRevision(){
        Map<String,String> body = new HashMap<String, String>();
        body.put("timestamp",timestamp);
        body.put("message",message);
        body.put("user", user);


        MutableDocumentRevision revision = new MutableDocumentRevision();
        revision.body = DocumentBodyFactory.create(body);

        return revision;
    }

    @Override
    public String toString() {
        //message will eventually be
        // *Username*
        // Message


        //for now just use colons
        StringBuilder stringBuilder = new StringBuilder(user);
        stringBuilder.append(": ");
        stringBuilder.append(message);
        return stringBuilder.toString();
    }

    @Override
    public int compareTo(Message another) {
        return this.date.compareTo(another.date);
    }
}
