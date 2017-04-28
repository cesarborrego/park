package com.neology.parking_neo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.neology.parking_neo.Services.AlarmReceiver;
import com.neology.parking_neo.Services.TimerService;
import com.neology.parking_neo.adapters.ViewPagerAdapter;
import com.neology.parking_neo.dialogs.PreciosPicker;
import com.neology.parking_neo.fragments.MapFragment;
import com.neology.parking_neo.fragments.PagoFragment;
import com.neology.parking_neo.rest.Api_Parking;
import com.neology.parking_neo.util_vending.IabHelper;
import com.neology.parking_neo.util_vending.IabResult;
import com.neology.parking_neo.util_vending.Inventory;
import com.neology.parking_neo.util_vending.Purchase;
import com.neology.parking_neo.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private int[] tabIcons = {
            R.drawable.icon_park,
            R.drawable.icon_pago
    };
    private IabHelper mHelper;

    int iMonto = 0;
    int montoActualizado = 0;
    int tipoMovimiento = 0;
    private Calendar calendar;
    private Intent my_intent;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendar = Calendar.getInstance();
        my_intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        /*
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        */
        connectGooglePlay();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcon();
        getDataTarjeta();
    }

    private void setupTabIcon() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    public void launchAlarm(int minutos_alarma) {
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + minutos_alarma);
        my_intent.putExtra("extra", "alarm on");
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                0,
                my_intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MapFragment(), "ONE");
        adapter.addFragment(new PagoFragment(), "TWO");
        viewPager.setAdapter(adapter);
        /*
        int index = viewPager.getCurrentItem();
        android.support.v4.app.Fragment currentFragment = adapter.getItem(index);*/
    }

    private void connectGooglePlay() {
        mHelper = new IabHelper(this, getResources().getString(R.string.api_key_billing));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void openBilling(int iMonto, int tipoMovimiento) {
        this.iMonto = iMonto;
        this.tipoMovimiento = tipoMovimiento;
        try {
            Random random = new Random();
            int requestCode = random.nextInt(65535);
            if (requestCode < 0) {
                requestCode = requestCode * -1;
            }
            mHelper.launchPurchaseFlow(this, Constants.SKU, requestCode,
                    mPurchaseFinishedListener, "mypurchasetoken");
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase) {
            if (result.isFailure()) {
                // Handle error
                return;
            } else if (purchase.getSku().equals(Constants.SKU)) {
                consumeItem();
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
                    mHelper.consumeAsync(inventory.getPurchase(Constants.SKU),
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
                        //LANZAR EL SERVICIO PARA ACTUALIZAR EL SALDO Y ACTUALIZAR LA UI CON EL SALDO
                        insertarMovimiento("");
                    } else {
                        // handle error
                    }
                }
            };

    private void insertarMovimiento(final String strTarjetaID) {
        /*
        "strTarjetaID":"nfc_parki",
        "dFechaMovimiento": "1464217068218",
        "iMonto": "30",
        "tipoMovimiento": "1"
         */

        long millis = new java.util.Date().getTime();

        JSONObject js = new JSONObject();
        try {
            js.put("strTarjetaID", "RFID-001");
            js.put("dFechaMovimiento", millis);
            js.put("iMonto", iMonto);
            js.put("tipoMovimiento", tipoMovimiento);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, Constants.ACTUALIZAR_TARJETA_URL, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("RESPUESTA SERVICIO POST", response.toString());
                        readJson(response, tipoMovimiento);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(null, "Error: " + error.getMessage());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        VolleyApp.getmInstance().addToRequestQueue(jsonObjReq);
    }

    private void readJson(JSONObject jsonObject, int tipoMovimiento) {
        try {
            JSONObject jsonObject1 = jsonObject.getJSONObject("object");
            montoActualizado = jsonObject1.getInt("iSaldo");
            MapFragment.saldoTxt.setText("$" + montoActualizado + ".00");
            if (tipoMovimiento == 2) {
                //lanzar picker para alarma
                showTimePicker(3);
                startService(new Intent(getApplicationContext(), TimerService.class));
                Log.i(TAG, "Started service");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent); // or whatever method used to update your GUI fields
        }
    };

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            long millisUntilFinished = intent.getLongExtra("countdown", 0);
            Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
            MapFragment.contador.setText("Tiempo restante "+ millisUntilFinished / 1000);
            MapFragment.cajaContador.setVisibility(View.VISIBLE);
        }
    }

    private void showTimePicker(int tipoMovimiento) {
        // Create the fragment and show it as a dialog.
        DialogFragment newFragment = PreciosPicker.newInstance(tipoMovimiento);
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    public void getDataTarjeta() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                Constants.TARJETA_URL + "RFID-001",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Datos tarjeta", response.toString());
                        new readTarjetaJson().execute(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Tarjeta", error.toString());
//                        getString(R.string.errorHost)+" "+sharedPreferences.getString(Constants_Settings.KEY_URL, null)
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //return Auth();
                return super.getHeaders();
            }
        };
        VolleyApp.getmInstance().addToRequestQueue(jsonObjectRequest);
    }

    class readTarjetaJson extends AsyncTask<JSONObject, Void, Boolean> {
        boolean respuesta = false;
        int saldoRegistrado = 0;

        @Override
        protected Boolean doInBackground(JSONObject... jsonObjects) {
            JSONObject jsonObject = jsonObjects[0];
            try {
                int code = jsonObject.getInt("code");
                switch (code) {
                    case 200:
                        JSONObject jsonObject1 = jsonObject.getJSONObject("parquimetros");
                        saldoRegistrado = jsonObject1.getInt("iSaldo");
                        respuesta = true;
                        break;
                    case 400:
                        respuesta = false;
                        break;
                    default:
                        respuesta = false;
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return respuesta;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                MapFragment.saldoTxt.setText("$" + saldoRegistrado + ".00");
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(br);
        Log.i(TAG, "Unregistered broacast receiver");
    }

    @Override
    public void onStop() {
        try {
            unregisterReceiver(br);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        registerReceiver(br, new IntentFilter(TimerService.COUNTDOWN_BR));
        super.onResume();
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, TimerService.class));
        Log.i(TAG, "Stopped service");
        super.onDestroy();
        if (mHelper != null) try {
            mHelper.dispose();
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
        mHelper = null;
    }
}
