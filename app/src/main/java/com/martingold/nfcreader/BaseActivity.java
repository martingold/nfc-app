package com.martingold.nfcreader;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.martingold.nfcreader.Utils.Constants;

import java.util.Arrays;

/**
 * Created by martin on 22.10.15.
 */
public abstract class BaseActivity extends ActionBarActivity {

    public static String MIME;
    public static final String TAG = "nfc-reader";
    private NfcAdapter mNfcAdapter;

    protected abstract int getLayoutResourceId();
    protected abstract String getServiceName();

    PendingIntent mPendingIntent;

    boolean isRead = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        MIME = Constants.MIME;

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
                Toast.makeText(this, "Toto zařízení nepodporuje NFC.", Toast.LENGTH_LONG).show();
                finish();
            return;
        }else{
            if (!mNfcAdapter.isEnabled()) {
                Toast.makeText(this, "Povolte NFC.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                finish();
            }
        }
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()) && !isRead) {
            handleIntent(getIntent());
        }
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause() {
        stopForegroundDispatch(this, mNfcAdapter);
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
        if(getServiceName().equals("content")){
            this.finish();
        }
    }

    private void handleIntent(Intent intent) {
        isRead = true;
        if(intent.getType() != null && intent.getType().equals("application/" + getPackageName())) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefRecord record = ((NdefMessage) rawMsgs[0]).getRecords()[0];
            String nfcData = new String(record.getPayload());
            if(nfcData.contains("@")){
                String[] data = nfcData.split("@");
                String host = data[1];
                String id = data [0];
                Constants.setServer(host);
                launchContentActivity(Integer.parseInt(id));
            }else{
                Toast.makeText(BaseActivity.this, "Neplatná nálepka", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);
        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }
        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    public void launchContentActivity(int id) {
        Intent i = new Intent(this, ContentViewActivity.class);
        i.putExtra("id", id);
        startActivity(i);
    }
}