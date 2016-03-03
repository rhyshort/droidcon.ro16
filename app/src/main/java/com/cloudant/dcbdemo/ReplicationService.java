package com.cloudant.dcbdemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cloudant.sync.datastore.Datastore;
import com.cloudant.sync.datastore.DatastoreManager;
import com.cloudant.sync.datastore.DatastoreNotCreatedException;
import com.cloudant.sync.notifications.ReplicationCompleted;
import com.cloudant.sync.replication.Replicator;
import com.cloudant.sync.replication.ReplicatorBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.Subscribe;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;

public class ReplicationService extends Service {



    private final BroadcastReceiver receiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            for (Replicator replicator : replicators){
                if(replicator.getState() == Replicator.State.COMPLETE){
                    replicator.start();
                }
            }
        }
    };

    private URI replication_url;
    private Replicator[] replicators = new Replicator[2];
    private AlarmManager alarmManager;

    //this is for the replication completed method to schedule
    // the next set of events
    private int numberOfReplicatorsCompleted = 0;

    @Override
    public void onCreate() {
        super.onCreate();


        new DBURLTask().execute();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




    private void makeReplicators(URI databaseURI) {

        //set up the datastore
        DatastoreManager manager = DCDatastoreManager.getManager(this);
        try {

            Datastore datastore = manager.openDatastore("droidcon16");
            replicators[0] = ReplicatorBuilder.pull().from(databaseURI).to(datastore).build();
            replicators[1] = ReplicatorBuilder.push().from(datastore).to(databaseURI).build();

            replicators[0].getEventBus().register(this);
            replicators[1].getEventBus().register(this);

            //register the receiver
            IntentFilter filter = new IntentFilter();
            filter.addAction("com.cloudant.DCBDemo.Replicate");
            registerReceiver(receiver,filter);


            //start them
            replicators[0].start();
            replicators[1].start();

        } catch (DatastoreNotCreatedException e) {
            Log.e(ChatActivity.class.getCanonicalName(), "Failed to create datastore", e);
            return;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Subscribe
    public void replicationComplete(ReplicationCompleted replicationCompleted){
        numberOfReplicatorsCompleted++;
        DCDatastoreManager.getEventBus().post(replicationCompleted);

        if(numberOfReplicatorsCompleted == 2){
            numberOfReplicatorsCompleted = 0;

            alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent("com.cloudant.DCBDemo.Replicate");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,1,intent,0);
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME,1000,pendingIntent);

        }


        //here we will set the next replication.
    }

    private class DBURLTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {
                URL bluemix = new URL("http://droidconro.eu-gb.mybluemix.net/?user=katytest");
                HttpURLConnection connection = (HttpURLConnection) bluemix.openConnection();
                connection.connect();

                if (connection.getResponseCode() == 200) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, URI> json = objectMapper.readValue(connection.getInputStream(),
                            new TypeReference<Map<String, URI>>() {
                            });

                     makeReplicators(json.get("url"));

                } else {
                    //task failed. handle this.
                    Log.i("MyTag","StatusCode is oddd");
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void uri) {
            super.onPostExecute(uri);

        }
    }
}
