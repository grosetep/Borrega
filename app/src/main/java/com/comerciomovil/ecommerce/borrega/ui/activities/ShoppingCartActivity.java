package com.comerciomovil.ecommerce.borrega.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.comerciomovil.ecommerce.borrega.R;
import com.comerciomovil.ecommerce.borrega.items.CartProductItem;
import com.comerciomovil.ecommerce.borrega.items.UserItem;
import com.comerciomovil.ecommerce.borrega.model.ApiException;
import com.comerciomovil.ecommerce.borrega.model.DetailPublication;
import com.comerciomovil.ecommerce.borrega.model.ShoppingCart;
import com.comerciomovil.ecommerce.borrega.requests.CartRequest;
import com.comerciomovil.ecommerce.borrega.requests.UpdateShoppingCartRequest;
import com.comerciomovil.ecommerce.borrega.responses.GenericResponse;
import com.comerciomovil.ecommerce.borrega.responses.GetCartResponse;
import com.comerciomovil.ecommerce.borrega.retrofit.RestServiceWrapper;
import com.comerciomovil.ecommerce.borrega.tools.Connectivity;
import com.comerciomovil.ecommerce.borrega.tools.Constants;
import com.comerciomovil.ecommerce.borrega.tools.GeneralFunctions;
import com.comerciomovil.ecommerce.borrega.tools.ShowConfirmations;
import com.comerciomovil.ecommerce.borrega.tools.StringOperations;
import com.comerciomovil.ecommerce.borrega.ui.adapters.ShoppingCartAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class ShoppingCartActivity extends AppCompatActivity {
    private static final String TAG = ShoppingCartActivity.class.getSimpleName();
    private ArrayList<CartProductItem> products=new ArrayList();
    private FrameLayout container_loading;
    private RecyclerView recyclerview;
    private DetailPublication detail;
    private LinearLayoutManager llm;
    private RelativeLayout no_connection_layout;
    private LinearLayout layout_actions;
    private AppCompatButton button_retry;
    private ShoppingCartAdapter mAdapter;
    private LinearLayout button_next;
    private ProgressBar pbLoading;
    private TextView text_total;
    private Float total = 0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        Intent i = getIntent();
        detail = (DetailPublication)i.getSerializableExtra(Constants.SELECTED_PRODUCT);
        init();
        final Toolbar toolbar_shipping = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar_shipping);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Connectivity.isNetworkAvailable(getApplicationContext())) {
            products.clear();
            initProcess(true);
            getShoppingCart();
        }else{
            showNoConnectionLayout();
        }
    }
    private void init(){
        container_loading = (FrameLayout) findViewById(R.id.container_loading);
        no_connection_layout = (RelativeLayout) findViewById(R.id.no_connection_layout);
        layout_actions = (LinearLayout) findViewById(R.id.layout_actions);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setHasFixedSize(true);
        button_retry = (AppCompatButton) findViewById(R.id.button_retry);
        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initProcess(true);
                getShoppingCart();
            }
        });
        button_next = (LinearLayout) findViewById(R.id.button_next);
        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (products!=null && products.size()>0) {// por lo menos hay un producto en carrito
                //update shopping cart num items before next step: mandar lista de productos para actualizar el carrito
                //updateShoppingCart();
                updateShoppingCartOnServer();

            }else{
                ShowConfirmations.showConfirmationMessage(getString(R.string.promt_shopping_cart_empty), ShoppingCartActivity.this);
            }

            }
        });
        pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        text_total = (TextView) findViewById(R.id.text_total);
    }
    private void initProcessUpdate(boolean flag){
        pbLoading.setVisibility(flag? View.VISIBLE: View.INVISIBLE);
    }
    private void startShipping(){
        Intent i = new Intent(getApplicationContext(), ShippingActivity.class);
        Bundle args = new Bundle();
        ShoppingCart shopping_cart = new ShoppingCart();
        shopping_cart.setProducts(products);
        shopping_cart.setTotal(getTotal());
        shopping_cart.setId_cart(products.get(0).getIdCart());
        args.putSerializable(Constants.SELECTED_SHOPPING_CART, shopping_cart);
        i.putExtras(args);
        startActivity(i);
    }
    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    private void initProcess(boolean flag){
        container_loading.setVisibility(flag? View.VISIBLE: View.GONE);
        layout_actions.setVisibility(flag? View.GONE: View.VISIBLE);
        recyclerview.setVisibility(flag? View.GONE: View.VISIBLE);
        no_connection_layout.setVisibility(View.GONE);
    }
    private void onSuccess(){
        initProcess(false);
        setupAdapter();
        calculateTotal();
    }
    private void showNoConnectionLayout(){
        container_loading.setVisibility(View.GONE);
        layout_actions.setVisibility(View.GONE);
        recyclerview.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.VISIBLE);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void setupAdapter(){
        // Set the adapter
        mAdapter = new ShoppingCartAdapter(this,products);
        recyclerview.setAdapter(mAdapter);
        recyclerview.invalidate();
        if (recyclerview.getLayoutManager()==null){
            recyclerview.setLayoutManager(llm);
        }
    }
    /*private void updateShoppingCart(){
        boolean make_update = false;
        if (products!=null && products.size()>0){
            for(int i=0;i<products.size();i++){
                if (!products.get(i).getUnits().equals(String.valueOf(Constants.uno))){
                    make_update = true;
                    break;
                }
            }
            //check for updates
            if (make_update){
                updateShoppingCartOnServer();
            }else{
                startShipping();
            }
        }
    }*/
    private String getCommaSeparatedString(ArrayList<CartProductItem> products){
        StringBuffer buffer = new StringBuffer();
        if (products!=null && products.size()>0) {
            for (CartProductItem p : products){
                buffer.append(p.getIdProduct());
                buffer.append(",");
                buffer.append(p.getUnits());
                buffer.append(",");
            }
            //delete last comma
            buffer.deleteCharAt(buffer.length()-1);
        }
        //Log.d(TAG,"carrito:"+buffer.toString());
        return buffer.toString();
    }
    private void updateShoppingCartOnServer(){
        UserItem user = GeneralFunctions.getCurrentUser(getApplicationContext());
        //show loading
        initProcessUpdate(true);

        UpdateShoppingCartRequest request = new UpdateShoppingCartRequest();
        request.setIdUser(user!=null?user.getIdUser():"0");
        request.setProducts(getCommaSeparatedString(products));
        RestServiceWrapper.updateShoppingCart(request, new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, retrofit2.Response<GenericResponse> response) {
                if (response != null && response.isSuccessful()) {
                    GenericResponse update_response = response.body();
                    if (update_response != null && update_response.getStatus().equals(Constants.success)) {
                        if (update_response.getResult() != null && update_response.getResult().getStatus().equals(String.valueOf(Constants.uno))) {
                            Log.d(TAG, "Respuesta:" + update_response.getResult().getMessage());
                            initProcessUpdate(false);
                            startShipping();
                        } else {
                            initProcessUpdate(false);
                            String response_error = update_response.getMessage();
                            ShowConfirmations.showConfirmationMessage(response_error, ShoppingCartActivity.this);
                        }

                    } else if (update_response != null && update_response.getStatus().equals(Constants.no_data)) {
                        String response_error = update_response.getMessage();
                        initProcessUpdate(false);
                        ShowConfirmations.showConfirmationMessage(response_error, ShoppingCartActivity.this);
                    } else {
                        initProcessUpdate(false);
                        String response_error = update_response.getMessage();
                        ShowConfirmations.showConfirmationMessage(response_error, ShoppingCartActivity.this);
                    }

                } else {
                    ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, getString(R.string.error_generic)), ShoppingCartActivity.this);
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Log.d(TAG, "ERROR: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                ApiException apiException = new ApiException();
                try {
                    initProcessUpdate(false);
                    apiException.setMessage(t.getMessage());
                    ShowConfirmations.showConfirmationMessage(apiException.getMessage(), ShoppingCartActivity.this);
                } catch (Exception ex) {
                    // do nothing
                }
            }
        });
    }
    private void getShoppingCart(){
        UserItem user = GeneralFunctions.getCurrentUser(getApplicationContext());
        RestServiceWrapper.getShoppingCart(user!=null?user.getIdUser():"0",new Callback<GetCartResponse>() {
            @Override
            public void onResponse(Call<GetCartResponse> call, retrofit2.Response<GetCartResponse> response) {
                if (response != null && response.isSuccessful()) {
                    GetCartResponse products_response = response.body();
                    if (products_response != null && products_response.getStatus().equals(Constants.success)) {


                       if (products_response.getResult()!=null) {
                           if (products_response.getResult().size() > 0) {
                               products.addAll(products_response.getResult());
                           }
                       }

                    } else if (products_response != null && products_response.getStatus().equals(Constants.no_data)){
                        String response_error = response.body().getMessage();
                        ShowConfirmations.showConfirmationMessage(response_error,ShoppingCartActivity.this);
                        Log.d(TAG, "Mensage:" + response_error);
                    }else{
                        String response_error = response.message();
                        ShowConfirmations.showConfirmationMessage(response_error,ShoppingCartActivity.this);
                        Log.d(TAG, "Error:" + response_error);
                    }

                    onSuccess();
                }else{
                    ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login,getString(R.string.error_generic)),ShoppingCartActivity.this);
                }
            }
            @Override
            public void onFailure(Call<GetCartResponse> call, Throwable t) {
                Log.d(TAG, "ERROR: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                ApiException apiException = new ApiException();
                try {
                    apiException.setMessage(t.getMessage());
                    ShowConfirmations.showConfirmationMessage(apiException.getMessage(),ShoppingCartActivity.this);
                } catch (Exception ex) {
                    // do nothing
                }
            }
        });
    }

    public void calculateTotal(){
        Float total = 0f;
        Float price = 0f;
        if (products!=null && products.size()>0){
            for (CartProductItem product:products) {
                if (product.getOfferPrice()!=null && !product.getOfferPrice().isEmpty()){
                    price = Float.parseFloat(product.getOfferPrice()) * Integer.parseInt(product.getUnits().trim());
                }else{
                    price = Float.parseFloat(product.getRegularPrice()) * Integer.parseInt(product.getUnits().trim());
                }
                total = total + price;
            }

        }
        setTotal(total);
        text_total.setText(StringOperations.getAmountFormat(String.valueOf(total)));
    }

    public  void removeFromCart( final int position) {
        UserItem user = GeneralFunctions.getCurrentUser(getApplicationContext());
        CartProductItem item = products.get(position);
        CartRequest request = new CartRequest();
        request.setId_product(item.getIdProduct());
        request.setId_user(user!=null?user.getIdUser():"0");
        request.setUnits(item.getUnits());
        request.setOperation("del");

        RestServiceWrapper.shoppingCart(request,new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, retrofit2.Response<GenericResponse> response) {
                if (response != null && response.isSuccessful()) {
                    GenericResponse cart_response = response.body();
                    if (cart_response != null && cart_response.getStatus().equals(Constants.success)) {
                        if (cart_response.getResult().getStatus().equals(String.valueOf(Constants.uno))) {
                            ShowConfirmations.showConfirmationMessageShort(cart_response.getResult().getMessage(), ShoppingCartActivity.this);
                            mAdapter.removeItem(position);
                            mAdapter.notifyDataSetChanged();
                            calculateTotal();
                        }else{
                            ShowConfirmations.showConfirmationMessage(cart_response.getResult().getMessage(), ShoppingCartActivity.this);
                        }
                    } else if (cart_response != null && cart_response.getStatus().equals(Constants.no_data)){
                        String response_error = response.body().getMessage();
                        Log.d(TAG, "Mensage:" + response_error);
                        ShowConfirmations.showConfirmationMessage(response_error, ShoppingCartActivity.this);
                    }else{
                        String response_error = response.message();
                        Log.d(TAG, "Error:" + response_error);
                        ShowConfirmations.showConfirmationMessage(response_error, ShoppingCartActivity.this);
                    }
                }else{
                    ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login,getString(R.string.error_generic)),ShoppingCartActivity.this);
                }
            }
            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Log.d(TAG, "ERROR: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                ApiException apiException = new ApiException();
                try {
                    apiException.setMessage(t.getMessage());
                    ShowConfirmations.showConfirmationMessage(apiException.getMessage(), ShoppingCartActivity.this);
                } catch (Exception ex) {
                    // do nothing
                }
            }
        });




    }
}
