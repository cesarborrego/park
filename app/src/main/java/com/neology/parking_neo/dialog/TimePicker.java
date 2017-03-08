package com.neology.parking_neo.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Cesar Segura on 07/03/2017.
 */

public class TimePicker extends DialogFragment implements android.app.TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c;
        int hour = 0;
        int minute = 0;
        c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        // Create a new instance of TimePicker and return it
        return new android.app.TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
    }
}
