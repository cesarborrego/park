package com.neology.parking_neo;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.neology.parking_neo.dialogs.PreciosPicker;

/**
 * Created by Cesar Segura on 28/02/2017.
 */

public class BottomSheetDataNFC extends BottomSheetDialogFragment {

    RelativeLayout recargarBtnId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
        initElements(v);
        return v;
    }

    private void initElements(View v) {
        recargarBtnId = (RelativeLayout)v.findViewById(R.id.recargarBtnID);
        recargarBtnId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    void showDialog() {
        // Create the fragment and show it as a dialog.
        DialogFragment newFragment = PreciosPicker.newInstance(1);
        newFragment.show(getFragmentManager(), "dialog");
    }
}
