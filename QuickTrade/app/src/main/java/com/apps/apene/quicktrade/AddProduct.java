package com.apps.apene.quicktrade;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.apene.quicktrade.model.FirebaseAdapter;
import com.apps.apene.quicktrade.model.Product;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;

public class AddProduct extends AppCompatActivity implements View.OnClickListener{

    // Elementos gráficos
    protected EditText mProductTitle = null;
    protected ImageView mProductImage = null;
    protected EditText mProductDescription = null;
    protected Spinner mProductSelectCategory = null;
    protected EditText mProductPrice = null;
    protected TextView mProductCurrency = null;
    protected Spinner mProductCountry = null;
    protected EditText mProductZip = null;
    protected Button mButtonAdd = null;
    protected FirebaseAuth mAuth = null;
    protected String sUserUID = null;
    protected String sImage = null;
    protected String sParent = null;
    // Identificador del Intent para el resultado de abrir la galería
    protected static final int GALLERY_INTENT = 100;
    // Objteo URI para la ruta de la imagen
    protected Uri imageURI = null;

    // Boolean para determinar si es un producto del usuario actual o no
    protected Boolean bIsCurrentUserProduct = false;

    // Objeto FirebaseAdapter, llamará a los métodos de añadir un nuevo producto
    private FirebaseAdapter fbAdapter = null;

    // Objeto Storage, lo usaremos para guardar la imagen seleccionada en Firebase Storage
    private StorageReference mStorage = null;

    // Objeto BBDD
    protected DatabaseReference mDataBase;

