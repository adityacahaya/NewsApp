package com.wordpress.kadekadityablog.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by I Kadek Aditya on 5/13/2017.
 */

/**
 * membuat class Loader yang digunakan untuk mengambil data dari internet dilakukan secara background
 * supaya tidak menghambat activity yang sedang berjalan di depannya
 */
public class NewsLoader extends AsyncTaskLoader<List<News>> {

    // membuat tag untuk log kalau misalkan ingin mencari error
    private static final String LOG_TAG = "NewsApp";

    // membuat variable untung nampung URL yang mau di connect
    private String url;

    public NewsLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG,"onStartLoading() di panggil");
        forceLoad();
    }

    /**
     * prosedur yang digunakan untuk mengambil data dari internet tapi dilakukan di belakang layar
     */
    @Override
    public List<News> loadInBackground() {
        Log.i(LOG_TAG,"loadInBackground() di panggil");
        /**
         * kalau url nya null, maka kembaliin list kosong
         */
        if (this.url == null) {
            return null;
        }

        // lakukan request connet ke internet untuk ngambil data dari JSON dan masukin ke List<News>
        List<News> newsList = QueryUtils.fetchNewsData(this.url);

        //kemabliin hasil loadData nya berupa List<News>
        return newsList;
    }
}
