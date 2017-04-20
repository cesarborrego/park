package com.neology.parking_neo.dialogs;

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

public class PreciosPicker extends DialogFragment implements NumberPicker.OnValueChangeListener {
    int typeMsg;

    public static PreciosPicker newInstance(int typeMsg) {
        PreciosPicker frag = new PreciosPicker();
        Bundle args = new Bundle();
        args.putInt("typeMsg", typeMsg);
        frag.setArguments(args);
        if (typeMsg == 3) {
            frag.setCancelable(false);
        }
        return frag;
    }

    NumberPicker numberPicker;
    RelativeLayout recargarPickerID;
    TextView cantidad;
    int iMonto;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        typeMsg = getArguments().getInt("typeMsg");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.precios_picker, container, false);
        initElements(v);
        return v;
    }

    private void initElements(View v) {
        numberPicker = (NumberPicker) v.findViewById(R.id.numberPicker);

        if (typeMsg == 2 || typeMsg == 3) {
            int start = 1;
            String[] numbers = new String[12];
            for (int i = 0; i < 12; i++) {
                numbers[i] = start + "";
                if (start == 1) {
                    start = start + 4;
                } else {
                    start = start + 5;
                }
            }
            numberPicker.setMaxValue(12);
            numberPicker.setMinValue(1);
            numberPicker.setDisplayedValues(numbers);
        } else {
            numberPicker.setMaxValue(100);
            numberPicker.setMinValue(10);
        }

        numberPicker.setOnValueChangedListener(this);
        cantidad = (TextView) v.findViewById(R.id.cantidadRecargar);
        update(numberPicker.getValue());
        recargarPickerID = (RelativeLayout) v.findViewById(R.id.recargarPickerID);
        recargarPickerID.setOnClickListener(recargarListener);
    }

    View.OnClickListener recargarListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //typeMsg la reuso porque solo necesito saber si es 1 o 2
            //la uso principalmente para mandar el tipo de mensaje al dialog pero tmb la mandare para el post
            if (typeMsg == 3) {
                ((MainActivity) getActivity()).launchAlarm(iMonto);
            } else {
                ((MainActivity) getActivity()).openBilling(iMonto, typeMsg);
            }
            dismiss();
        }
    };

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
        Log.d("Picker", "Valor " + i1);
        update(i1);
    }

    private void update(int i1) {

        switch (typeMsg) {
            case 1:
                cantidad.setText(getString(R.string.cantidad_recargar) + i1 + ".00");
                break;
            case 2:
                i1 = i1 * 7;
                cantidad.setText(getString(R.string.cantidad_comprar) + i1 + ".00");
                break;
            case 3:
                cantidad.setText(getString(R.string.tiempo_alarma) + i1 + " mins");
            default:
                break;

        }
        iMonto = i1;
    }
}
