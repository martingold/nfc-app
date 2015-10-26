package com.martingold.nfcreader.Write;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.martingold.nfcreader.R;

import java.util.List;

public class WriteListAdapter extends ArrayAdapter<Item> {

    public WriteListAdapter(Context context, int resource, List<Item> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.row_write_item, null);
        }
        Item p = getItem(position);
        if (p != null) {
            TextView name = (TextView) v.findViewById(R.id.row_write_item_name);
            name.setText(p.getName());
        }
        return v;
    }

}
