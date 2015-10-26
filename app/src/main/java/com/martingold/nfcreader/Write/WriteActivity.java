package com.martingold.nfcreader.Write;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.martingold.nfcreader.R;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

/**
 * Created by martin on 26.10.15.
 */
    public class WriteActivity extends AppCompatActivity {
        NfcAdapter nfcAdapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //setContentView(R.layout.activity_main);
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        }

        @Override
        protected void onResume() {
            super.onResume();
            enableForegroundDispatchSystem();
        }

        @Override
        protected void onPause() {
            super.onPause();
            disableForegroundDispatchSystem();
        }


        @Override
        protected void onNewIntent(Intent intent) {
            super.onNewIntent(intent);
            Log.i("nfc", "NEW INTENT");
            if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
                Toast.makeText(this, "NfcIntent!", Toast.LENGTH_SHORT).show();
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                NdefMessage ndefMessage = createNdefMessage("Hallo");
                writeNdefMessage(tag, ndefMessage);
            }
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
                    Toast.makeText(this, "Tag writen!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("writeNdefMessage", e.getMessage());
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

            return new NdefMessage(new NdefRecord[]{ndefRecord});
        }

    }
