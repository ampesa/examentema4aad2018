package com.apps.apene.quicktrade;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class UserSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        // Mostramos la barra de acción en la actividad
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new UserPreferences())
                .commit();

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        // Al pulsar atrás, finalizamos la actividad y volvemos a main
        switch (item.getItemId()){
            case android.R.id.home :
                this.finish();
                return true;
            default:
                return true;
        }
    }
}
