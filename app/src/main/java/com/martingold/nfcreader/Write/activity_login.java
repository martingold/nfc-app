package com.martingold.nfcreader.Write;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.martingold.nfcreader.R;
import com.martingold.nfcreader.Utils.App;
import com.martingold.nfcreader.Utils.Constants;
import com.martingold.nfcreader.Utils.Methods;
import com.martingold.nfcreader.Utils.RestClient;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by martin on 26.10.15.
 */
public class activity_login extends AppCompatActivity {


    EditText name;
    EditText password;
    EditText host;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        name = (EditText) findViewById(R.id.login_name);
        password = (EditText) findViewById(R.id.login_password);
        submit = (Button) findViewById(R.id.login_submit);
        host = (EditText) findViewById(R.id.login_host);

        name.setText(Methods.getSharedPref(activity_login.this, "login"));
        host.setText(Methods.getSharedPref(activity_login.this, "host"));

        if(name.getText().toString().length() > 0){
            password.requestFocus();
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hostURL = host.getText().toString();
                if(Methods.isUrlValid(hostURL)){
                    Constants.setServer(hostURL);
                    RequestParams params = new RequestParams();
                    params.put("username", name.getText().toString().trim());
                    params.put("password", password.getText().toString().trim());
                    RestClient.post("login/", params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                if(response.getString("status").equals("error")){
                                    Toast.makeText(activity_login.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                }else{
                                    //Toast.makeText(activity_login.this, "SUCESS: "+response.getString("message"), Toast.LENGTH_SHORT).show();
                                    ((App) getApplicationContext()).id = Integer.parseInt(response.getString("message"));
                                    startActivity(new Intent(activity_login.this, WriteActivity.class));
                                    Methods.setSharedPref(activity_login.this, "login", name.getText().toString().trim());
                                    Methods.setSharedPref(activity_login.this, "host", Constants.server);
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            Toast.makeText(activity_login.this, "Nastala neznámá chyba", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(activity_login.this, "Zadaný server není platný", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
