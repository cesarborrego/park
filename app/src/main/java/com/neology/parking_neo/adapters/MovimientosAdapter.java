package com.neology.parking_neo.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.neology.parking_neo.R;
import com.neology.parking_neo.model.Detalle;
import com.neology.parking_neo.model.Movimientos;
import com.neology.parkingneology.Detalle_Activity;

import java.util.ArrayList;

/**
 * Created by Cesar Segura on 06/04/2017.
 */

public class MovimientosAdapter extends RecyclerView.Adapter<MovimientosAdapter.AdapterViewHolder>{
    ArrayList<Movimientos> movimientosArrayList;
    Activity c;

    public MovimientosAdapter(ArrayList<Movimientos> movimientosArrayList, Activity c) {
        this.movimientosArrayList = movimientosArrayList;
        this.c = c;
    }

    @Override
    public AdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_hstoriall, parent, false);
        return new AdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdapterViewHolder holder, int position) {
        Movimientos movimientos = movimientosArrayList.get(position);
        holder.nombreParki.setText("Parkimetro "+position);
        holder.tarjetaID.setText(movimientos.getStrTarjetaID());
        holder.montoID.setText("$"+movimientos.getMonto());
        holder.montoID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(c.getApplicationContext(), Detalle_Activity.class);
                c.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return movimientosArrayList.size();
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder {
        TextView nombreParki;
        TextView tarjetaID;
        TextView montoID;

        public AdapterViewHolder(View itemView) {
            super(itemView);
            nombreParki = (TextView)itemView.findViewById(R.id.nombreParquimetroID);
            tarjetaID = (TextView)itemView.findViewById(R.id.tarjetaID);
            montoID = (TextView)itemView.findViewById(R.id.montoID);
        }
    }
}
