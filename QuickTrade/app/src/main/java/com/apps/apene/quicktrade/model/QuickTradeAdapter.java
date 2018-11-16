package com.apps.apene.quicktrade.model;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.apene.quicktrade.ProductView;
import com.apps.apene.quicktrade.R;

import java.util.ArrayList;
import java.util.List;

public class QuickTradeAdapter extends RecyclerView.Adapter<QuickTradeAdapter.QuickTradeViewHolder>{

    protected List<Product> mProducts = null;

    // Constructor de la clase, recibe un List y lo asigna a mCharacterDB
    public QuickTradeAdapter(List<Product> productsList){
        // Le asignamos a mCharacterDB el ArrayList con la carga de la base de datos recibida desde MainActivity
        mProducts = new ArrayList<Product>(productsList);
    }

    @NonNull
    @Override
    public QuickTradeAdapter.QuickTradeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.quick_trade_view_holder_layout, viewGroup, false);

        // El médoto devuelve el ViewHolder "inflado"
        return new QuickTradeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuickTradeAdapter.QuickTradeViewHolder quickTradeViewHolder, int position) {
        Product product = mProducts.get(position);
        String title = product.getTitle();
        String price = product.getPrice();
        String currency = "€";
        String productData = title + "\n" + price;

        quickTradeViewHolder.mProductData.setText(productData);
    }


    @Override
    public int getItemCount() {
        return 0;
    }

    public class QuickTradeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView mProductImage = null;
        protected TextView mProductData = null;
        protected Context mContext = null;
        protected String mProductKey = null;

        public QuickTradeViewHolder(@NonNull View itemView) {
            super(itemView);

            mProductImage = itemView.findViewById(R.id.iv_results);
            mProductData = itemView.findViewById(R.id.tv_results);
            mProductImage.setOnClickListener(this);
            mProductData.setOnClickListener(this);

            mContext = itemView.getContext();
        }

        @Override
        public void onClick(View v) {

            int viewId = v.getId();

            if (viewId == mProductImage.getId() || viewId == mProductData.getId()){
                // Si pulsa en el icono o en el personaje lanzamos el método showCharacterDetails
                showProductDetails();
            }

        }

        public void showProductDetails(){

            Intent showProduct = new Intent(mContext, ProductView.class);
            // Pasamos el int al Bundle
            showProduct.putExtra("key", mProductKey);
            // Iniciamos la actividad
            mContext.startActivity(showProduct);
        }
    }
}
