package com.fouritil.androidtestapplication;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;

/**
 * Created by andre on 10/21/15.
 */
public class Webservice {

    public Boolean isJson(String str) {
        try {
            Object json = new JSONTokener(str).nextValue();

            if (json instanceof JSONObject || json instanceof JSONArray)
                return true;
            else
                return false;
        } catch (JSONException e) {
            return false;
        }
    }


    public JSONObject getStores() {

        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://andrebian.com/json-example.json");

        try {

            // Execute HTTP GET Request

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = httpclient.execute(httpGet, responseHandler);

            System.out.println(responseBody);

            if( isJson(responseBody) ) {
                return new JSONObject(responseBody);
            } else {
                return null;
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
