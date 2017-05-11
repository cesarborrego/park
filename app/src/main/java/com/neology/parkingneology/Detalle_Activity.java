package com.neology.parkingneology;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.neology.parking_neo.R;
import com.neology.parking_neo.model.Movimientos;
import com.neology.parking_neo.utils.ImageUtil;

public class Detalle_Activity extends AppCompatActivity {
    Movimientos movimientos;

    ImageView mapaImg;
    TextView idTarjeta;
    TextView numMovimiento;

    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_);
        movimientos = getIntent().getParcelableExtra("detalle");
        position = getIntent().getIntExtra("posicion",0);
        initElements();
    }

    private void initElements() {
        mapaImg = (ImageView) findViewById(R.id.image_paralax);
        ImageUtil.setImg(movimientos.getMapa(), mapaImg);
        idTarjeta = (TextView) findViewById(R.id.tituloPromo1);
        idTarjeta.setText(movimientos.getStrTarjetaID());
        numMovimiento = (TextView) findViewById(R.id.descripcion1);
        numMovimiento.setText("Parkimetro "+position);
    }
}
