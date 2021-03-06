package com.comerciomovil.ecommerce.borrega.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.comerciomovil.ecommerce.borrega.R;
import com.comerciomovil.ecommerce.borrega.ui.activities.AddShippingAddressActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class NumIntFragment extends Fragment {

    private TextView text_area;
    private int mPageNumber;
    private EditText text_num_int;


    public String getData(){ return text_num_int.getText().toString();}

    public NumIntFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(AddShippingAddressActivity.ARG_PAGE);
    }

    public static NumIntFragment create(int pageNumber) {
        NumIntFragment fragment = new NumIntFragment();
        Bundle args = new Bundle();
        args.putInt(AddShippingAddressActivity.ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup view = (ViewGroup) inflater
                .inflate(R.layout.fragment_num_int, container, false);
        text_num_int = (EditText) view.findViewById(R.id.text_num_int);
        text_num_int.requestFocus();
        text_area = (TextView) view.findViewById(R.id.text_area);
        return view;
    }

    public void setError(String error){
        text_num_int.setError(error);
    }

    @Override
    public void onResume() {
        super.onResume();
        text_area.setText(AddShippingAddressActivity.getShipping_point().getGooglePlace());
    }
}
