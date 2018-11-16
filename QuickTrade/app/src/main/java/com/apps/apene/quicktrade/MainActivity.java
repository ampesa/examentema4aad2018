package com.apps.apene.quicktrade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.apps.apene.quicktrade.model.ProductSearch;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    // Elementos gráficos
    protected ImageView mCategoryMotor = null;
    protected ImageView mCategoryHome = null;
    protected ImageView mCategoryTech = null;
    protected Button mButtonUpload = null;
    protected FirebaseAuth mAuth = null;
    protected String sCurrentUID = null;
    protected ProductSearch mSearch = null;
    protected String attribute = null;
    protected String value = null;

    // Recuerda conectar con la API REST de la moneda para actualizar el tipo de cambio al iniciar
    // esta actividad
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Mostramos la barra de acción superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Referencias
        mCategoryMotor = findViewById(R.id.iv_category_motor);
        mCategoryHome = findViewById(R.id.iv_category_home);
        mCategoryTech = findViewById(R.id.iv_category_tech);
        mButtonUpload = findViewById(R.id.bt_main_upload);
        mAuth = FirebaseAuth.getInstance();
        sCurrentUID = mAuth.getCurrentUser().getUid();
        mSearch = new ProductSearch();


        mCategoryMotor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value = "Vehicles";
                attribute = "category";
                Intent result = new Intent(MainActivity.this, ResultsView.class);
                result.putExtra("attribute", attribute);
                result.putExtra("value", value);
                startActivity(result);
            }
        });

        mCategoryHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mCategoryTech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToAddProduct = new Intent(getApplicationContext(), AddProduct.class);
                startActivity(goToAddProduct);
                //Toast.makeText(getApplicationContext(), sCurrentUID, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Intents para cada selección de items del menú
        switch (item.getItemId()){
            // Al pulsar atrás, finalizamos la actividad y volvemos a main
            case android.R.id.home :
                this.finish();
                return true;
            case R.id.menu_profile :
                Intent goToProfile = new Intent (this, Profile.class);
                startActivity(goToProfile);
                return true;
            case R.id.menu_settings :
                Intent gotToSettings = new Intent (this, UserSettings.class);
                startActivity(gotToSettings);
                return true;
            case R.id.menu_search :
                return true;
            case R.id.menu_logout :
                return true;
            default:
                return true;
        }
    }
}
