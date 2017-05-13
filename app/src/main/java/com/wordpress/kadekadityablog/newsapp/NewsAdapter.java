package com.wordpress.kadekadityablog.newsapp;

/**
 * Created by I Kadek Aditya on 5/13/2017.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * buat class Adapter untuk menampung daftar News kedalam ListView
 */
public class NewsAdapter extends ArrayAdapter<News> {

    //variable untuk menentukan split date berdasarkan
    private static final String DATE_SPLIT_BY = "T";

    public NewsAdapter(Context context, ArrayList<News> newsArrayList) {
        super(context, 0, newsArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        /**
         * masukin Layout untuk adapter ini adalah List_View
         */
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        //dapetin masing-masing news pada ArrayList<News>
        News news = getItem(position);

        /**
         * masukin masing-masing atribut ke view yang benar
         */
        TextView textViewTitle = (TextView) listItemView.findViewById(R.id.title);
        textViewTitle.setText(news.getTitle());
        TextView textViewSection = (TextView) listItemView.findViewById(R.id.section);
        textViewSection.setText(news.getSection());

        /**
         * untuk date perlu dipisahkan antara tanggal dan jam baru kemudian dimasukkan kedalam View
         */
        String baseDate = news.getDate();
        String tanggal = "";
        String jam = "";
        if (baseDate.contains(DATE_SPLIT_BY)) {
            String pisahBaseDate[] = baseDate.split(DATE_SPLIT_BY);
            tanggal = pisahBaseDate[0];
            int panjangKalimatJam = pisahBaseDate[1].length() - 1;
            jam = pisahBaseDate[1].substring(0,panjangKalimatJam);
        }
        TextView textViewDate = (TextView) listItemView.findViewById(R.id.date);
        textViewDate.setText(tanggal);
        TextView textViewJam = (TextView) listItemView.findViewById(R.id.jam);
        textViewJam.setText(jam);

        return listItemView;
    }
}
