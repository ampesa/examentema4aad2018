package com.apps.apene.quicktrade.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.apps.apene.quicktrade.AddProduct;
import com.apps.apene.quicktrade.MainActivity;
import com.apps.apene.quicktrade.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// Clase que contiene los métodos de acceso a Firebase
public class FirebaseAdapter {

    protected int mResult = 0;
    // Objeto FirebaseAuth
    private FirebaseAuth mAuth;
    // Objeto FirebaseUser para añadir el uid al registro en la bbdd
    private FirebaseUser mUser;

    private String userUID = null;
    // Objeto Base de Datos
    DatabaseReference mDataBase;
    // Contexto de la aplicación que usa la base de datos
    private final Context context;
    User noPassUser = new User();

    // Constructor
    public FirebaseAdapter (Context c) {
        context = c;
        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance().getReference(context.getString(R.string.db_node_users));
    }

    // Método para registrar usuario con correo y contraseña en Firebase Authentication
    public int signUp(final User u){
        // Recogemos email y password introducidos por el usuario
        String email = u.getEmail();
        String password = u.getPass();
        // Utilizamos el método createUserWith... para crear el usuario en Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            mUser = mAuth.getCurrentUser();
                            // Obtenemos el UID del nuevo usuario y lo añadimos al objeto User u
                            userUID = mUser.getUid();
                            u.setUid(userUID);
                            u.setPass("");
                            // Guardamos los datos del usuario en Firebase Realtime Database incluyendo el UID
                            saveUser(u);
                            mResult = 1;
                            Toast.makeText(context, context.getString(R.string.signup_ok)+ mUser.getUid(),
                                    Toast.LENGTH_SHORT).show();
                            // Como el registro es correcto llevamos al usuario a la actividad principal
                            Intent goToMain = new Intent(context.getApplicationContext(), MainActivity.class);
                            context.startActivity(goToMain);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(context, context.getString(R.string.signup_fail) + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            mResult = 0;
                        }
                    }
                });
        return mResult;
    }

    // Método para guardar datos de un nuevo usuario en Firebase Real Time Database
    public int saveUser(User u){
        // Obtenemos la siguiente clave de la bbdd
        String key = mDataBase.push().getKey();
        // Añadimos el User "u" como hijo del nodo referenciado en el constructor
        mDataBase.child(key).setValue(u);
        return 1;
    } // fin método saveUser()

    // Método para actualizar los datos de un usuario
    public int updateUser(User u){
        // Obtenemos la siguiente clave de la bbdd
        String key = mDataBase.push().getKey();
        // Añadimos el User "u" como hijo del nodo referenciado en el constructor
        mDataBase.child(key).setValue(u);
        return 1;
    } // fin método saveUser()

    // Método para registrar usuario con correo y contraseña en Firebase Authentication
    public int login (final User u){
        // Recogemos email y password introducidos por el usuario
        String email = u.getEmail();
        String password = u.getPass();
        // Utilizamos el método createUserWith... para crear el usuario en Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            mUser = mAuth.getCurrentUser();
                            // Obtenemos el UID del nuevo usuario y lo añadimos al objeto User u
                            userUID = mUser.getUid();
                            u.setUid(mUser.getUid());
                            mResult = 1;
                            Toast.makeText(context, context.getString(R.string.login_ok)+ mUser.getUid(),
                                    Toast.LENGTH_SHORT).show();
                            // Como el login es correcto llevamos al usuario a la actividad principal
                            Intent goToMain = new Intent(context.getApplicationContext(), MainActivity.class);
                            context.startActivity(goToMain);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(context, context.getString(R.string.login_fail) + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            mResult = 0;
                        }
                    }
                });
        return mResult;
    }

    // Método para enviar un correo de modificación de contraseña al usuario
    public void resetPassword (User u){
        String email = u.getEmail();
        mAuth.sendPasswordResetEmail(email);
    }

    // Método para guardar datos de un nuevo usuario en Firebase Real Time Database
    public String addProduct(Product p){
        // Situamos el adaptador en el nodo products
        mDataBase = FirebaseDatabase.getInstance().getReference(context.getString(R.string.db_node_products));
        // Obtenemos la siguiente clave de la bbdd
        String key = mDataBase.push().getKey();
        // Añadimos la clave al producto
        p.setKey(key);
        // Añadimos el Product "p" como hijo del nodo referenciado
        mDataBase.child(key).setValue(p);
        // Mostramos un Toast confirmando que se ha añadido el producto
        Toast.makeText(context, context.getString(R.string.toast_product_add_ok),
                Toast.LENGTH_LONG).show();
        return key;
    } // fin método addProduct()

    /*
    // Registramos el usuario en segundo plano pasando un objeto User con los datos introducidos y
    // obtenemos un objteo FirebaseUser con el que confirmaremos el método saveUser
    public class FirebaseAsyncTask extends AsyncTask<User, Void, FirebaseUser> {

        @Override
        protected FirebaseUser doInBackground(User... users) {
            User user = users[0];
            String email = user.getEmail();
            String password = user.getPass();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                mUser = mAuth.getCurrentUser();
                                //mResult = 1;
                            } else {
                                // If sign in fails, display a message to the user.
                                //mResult = 0;
                            }

                        }
                    });
            //return mResult;

            return mUser;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(FirebaseUser firebaseUser) {
            super.onPostExecute(firebaseUser);
            mUser = firebaseUser;
            userUID = mUser.getUid();
        }
    }*/
}
