package com.neology.parking_neo.utils;

import com.google.android.gms.maps.model.LatLng;
import com.neology.parking_neo.MainActivity;

/**
 * Created by Cesar Segura on 24/02/2017.
 */

public class Constants {
    public static final int SUCCESS_RESULT = 0;

    public static final int FAILURE_RESULT = 1;

    public static final String PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationaddress";

    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";

    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";

    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    public static final String SKU_10 = "parki_1";
    public static final String SKU_20 = "parki_20";
    public static final String SKU_30 = "parki_30";
    public static final String SKU_40 = "parki_40";
    public static final String SKU_50 = "parki_50";
    public static final String SKU_1_00 = "parki_1_00";
    public static final String ACTUALIZAR_TARJETA_URL = "http://mobile.neology-demos.com:8080/api/insertMovementParquimetro";
    public static final String DATOS_TARJETA_URL = "http://mobile.neology-demos.com:8080/api/parquimetros?strTarjetaID=";
    public static final String MOVIMIENTOS_URL = "http://mobile.neology-demos.com:8080/api/movimientos?strTarjetaID=";
    public static final String TARJETA_URL = "http://mobile.neology-demos.com:8080/api/parquimetros?strTarjetaID=";

    //private static String SKU = "parki_40";
    public static String SKU = "android.test.purchased";

    public static final String URL_API1 = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
    public static final String URL_API2 = "&radius=2000&type=parking&key=AIzaSyBs3CoFTpKcE0MmfmQr-sBclvkqK1PKSWw";

    public static String urlRoute = "https://maps.googleapis.com/maps/api/directions/json?origin=";
    public static String getRequestUrl(LatLng latLngOri, LatLng latLngDes) {
        return Constants.urlRoute + latLngOri.latitude + "," + latLngOri.longitude + "&destination=" + latLngDes.latitude + "," + latLngDes.longitude;
    }
    public static String URL_MAPA_STATICO (LatLng origen, LatLng destino) {
        return "https://maps.googleapis.com/maps/api/staticmap?" +
                "size=800x500&markers=color:green%7Clabel:A%7C"+
                origen.latitude+","+origen.longitude+
                "&markers=color:red%7Clabel:B%7C"+
                destino.latitude+","+destino.longitude+
                "&path=weight:4%7Ccolor:0x0000ff%7Cenc:eotuB|pn|QoNYpAyDnDuKjBiGtAyF|@gEJqAf@cI\\aFDQRo@f@cA^c@vEeDx@?T??ZGdBV?fOXGfDaACmLS";
    }
}
