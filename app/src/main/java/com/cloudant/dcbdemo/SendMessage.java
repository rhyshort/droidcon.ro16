package com.cloudant.dcbdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Activity to gather the message to send from the user.
 */
public class SendMessage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_send_message);

        Button sendBtn = (Button)findViewById(R.id.send);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the data from the UI and send back to the calling activity.
                String text = ((EditText)findViewById(R.id.editText)).getText().toString();

                Intent intent = new Intent();
                intent.putExtra("message",text);
                setResult(0,intent);
                finish();
            }
        });


    }
}
