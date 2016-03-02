package com.cloudant.dcbdemo;

import android.content.Context;

import com.cloudant.sync.datastore.DatastoreManager;
import com.google.common.eventbus.EventBus;

/**
 * Created by Rhys Short on 28/01/2016.
 */
public class DCDatastoreManager {

    private static DatastoreManager manager;

    private static EventBus eventBus = new EventBus();


    public static synchronized DatastoreManager getManager(Context ctx){
        if (manager == null){
            manager = new DatastoreManager(ctx.getDir("cloudantsync2", Context
                    .MODE_PRIVATE));
        }

        return manager;
    }

    public static EventBus getEventBus(){
        return eventBus;
    }






}
