package com.martingold.nfcreader;


import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by martin on 23.5.15.
 */
class NdefParser extends AsyncTask<String, Void, String> {

    Tag tag;
    NdefParserListener listener;

    public interface NdefParserListener {
        void onNdefDone(String response, String status);
    }

    public NdefParser(NdefParserListener listener, Tag tag){
        this.listener = listener;
        this.tag = tag;
        this.execute();
    }

    @Override
    protected String doInBackground(String... params) {

        Ndef ndef = Ndef.get(tag);
        if (ndef == null) {
            return null;
        }

        NdefMessage ndefMessage = ndef.getCachedNdefMessage();

        NdefRecord[] records = ndefMessage.getRecords();
        for (NdefRecord ndefRecord : records) {
            try {
                Log.i("nfc", "" + readText(ndefRecord));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                try {
                    return readText(ndefRecord);
                } catch (UnsupportedEncodingException e) {
                    Log.e("nfc", "Unsupported Encoding", e);
                }
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        listener.onNdefDone(s, "ok");
    }

    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        byte[] payload = record.getPayload();

        String textEncoding;
        if ((payload[0] & 128) == 0){
            textEncoding = "UTF-8";
        }else{
            textEncoding = "UTF-16";
        }

        int languageCodeLength = payload[0] & 0063;

        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
    }
}

