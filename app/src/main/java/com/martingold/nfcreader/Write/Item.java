package com.martingold.nfcreader.Write;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by martin on 26.10.15.
 */
public class Item {

    private String name;
    private String description;


    public Item(JSONObject json) throws JSONException {
        this.name = json.getString("name");
        this.description = json.getString("description");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
