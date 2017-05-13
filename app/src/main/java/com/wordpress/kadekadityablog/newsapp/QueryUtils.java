package com.wordpress.kadekadityablog.newsapp;

/**
 * Created by I Kadek Aditya on 5/8/2017.
 */

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


/**
 * class yang digunakan untuk mengambil data dari internet dan memparsing JSON nya
 */
public final class QueryUtils {

    //membuat tag untuk log kalau misalkan ingin mencari error
    private static final String LOG_TAG = "NewsApp";

    private QueryUtils() {}

    /**
     * prosedur yang dilakukan untuk mendapatkan data dalam bentuk List<News> dari internet
     */
    public static List<News> fetchNewsData(String requestUrl) {
        Log.i(LOG_TAG,"fetchNewsData() di panggil");

        // membuat URL terlebih dahulu
        URL url = createUrl(requestUrl);

        /**
         * request data dari URL yang di buat, dengan HTTP request untuk mendapatkan JSON nya
         */
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // ekstrak data dari JSON agar menjadi List<News>
        List<News> newsList = extractFeatureFromJson(jsonResponse);

        // kembalikan hasil prosedur menjadi List<News>
        return newsList;
    }

    /**
     * prosedur untuk membuat URL dari String url
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * prosedur unruk membuat Request ke URL menggunakan HTTP Request untuk mendapatkan data JSON
     */
    private static String makeHttpRequest(URL url) throws IOException {
        //variable untuk menampung hasil JSON
        String jsonResponse = "";

        // kalau url nya null, maka kembalikan JSON kosong
        if (url == null) {
            return jsonResponse;
        }

        //mulai connect
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(20000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // kalau connect berhasil (response code 200) maka langsung ambil JSON nya
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            /**
             * close connection sama input stream yang di pake buat dapetin data JSON
             */
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * prosedur yang digunakan untuk membaca dan mengambil data JSON saat berhasil connect ke internet
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * prosedur untuk menghasilkan List<News> dengan mengekstrak data JSON yang di dapatkan sebelumnya
     */
    private static List<News> extractFeatureFromJson(String newsJSON) {
        /**
         * jika data JSON kosong, maka kembalikan list nya null
         */
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // buat List<News> kosong untuk menampung data setiap News dari hasil JSON
        List<News> newsList = new ArrayList<>();

        /**
         * disini langsung lakukan parsing JSON nya ke object class News terus add ke List<News>
         */
        try {

            // buat JSONObject dari JSON response
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            // Extract JSONObject sesuai dengan key yang ada di data JSON nya (parent)
            JSONObject responseObject = baseJsonResponse.getJSONObject("response");

            //Extract JSONArray sesuai dengan key yang ada di data JSON nya (child dari responseObject)
            JSONArray resultsArray = responseObject.getJSONArray("results");

            // untuk setiap News pada resultsArray maka dapatkan data nya
            for (int i = 0; i < resultsArray.length(); i++) {

                // dapatkan news pada setiap perulangan
                JSONObject currentNews = resultsArray.getJSONObject(i);

                /**
                 * dapetin data-data yang kita perlu dari current News
                 */
                String title = currentNews.getString("webTitle");
                String section = currentNews.getString("sectionName");
                String date = currentNews.getString("webPublicationDate");
                String url = currentNews.getString("webUrl");

                // buat object untuk News dan masukkin data-data sebelumnya yang udah di dapet
                News news = new News(title, section, date, url);

                // masukin news kedalam List<News>
                newsList.add(news);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // kemablikan hasil prosedur berupa List<News>
        return newsList;
    }

}