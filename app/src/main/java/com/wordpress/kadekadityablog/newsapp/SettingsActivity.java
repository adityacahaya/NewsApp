package com.wordpress.kadekadityablog.newsapp;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    /**
     * untuk menambahkan settings ke dalam aplikasi maka di butuhkan PreferenceFragmentt
     */
    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // ketika menu setting di buka, isi setting nya ngambil dari sini
            addPreferencesFromResource(R.xml.settings_main);

            Preference topic = findPreference(getString(R.string.settings_topic_key));
            bindPreferenceSummaryToValue(topic);
        }

        /**
         * prosedur ini digunakan untuk mendeteksi apabila ada perubahan pada settings yang dilakukan
         * oleh user
         */
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Log.i("SettingsActivity","onPreferenceCange() jalan");
            String stringValue = newValue.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            }
            return true;
        }

        /**
         * prosedur ini dibuat untuk mengikat nilai yang sudah di set oleh user sebelumnya
         */
        private void bindPreferenceSummaryToValue(Preference preference) {
            Log.i("SettingsActivity","bindPreferenceSumaary() jalan");
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }
    }
}
