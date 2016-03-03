package com.cloudant.dcbdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * A login screen that offers a field to set the username for the current user.
 */
public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        final EditText usernameView = (EditText) findViewById(R.id.username);



        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameView.getText().toString();
                if(username.isEmpty() && username.trim().isEmpty()){
                    //show a toast
                    return;
                }

                Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
                intent.putExtra(BundleConstants.USERNAME, username);

                LoginActivity.this.startActivity(intent);

            }
        });
    }

}

