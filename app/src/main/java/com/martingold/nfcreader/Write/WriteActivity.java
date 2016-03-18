package com.martingold.nfcreader.Write;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.martingold.nfcreader.R;
import com.martingold.nfcreader.Utils.App;
import com.martingold.nfcreader.Utils.Constants;
import com.martingold.nfcreader.Utils.Methods;
import com.martingold.nfcreader.Utils.RestClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/**
 * Created by martin on 26.10.15.
 */
    public class WriteActivity extends AppCompatActivity {
        NfcAdapter nfcAdapter;
        int selectedItemPos;
        Dialog d;
        List<Item> iList;
        WriteListAdapter adapter;

        String URL = "";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_write);

            URL = Constants.getSimpleServerName();

            d = new Dialog(WriteActivity.this);
            d.setTitle("Přiložte NFC Tag");
            d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    selectedItemPos = -1;
                    disableForegroundDispatchSystem();
                }
            });

            nfcAdapter = NfcAdapter.getDefaultAdapter(this);


            iList = new ArrayList<Item>();
            adapter = new WriteListAdapter(this, R.layout.row_write_item, iList);
            ListView lv = (ListView) findViewById(R.id.write_list);
            lv.setAdapter(adapter);


            RestClient.get("places/" + ((App) getApplicationContext()).id + "/items", null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            iList.add(new Item(response.getJSONObject(i)));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Toast.makeText(WriteActivity.this, "Nastala neznámá chyba", Toast.LENGTH_SHORT).show();
                }
            });




            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectedItemPos = position;
                    enableForegroundDispatchSystem();
                    d.show();
                }
            });
        }

        @Override
        protected void onResume() {
            super.onResume();
        }

        @Override
        protected void onPause() {
            super.onPause();
            disableForegroundDispatchSystem();
        }


        public NdefMessage getAppNdef(int itemId){
            NdefRecord appRecord = NdefRecord.createApplicationRecord(getApplicationContext().getPackageName());
            NdefRecord relayRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                    new String("application/" + getApplicationContext().getPackageName()).getBytes(Charset.forName("US-ASCII")),
                    null, (itemId+"@"+URL).getBytes());
            return new NdefMessage(new NdefRecord[] {relayRecord, appRecord});
        }


        @Override
        protected void onNewIntent(Intent intent) {
            super.onNewIntent(intent);
            if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                int tag_id = Methods.byteArrayToInt(tag.getId());
                writeNdefMessage(tag, getAppNdef(iList.get(selectedItemPos).getId()));
                updateDatabaseTagId(tag_id);

            }
        }

        public void updateDatabaseTagId(final int tagId){
            RequestParams params = new RequestParams();
            params.put("tag_id", tagId);
            RestClient.post("items/"+iList.get(selectedItemPos).getId()+"/tag", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        if(response.getString("status").equals("ok")){
                            if(d.isShowing()){
                                d.dismiss();
                            }
                            iList.get(selectedItemPos).setTag_id(tagId);
                            Toast.makeText(WriteActivity.this, "Tag úspěšně přiřazen", Toast.LENGTH_SHORT).show();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });

                        }else{
                            Toast.makeText(WriteActivity.this, "Nastala neznámá chyba", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Toast.makeText(WriteActivity.this, "Nastala neznámá chyba", Toast.LENGTH_SHORT).show();
                    if(d.isShowing()){
                        d.dismiss();
                    }
                }
            });
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

            return super.onOptionsItemSelected(item);
        }


        private void enableForegroundDispatchSystem() {
            Intent intent = new Intent(this, WriteActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            IntentFilter[] intentFilters = new IntentFilter[]{};
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
        }

        private void disableForegroundDispatchSystem() {
            nfcAdapter.disableForegroundDispatch(this);
        }

        private void formatTag(Tag tag, NdefMessage ndefMessage) {
            try {
                NdefFormatable ndefFormatable = NdefFormatable.get(tag);
                if (ndefFormatable == null) {
                    Toast.makeText(this, "Tag is not ndef formatable!", Toast.LENGTH_SHORT).show();
                    return;
                }
                ndefFormatable.connect();
                ndefFormatable.format(ndefMessage);
                ndefFormatable.close();
                Toast.makeText(this, "Tag writen!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e("formatTag", e.getMessage());
            }
        }
        private void writeNdefMessage(Tag tag, NdefMessage ndefMessage) {
            try {
                if (tag == null) {
                    Toast.makeText(this, "Tag object cannot be null", Toast.LENGTH_SHORT).show();
                    return;
                }
                Ndef ndef = Ndef.get(tag);
                if (ndef == null) {
                    // format tag with the ndef format and writes the message.
                    formatTag(tag, ndefMessage);
                } else {
                    ndef.connect();
                    if (!ndef.isWritable()) {
                        Toast.makeText(this, "Tag is not writable!", Toast.LENGTH_SHORT).show();
                        ndef.close();
                        return;
                    }
                    ndef.writeNdefMessage(ndefMessage);
                    ndef.close();
                    //Toast.makeText(this, "Tag writen!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("writeNdefMessage", ""+e.getMessage());
            }
        }


        private NdefRecord createTextRecord(String content) {
            try {
                byte[] language;
                language = Locale.getDefault().getLanguage().getBytes("UTF-8");

                final byte[] text = content.getBytes("UTF-8");
                final int languageSize = language.length;
                final int textLength = text.length;
                final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLength);

                payload.write((byte) (languageSize & 0x1F));
                payload.write(language, 0, languageSize);
                payload.write(text, 0, textLength);

                return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());

            } catch (UnsupportedEncodingException e) {
                Log.e("createTextRecord", e.getMessage());
            }
            return null;
        }


        private NdefMessage createNdefMessage(String content) {

            NdefRecord ndefRecord = createTextRecord(content);
            NdefRecord hostURL = createTextRecord(Constants.server);

            return new NdefMessage(new NdefRecord[]{ndefRecord, hostURL});
        }

    }

