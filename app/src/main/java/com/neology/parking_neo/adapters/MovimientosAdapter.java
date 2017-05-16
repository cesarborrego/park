package com.neology.parking_neo.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.android.gms.maps.model.LatLng;
import com.neology.parking_neo.R;
import com.neology.parking_neo.VolleyApp;
import com.neology.parking_neo.model.Movimientos;
import com.neology.parking_neo.utils.Constants;
import com.neology.parkingneology.Detalle_Activity;

import java.util.ArrayList;

/**
 * Created by Cesar Segura on 06/04/2017.
 */

public class MovimientosAdapter extends RecyclerView.Adapter<MovimientosAdapter.AdapterViewHolder> {
    ArrayList<Movimientos> movimientosArrayList;
    Activity c;
    private Movimientos movimientos;
    private String imageEncoded;
    private Bitmap bitmapMain;
    private Bitmap mapaBitmap;

    public MovimientosAdapter(ArrayList<Movimientos> movimientosArrayList, Activity c) {
        this.movimientosArrayList = movimientosArrayList;
        this.c = c;
    }

    public void setMovimientosList(ArrayList<Movimientos> movimientosArrayList) {
        this.movimientosArrayList = movimientosArrayList;
        notifyItemChanged(0, movimientosArrayList.size());
    }

    @Override
    public AdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_hstoriall, parent, false);
        return new AdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdapterViewHolder holder, final int position) {
        movimientos = movimientosArrayList.get(position);

        holder.nombreParki.setText("Parkimetro " + position);
        holder.tarjetaID.setText(movimientos.getStrTarjetaID());
        holder.montoID.setText("$" + movimientos.getMonto());

        getStaticMap(new LatLng(movimientos.getLatOri(), movimientos.getLngOri()),
                new LatLng(movimientos.getLatDest(), movimientos.getLngDest()),
                movimientos.getRuta(),
                holder.mapa);

        holder.mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(c.getApplicationContext(), Detalle_Activity.class);
                i.putExtra("detalle", movimientos);
                i.putExtra("posicion", position);
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
        ImageView mapa;

        public AdapterViewHolder(View itemView) {
            super(itemView);
            nombreParki = (TextView) itemView.findViewById(R.id.nombreParquimetroID);
            tarjetaID = (TextView) itemView.findViewById(R.id.tarjetaID);
            montoID = (TextView) itemView.findViewById(R.id.montoID);
            mapa = (ImageView) itemView.findViewById(R.id.mapaImgID);
        }
    }

    private void getStaticMap(final LatLng origen, final LatLng destino, final String ruta, final ImageView imageView) {
        ImageRequest request = new ImageRequest(
                Constants.URL_MAPA_STATICO(origen, destino, ruta),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        VolleyApp.getmInstance().addToRequestQueue(request);
    }


}
