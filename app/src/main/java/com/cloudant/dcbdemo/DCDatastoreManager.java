package com.cloudant.dcbdemo;

import android.content.Context;

import com.cloudant.sync.datastore.DatastoreManager;
import com.google.common.eventbus.EventBus;

/**
 * A class to make a single Datastore manager visible app wide,
 * this is probably not a great practise to have.
 */
public class DCDatastoreManager {

    private static DatastoreManager manager;

    private static EventBus eventBus = new EventBus();


    public static synchronized DatastoreManager getManager(Context ctx){
        if (manager == null){
            manager = new DatastoreManager(ctx.getDir("cloudantsync", Context
                    .MODE_PRIVATE));
        }

        return manager;
    }

    public static EventBus getEventBus(){
        return eventBus;
    }






}
