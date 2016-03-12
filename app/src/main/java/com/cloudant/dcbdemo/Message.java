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
 * A Class to transform a document into a displable format, and transform a
 * new message into a document revision.
 */
public class Message implements Comparable<Message>
{

    private String timestamp;
    private Date date = null;
    private String message;
    private String user;


    /**
     * Constrcutor which makes a new message object, it will generate the times string.
     */
    public Message(@NonNull String user, @NonNull String message){
        this.user = user;
        this.message = message;

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", Locale.ENGLISH);

        timestamp = format.format(date);


    }

    /**
     * Creates a message object from a DocumentRevision, this should be primarly used to generate
     * a message object from a saved document.
     * @param revision The revision which contains the information to populate the
     *                 message object with.
     */
    public Message(@NonNull BasicDocumentRevision revision){
        Map<String,Object> body = revision.getBody().asMap();
        //for now cast will need to polish
        timestamp = (String)body.get("timestamp");
        message = (String)body.get("message");
        user = (String)body.get("user");

    }

    /**
     * Turns this message into a MutableDoumentRevision for saving,
     * it <strong>will not</strong> contain an ID so it will
     * always create new unique documents when saved to the datastore.
     * @return a new MutableDocumentRevision.
     */
    public @NonNull MutableDocumentRevision toRevision(){
        Map<String,String> body = new HashMap<String, String>();
        body.put("timestamp",timestamp);
        body.put("message",message);
        body.put("user", user);


        MutableDocumentRevision revision = new MutableDocumentRevision();
        revision.body = DocumentBodyFactory.create(body);

        return revision;
    }


    /**
     * Generates a string representation of the object,
     * it is used to display the message in the list view,
     * so it takes the form of Username: "Message"
     * @return
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(user);
        stringBuilder.append(": ");
        stringBuilder.append(message);
        return stringBuilder.toString();
    }

    /**
     * Comparator for messages, this only performs the compare on the internal
     * date of the message, this allows messages to be ordered by date.
     * @param another The message to compare to
     * @return
     */
    @Override
    public int compareTo(Message another) {
        return this.date.compareTo(another.date);
    }
}
