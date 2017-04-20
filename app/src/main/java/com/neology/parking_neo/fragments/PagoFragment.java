package com.neology.parking_neo.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.neology.parking_neo.MainActivity;
import com.neology.parking_neo.R;
import com.neology.parking_neo.Services.AlarmReceiver;
import com.neology.parking_neo.VolleyApp;
import com.neology.parking_neo.adapters.MovimientosAdapter;
import com.neology.parking_neo.model.Movimientos;
import com.neology.parking_neo.util_vending.IabHelper;
import com.neology.parking_neo.util_vending.IabResult;
import com.neology.parking_neo.util_vending.Inventory;
import com.neology.parking_neo.util_vending.Purchase;
import com.neology.parking_neo.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Cesar Segura on 24/02/2017.
 */

public class PagoFragment extends Fragment {

    private static final String TIME_PATTERN = "HH:mm";

    private TextView lblDate;
    private TextView lblTime;
    private Button datePickerBtn;
    private LinearLayout timePickerBtn, comprarBtn;
    private Button cancelarAlarmaBtn;
    private Calendar calendar;
    private DateFormat dateFormat;
    private SimpleDateFormat timeFormat;


    private RecyclerView mRecyclerView;
    MovimientosAdapter movimientosAdapter;
    ArrayList<Movimientos> movimientosArrayList;
    Movimientos movimientos;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMovimientos();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.pago_fragment, container, false);
        initRecycler(v);
        return v;
    }

    private void initRecycler(View v) {
        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    public void getMovimientos() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                Constants.MOVIMIENTOS_URL + "RFID-001",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Movimientos", response.toString());
                        new readMovimientosJson().execute(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Movimientos", error.toString());
//                        getString(R.string.errorHost)+" "+sharedPreferences.getString(Constants_Settings.KEY_URL, null)
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
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

    class readMovimientosJson extends AsyncTask<JSONObject, Void, ArrayList<Movimientos>> {

        @Override
        protected ArrayList<Movimientos> doInBackground(JSONObject... jsonObjects) {
            JSONObject jsonObject = jsonObjects[0];
            try {
                int code = jsonObject.getInt("code");
                switch (code) {
                    case 200:
                        movimientosArrayList = new ArrayList<Movimientos>();
                        JSONArray array = jsonObject.getJSONArray("list");
                        for (int i = 0; i<array.length(); i++) {
                            JSONObject jsonObject1 = array.getJSONObject(i);
                            JSONObject jsonObject2 = jsonObject1.getJSONObject("tipoMovimientos");
                            movimientos = new Movimientos(
                                    jsonObject1.getString("strTarjetaID"),
                                    jsonObject1.getLong("dFechaMovimiento"),
                                    jsonObject1.getInt("iMonto"),
                                    jsonObject2.getString("description"));
                            movimientosArrayList.add(movimientos);
                        }
                        break;
                    case 400:
                        break;
                    default:
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return movimientosArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Movimientos> list) {
            super.onPostExecute(list);
            if(list!=null) {
                movimientosAdapter = new MovimientosAdapter(list, getActivity());
                movimientosAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(movimientosAdapter);
            }
        }
    }
}