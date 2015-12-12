package com.martingold.nfcreader.Utils;

import android.app.Application;

/**
 * Created by martin on 26.10.15.
 */
public class App extends Application {

    public int id;
    public String URL;

    public boolean isLogged(){
        return id > 0;
    }

}
