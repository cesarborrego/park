package com.neology.parking_neo.dialogs;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.neology.parking_neo.MainActivity;
import com.neology.parking_neo.R;

/**
 * Created by Cesar Segura on 22/03/2017.
 */

public class PreciosPicker extends DialogFragment implements NumberPicker.OnValueChangeListener{

    public static PreciosPicker newInstance() {
        return new PreciosPicker();
    }

    NumberPicker numberPicker;
    RelativeLayout recargarPickerID;
    TextView cantidad;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.precios_picker, container, false);
        initElements(v);
        return v;
    }

    private void initElements(View v) {
        numberPicker = (NumberPicker)v.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(10);
        numberPicker.setMaxValue(100);
        numberPicker.setOnValueChangedListener(this);
        cantidad = (TextView)v.findViewById(R.id.cantidadRecargar);
        update(numberPicker.getValue());
        recargarPickerID = (RelativeLayout)v.findViewById(R.id.recargarPickerID);
        recargarPickerID.setOnClickListener(recargarListener);
    }

    View.OnClickListener recargarListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ((MainActivity)getActivity()).openBilling();
            dismiss();
        }
    };

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
        Log.d("Picker", "Valor "+i1);
        update(i1);
    }

    private void update(int i1) {
        cantidad.setText(getString(R.string.cantidad_recargar) + i1 + ".00");
    }
}
