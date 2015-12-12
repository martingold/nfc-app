package com.martingold.nfcreader.Utils;

/**
 * Created by martin on 21.10.15.
 */
public class Constants {

 // public static String server = "http://192.168.1.19/";
  public static String server = "http://www.nfcreader.hol.es/";

  public static String MIME = "application/com.martingold.nfcreader";

  public static String setServer(String URL){
    if(!URL.startsWith("http://")){
      URL = "http://" + URL;
    }
    if(URL.contains("www.")){
      URL.replace("www.", "");
    }
    if (URL.endsWith("/")) {
      URL = URL.substring(0, URL.length() - 1);
    }
    server = URL;
    return URL;
  }

    public static String getSimpleServerName(){
     return server.replaceFirst("^(http://|http://www\\.|www\\.)","");
    }


}
