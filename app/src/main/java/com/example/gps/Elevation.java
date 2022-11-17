package com.example.gps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * @author Oliver Brottare
 * Elevation gets the elevation of a place using its coordinates.
 */
public class Elevation {
    /**
     * Reads a string as an URL and tries to returns any data in a String
     * @param url The URL the method tries to get to.
     * @return Any data that the server returns.
     * @throws IOException If an INPUT/OUTPUT Exception occurs then the method throws a IOException.
     *                     The URL may be invalid.
     */
    private static String readStringFromUrl(String url) throws IOException {

        try (InputStream inputStream = new URL(url).openStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder();
            int cp;
            while ((cp = reader.read()) != -1) {
                stringBuilder.append((char) cp);
            }
            return stringBuilder.toString();
        }
    }

    /**
     * Creates a JSONObject with the information sent from the URL.
     * @param url The URL used to get the data.
     * @return JSONObject with data from the URL
     * @throws IOException INPUT/OUTPUT error, if the URL is invalid
     * @throws JSONException If the data returned is not valid to create a JSONObject with.
     */
    private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        String text = readStringFromUrl(url);
        return new JSONObject(text);
    }

    /**
     * Requests the elevation of a chosen place from Googles elevation API using coordinates.
     * @param lat The latitude used for the request
     * @param lon The longitude used for the request
     * @return The elevation above sea level in meters.
     */
    private int reqElevation(String lat, String lon){

        //Google returns a JSON
        JSONObject jsonObject;

        try {

            //Read Json from the API
            jsonObject = readJsonFromUrl("https://maps.googleapis.com/maps/api/elevation/json?locations=" + lat +"%2C" + lon + "&key=AIzaSyAFCjzAKiGkOut6Mre0evMVFsETTzpNMVs" );

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return -9999;
        }

        //Returns the elevation
        try {
            return ((BigDecimal)((JSONObject) ((JSONArray) jsonObject.get("results")).get(0)).get("elevation")).intValue();
        }catch (JSONException e){
            e.printStackTrace();
        }

        //Returns -9999 in case something went wrong.
        return -9999;
    }
}
