package com.martingold.nfcreader.Write;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by martin on 26.10.15.
 */
public class Item {



    private int id;
    private String name;
    private String description;
    private int tag_id;


    public Item(JSONObject json) throws JSONException {
        this.name = json.getString("name");
        this.description = json.getString("description");
        this.id = json.getInt("id");
        this.tag_id = json.getInt("tag_id");
    }

    public int getId() {
        return id;
    }

    public void setTag_id(int tag_id) {
        this.tag_id = tag_id;
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

    public int getTag_id() {
        return tag_id;
    }

}
