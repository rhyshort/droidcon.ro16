package com.cloudant.dcbdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cloudant.sync.datastore.BasicDocumentRevision;
import com.cloudant.sync.datastore.Datastore;
import com.cloudant.sync.datastore.DatastoreManager;
import com.cloudant.sync.datastore.DatastoreNotCreatedException;
import com.cloudant.sync.datastore.DocumentException;
import com.cloudant.sync.datastore.DocumentNotFoundException;
import com.cloudant.sync.notifications.ReplicationCompleted;
import com.google.common.eventbus.Subscribe;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<Message> listAdapter = null;
    private ListView listView = null;

    private String username;

    private Datastore datastore;
    private com.cloudant.sync.replication.ReplicationService mReplicationService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        username = getIntent().getStringExtra("username");

        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(android.R.id.list);

        listAdapter = new ArrayAdapter<Message>(this,R.layout.list_item);
        listView.setAdapter(listAdapter);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.startActivityForResult(new Intent(MainActivity.this,
                        SendMessage.class), 1);
            }
        });

        //create the service
        new CreateService().execute();
        //new CreateDocs().execute();
        //set up the datastore
        DatastoreManager manager = DCDatastoreManager.getManager(this);
        try {
            datastore = manager.openDatastore("droidcon16");
            DCDatastoreManager.getEventBus().register(this);
            updateMessages();



        } catch (DatastoreNotCreatedException e) {
            Log.e(MainActivity.class.getCanonicalName(), "Failed to create datastore", e);
            this.finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //for now this only returns message data but we will be prepaed for others
        switch (requestCode){
            case 1:
                //message
                if(resultCode == 0){
                    //no error
                    Message message = new Message(username,data.getStringExtra("message"));
                    try {
                        this.datastore.createDocumentFromRevision(message.toRevision());
                        //issue reload.

                        this.updateMessages();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                        //this should never happen
                    }
                } else {
                    //errored do something.
                }
                break;
        }
    }

    private void updateMessages() {
        try {
            //this could be heavy should consider moving off main thread
            //generate the docs!
            List<String> documentIds = datastore.getAllDocumentIds();
            final List<Message> newMessages = new ArrayList<Message>();
            for (String id : documentIds) {
                BasicDocumentRevision revision = datastore.getDocument(id);
                newMessages.add(new Message(revision));
            }

            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //clear for now what we should actually do is make sure we haven't seen that id before.
                    listAdapter.clear();
                    listAdapter.addAll(newMessages);
                    listView.postInvalidate();
                }
            });
        } catch (DocumentNotFoundException e){
            e.printStackTrace();
            this.finish();
            //this should never happen.
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private class CreateDocs extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            Message message = new Message("rhyshort","Hello World!");

            for ( int i =0; i< 100; i++){
                try {
                    datastore.createDocumentFromRevision(message.toRevision());
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }

            Log.i("Finished!","Finished!");

            return null;
        }
    }

    @Subscribe
    public void replicationCompleted(ReplicationCompleted replicationCompleted){
        //trigger update and return quickly
        updateMessages();
    }


    private class CreateService extends AsyncTask<Void, Void, URI> {

        @Override
        protected URI doInBackground(Void... params) {



            return null;

        }

        @Override
        protected void onPostExecute(URI uri) {
            super.onPostExecute(uri);

            Intent intent = new Intent(MainActivity.this, ReplicationService.class);
            MainActivity.this.bindService(intent,
                    mConnection,
                    Context.BIND_AUTO_CREATE);
        }
    }

}
