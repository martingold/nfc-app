package com.martingold.nfcreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.martingold.nfcreader.Utils.Constants;
import com.martingold.nfcreader.Utils.RestClient;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ContentViewActivity extends BaseActivity {

    TextView name;
    TextView description;
    ImageView image;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_content_view;
    }

    @Override
    protected String getServiceName() {
        return "content";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        name = (TextView) findViewById(R.id.content_name);
        description = (TextView) findViewById(R.id.content_description);
        image = (ImageView) findViewById(R.id.content_image);


        final int id = getIntent().getIntExtra("id", 0);
        RestClient.get("items/"+id, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                try {
                    JSONObject item = json.getJSONObject(0);

                    name.setText(item.getString("name"));
                    description.setText(item.getString("description"));
                    Picasso.with(ContentViewActivity.this).load(Constants.server+"assets/images/"+item.getString("image")+".jpg").into(image, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            image.setVisibility(View.VISIBLE);
                        }
                        @Override
                        public void onError() {

                        }
                    });
                    findViewById(R.id.content_container).setVisibility(View.VISIBLE);


                } catch (JSONException e) {
                    Toast.makeText(ContentViewActivity.this, "Tato nálepka (id="+id+") nebyla nalezena", Toast.LENGTH_LONG).show();
                    finish();
                    e.printStackTrace();
                }
            }
        });
    }
}
