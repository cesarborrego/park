package com.neology.parking_neo.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.neology.parking_neo.MainActivity;
import com.neology.parking_neo.R;
import com.neology.parking_neo.Services.AlarmReceiver;
import com.neology.parking_neo.util_vending.IabHelper;
import com.neology.parking_neo.util_vending.IabResult;
import com.neology.parking_neo.util_vending.Inventory;
import com.neology.parking_neo.util_vending.Purchase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Cesar Segura on 24/02/2017.
 */

public class PagoFragment extends Fragment
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String TIME_PATTERN = "HH:mm";

    private TextView lblDate;
    private TextView lblTime;
    private Button datePickerBtn;
    private LinearLayout timePickerBtn, comprarBtn;
    private Button cancelarAlarmaBtn;
    private Calendar calendar;
    private DateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    Intent my_intent;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    private static final String TAG = "com.example.inappbilling";
    IabHelper mHelper;
    static final String SKU = "android.test.purchased";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();
        dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());
        my_intent = new Intent(getActivity().getApplicationContext(), AlarmReceiver.class);
        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        connectGooglePlay();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.pago_fragment, container, false);
        lblDate = (TextView) v.findViewById(R.id.lblDate);
        lblTime = (TextView) v.findViewById(R.id.lblTime);
        timePickerBtn = (LinearLayout) v.findViewById(R.id.btnTimePicker);
        timePickerBtn.setOnClickListener(timeListener);
        comprarBtn = (LinearLayout) v.findViewById(R.id.comprarID);
        comprarBtn.setOnClickListener(comprarListener);
        update();
        return v;
    }

    private void update() {
        lblDate.setText(dateFormat.format(calendar.getTime()));
        lblTime.setText(timeFormat.format(calendar.getTime()));
    }

    View.OnClickListener timeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showTimePicker();
        }
    };

    View.OnClickListener cancelarAlarmaListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            alarmManager.cancel(pendingIntent);
            my_intent.putExtra("extra", "alarm off");
            getActivity().sendBroadcast(my_intent);
        }
    };

    private void showDatePicker() {
        DatePickerDialog.newInstance(this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show(getActivity().getFragmentManager(), "datePicker");
    }

    private void showTimePicker() {
        TimePickerDialog.newInstance(this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true)
                .show(getActivity().getFragmentManager(), "timePicker");
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year, monthOfYear, dayOfMonth);
        update();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        update();
        my_intent.putExtra("extra", "alarm on");
        pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(),
                0,
                my_intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void connectGooglePlay() {
        mHelper = new IabHelper(getContext(), getResources().getString(R.string.api_key_billing));
        mHelper.enableDebugLogging(true, TAG);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "In-app Billing setup failed: " +
                            result);
                } else {
                    Log.d(TAG, "In-app Billing is set up OK");
                }
            }
        });
    }


    View.OnClickListener comprarListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ((MainActivity)getActivity()).openBilling();
        }
    };

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase) {
            if (result.isFailure()) {
                // Handle error
                return;
            } else if (purchase.getSku().equals(SKU)) {
                consumeItem();
                comprarBtn.setEnabled(false);
            }
        }
    };

    public void consumeItem() {
        try {
            mHelper.queryInventoryAsync(mReceivedInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {


            if (result.isFailure()) {
                // Handle failure
            } else {
                try {
                    mHelper.consumeAsync(inventory.getPurchase(SKU),
                            mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result) {

                    if (result.isSuccess()) {
                        comprarBtn.setEnabled(true);
                    } else {
                        // handle error
                    }
                }
            };


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) try {
            mHelper.dispose();
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
        mHelper = null;
    }
}