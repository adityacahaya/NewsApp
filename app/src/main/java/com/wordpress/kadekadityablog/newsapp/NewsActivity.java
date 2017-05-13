package com.wordpress.kadekadityablog.newsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.app.LoaderManager;
import android.net.Uri;
import android.content.Loader;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;
import static android.view.View.GONE;

/**
 * di main activity, untuk menggunakan loader untuk connect inet, maka perlu implement LoaderManager
 */
public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>{

    // membuat tag untuk log kalau misalkan ingin mencari error
    private static final String LOG_TAG = "NewsApp";

    // bikin url API yang mau di dapatkan (Guardian API)
    //private static final String NEWS_REQUEST_URL = "https://content.guardianapis.com/search?q=debates&api-key=test";
    private static final String NEWS_REQUEST_URL = "https://content.guardianapis.com/search";

    // bikin object adapter
    private NewsAdapter newsAdapter;

    // bikin object untuk mengetahui apps kita connect internet atau engga
    private boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG,"onCreate() di panggil");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        //cek koneksi apakah apps kita connect internet atau tidak
        connected = cekConnection();

        /**
         * kondisi kalau connect dan tidak connect, kalau connect langsung ambil data pake LoaderManager
         * kalau engga tampilin tulisan No Network Found
         */
        if (connected){
            Log.i(LOG_TAG,"connected");
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(1, null, this);
            Log.i(LOG_TAG,"initLoader() di panggil");
        }else{
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progres_bar);
            progressBar.setVisibility(GONE);
            TextView textView = (TextView)findViewById(R.id.tv_loadMessage);
            textView.setText("No network found");
        }

        /**
         * bikin kalau ListView nya engga ada data yang terisi, tampilin dulu tulisan kalau data nya belum ketemu
         */
        ListView listViewNews  = (ListView) findViewById(R.id.list);
        listViewNews.setEmptyView(findViewById(R.id.tv_loadMessage));

        /**
         * bikin dulu adapter news yang isinya kosong sebagai input ke ListView terus set ke ListView nya
         */
        newsAdapter = new NewsAdapter(this,new ArrayList<News>());
        listViewNews.setAdapter(newsAdapter);

        /**
         * bikin kalo misalkan ListView nya di klik, dia pindah ke browser sesuai dengan url dari si beritanya
         */
        listViewNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // dapetin news mana yang di klik
                News currentNews = newsAdapter.getItem(position);

                // Convert string URL ke URL untuk ke browser
                Uri newsUri = Uri.parse(currentNews.getUrl());

                // Langsung deh pindah ke browser
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                startActivity(websiteIntent);
            }
        });
    }

    /**
     * cara cek apps kita sudah connect atau belum ke internet, kalau connect pake true, kalau engga pake false
     */
    public boolean cekConnection(){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        }
        else {
            connected = false;
        }
        return connected;
    }

    /**
     * panggil prosedur ini untuk nge load data dari internet
     */
    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG,"onCreateLoader() di panggil");

        /**
         * ketika akan melakukan pencarian berita, maka URL yang digunakan perlu di tambahkan
         * beberapa query yang berdasarkan pada setting yang user telah buat dengan cara menggunakan
         * SharedPreferences ini, masukin seting user ke variable, terus rubah url sesuai dengan
         * inputan seting si user
         */
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String topic = sharedPrefs.getString(
                getString(R.string.settings_topic_key),
                getString(R.string.settings_topic_default)
        );
        Uri baseUri = Uri.parse(NEWS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("q", topic);
        uriBuilder.appendQueryParameter("api-key", "test");

        //ambil deh data dari internet sesuai setting user
        return new NewsLoader(this, uriBuilder.toString());
    }

    /**
     * panggil prosedur ini kalo load data nya sudah selesai
     */
    @Override
    public void onLoadFinished(android.content.Loader<List<News>> loader, List<News> data) {
        Log.i(LOG_TAG,"onLoadFinished() di panggil");

        //bersihkan adapter dari data sebelumnya
        newsAdapter.clear();

        /**
         * jika List<News> nya engga kosong (hasil dari load data sebelumnya), maka tambah data List<News>
         * ke adapter. Kalau kosong maka hilagin progress bar tampilin "News Not Found"
         */
        if (data != null && !data.isEmpty()) {
            newsAdapter.addAll(data);
        }
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progres_bar);
        progressBar.setVisibility(GONE);
        TextView textView = (TextView)findViewById(R.id.tv_loadMessage);
        textView.setText("News not found");
    }

    /**
     * panggil prosedur ini kalo aplikasinya di close
     */
    @Override
    public void onLoaderReset(android.content.Loader<List<News>> loader) {
        Log.i(LOG_TAG,"onLoaderReset() di panggil");

        // hapus semua data di adapter yang ada sebelumnya.
        newsAdapter.clear();
    }

    /**
     * untuk menambahkan adanya menu settings ke dalam aplikasi atau activity ini, maka di perlukan
     * prosedur ini
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