    // Objeto Product de la vista de producto
    Product previousProduct = null;
    String previousProductKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);



        // referencias
        mProductTitle = findViewById(R.id.et_addproduct_title);
        mProductImage = findViewById(R.id.iv_addproduct_image);
        mProductDescription = findViewById(R.id.et_addproduct_description);
        mProductSelectCategory = findViewById(R.id.spn_addproduct_category);
        mProductPrice = findViewById(R.id.et_addproduct_price);
        mProductCurrency = findViewById(R.id.et_addproduct_price_currency);
        mProductCountry = findViewById(R.id.spn_addproduct_country);
        mProductZip = findViewById(R.id.et_addproduct_zip);
        mButtonAdd = findViewById(R.id.bt_addproduct_add);

        // Referencia del Adaptador Firebase
        fbAdapter = new FirebaseAdapter(this);

        // Referencia del Storage
        mStorage = FirebaseStorage.getInstance().getReference();

        getData();


        // Adaptador para la lista de categorías
        String[] sProductCategories = getResources().getStringArray(R.array.product_categories);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, sProductCategories);
        mProductSelectCategory.setAdapter(categoryAdapter);

        // Adaptador para la lista de países
        String[] sCountries = getResources().getStringArray(R.array.product_country);
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, sCountries);
        mProductCountry.setAdapter(countryAdapter);

        // Si seleccionamos país USA, el símbolo de moneda pasa a dolares
        mProductCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 3){
                    mProductCurrency.setText(getString(R.string.tv_add_product_dollar));
                } else {
                    mProductCurrency.setText(getString(R.string.tv_add_product_euro));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Obtenemos el UID del usuario
        mAuth = FirebaseAuth.getInstance();
        sUserUID = mAuth.getUid();

        // Listeners para las acciones de la imagen y el botón
        mProductImage.setOnClickListener(this);
        mButtonAdd.setOnClickListener(this);

        // Si venimos de la vista de producto para editar el producto hacemos lo siguiente
        String productEditKey = getIntent().getStringExtra("key");
        if (productEditKey != null){
            // Referencia de la bbdd sobre el nodo "products"
            mDataBase = FirebaseDatabase.getInstance().getReference(getString(R.string.db_node_products));

            // Query para buscar el producto que coincide con la clave
            final Query previousProductData = mDataBase.orderByChild("key").equalTo(productEditKey);

            getProductValues(previousProductData);
        }

    }



    // Acciones de la imagen y el botón
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_addproduct_image:
                // Intent para seleccionar la imagen de la galería
                Intent pickPic = new Intent (Intent.ACTION_PICK);
                pickPic.setType("image/*");
                // Si la actividad es correcta se guardará la imagen seleccionada y obtendremos el nombre
                startActivityForResult(pickPic, GALLERY_INTENT);

                    break;
            case R.id.bt_addproduct_add:
                // Si todos los campos están rellenados
                if (!TextUtils.isEmpty(mProductTitle.getText().toString())) {
                    if (!TextUtils.isEmpty(mProductDescription.getText().toString())) {
                        if (!TextUtils.isEmpty(mProductPrice.getText().toString())) {
                            if (!TextUtils.isEmpty(mProductZip.getText().toString())) {
                                // Recogemos los datos del producto introducidos por el usuario
                                String title = mProductTitle.getText().toString();
                                String description = mProductDescription.getText().toString();
                                String category = mProductSelectCategory.getSelectedItem().toString();
                                String image = "";
                                String price = mProductPrice.getText().toString();
                                String country = mProductCountry.getSelectedItem().toString();
                                String zip = mProductZip.getText().toString();
                                String sellerUID = sUserUID;
                                String time = Calendar.getInstance().getTime().toString();
                                // Si el objeto Uri no es nulo, el usuario ha cargado una imagen y la asignamos al
                                // atributo del objteo p. En otro caso asignamos el nombre de la imagen por defecto
                                if (imageURI != null){
                                    image = imageURI.toString();
                                    // Creamos un nuevo Product y le pasamos los datos
                                    Product p = new Product(title, description, category, image, price,
                                            country, zip, sellerUID, time);
                                    // Guardamos la imagen el el Strorage llamando al métoddo addProduct con el Product "p" como parámetro
                                    // nos devuelve un string con la clave del producto para usarlo como nombre de la imagen
                                    // de esta manera podremos recuperar la imagen del Storage y asignarla al producto en las búsquedas
                                    //fbAdapter.addProduct(p);
                                    String productKey = fbAdapter.addProduct(p);
                                    StorageReference filePath = mStorage.child(getString(R.string.db_node_images)).child(productKey);
                                    filePath.putFile(imageURI);
                                    // Llevamos al usuario a la vista de producto ProductView
                                    Intent goToProductView = new Intent(getApplicationContext(), ProductView.class);
                                    goToProductView.putExtra("key", productKey);
                                    startActivity(goToProductView);
                                } else {
                                    // Si no se ha añadido una imagen
                                    // Hacemos lo mismo pero no guardamos nada en el Storage y la imagen será la imagen por defecto
                                    image = sImage;
                                    Product p = new Product(title, description, category, image, price,
                                            country, zip, sellerUID, time);
                                    String productKey = fbAdapter.addProduct(p);
                                    Intent goToProductView = new Intent(getApplicationContext(), ProductView.class);
                                    goToProductView.putExtra("key", productKey);
                                    startActivity(goToProductView);
                                }
                            } else {
                                Toast.makeText(AddProduct.this, getString(R.string.signup_no_zip),
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(AddProduct.this, getString(R.string.toast_product_no_price),
                                    Toast.LENGTH_LONG).show();
                        }
                    }  else {
                        Toast.makeText(AddProduct.this, getString(R.string.toast_product_no_description),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(AddProduct.this, getString(R.string.toast_product_no_title),
                            Toast.LENGTH_LONG).show();
                }
        } // fin del switch
    } // fin de onClick

    // Método que ejecuta el resultado de obtener la imagen de la galería
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Si el requestCode corresponde con el Intent de la Galería y el resultado es OK
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            // Usamos el objeto Uri para recoger la imagen
            imageURI = data.getData();
            // Asignamos la imagen al ImageView
            mProductImage.setImageURI(imageURI);
        } else {
            // Si el intent falla mostramos un toast al usuario y asignamos a sImage el nombre de la imagen por defecto
            Toast.makeText(AddProduct.this, getString(R.string.toast_upload_pic_fail),
                    Toast.LENGTH_LONG).show();
            sImage = "default_image.png";
        }

    }

    protected void getData(){
        Bundle extras = getIntent().getExtras();
        try {
            sParent = extras.getString("key");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para obtener los datos del producto seleccionado recibe un objeto Query
    private void getProductValues (Query selectedProductData){
        selectedProductData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Recorremos el DataSnapshot
                for (DataSnapshot datasnapshot: dataSnapshot.getChildren()) {
                    // Como solo debe haber un resultado, asignamos los valores a product
                    previousProduct = datasnapshot.getValue(Product.class);
                    // Rellenamos los EditText con los valores que acabamos de recoger en product
                    mProductTitle.setText(previousProduct.getTitle());
                    mProductDescription.setText(previousProduct.getDescription());
                    String category = previousProduct.getCategory();
                    int position = 0;
                    switch (category){
                        case "Vehicles":
                            position = 0;
                            break;
                        case "Home" :
                            position = 1;
                            break;
                        case "Technology" :
                            position = 2;
                    }
                    mProductSelectCategory.setSelection(position);
                    String country = previousProduct.getCategory();
                    int countryPosition = 0;
                    switch (category){
                        case "Spain":
                            countryPosition = 0;
                            break;
                        case "France" :
                            countryPosition = 1;
                            break;
                        case "Germany" :
                            countryPosition = 2;
                            break;
                        case "USA" :
                            countryPosition = 3;
                    }
                    mProductCountry.setSelection(countryPosition);
                    mProductZip.setText(previousProduct.getZip());
                    mProductPrice.setText(previousProduct.getPrice());
                    //sellerUID = previousProduct.getSellerUID();
                }
                Toast.makeText(getApplicationContext(), previousProductKey, Toast.LENGTH_LONG).show();

                mButtonAdd.setText(R.string.bt_profile_save);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://quicktrade-9d786.appspot.com/images/"+previousProductKey);
                try {
                    final File localFile = File.createTempFile("images", "jpeg");
                    storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            mProductImage.setImageBitmap(bitmap);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                        }
                    });
                } catch (IOException e ) {}



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
