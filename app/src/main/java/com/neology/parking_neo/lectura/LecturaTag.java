package com.neology.parking_neo.lectura;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Cesar Segura on 15/08/2016.
 */

public class LecturaTag {
    Intent intentlectura;
    private static final String TAG = LecturaTag.class.getSimpleName();
    public final PersoSecure secure = new PersoSecure();

    public LecturaTag(Intent intentlectura){
        this.intentlectura = intentlectura;
    }

    public String[] lectura() {
        //super.onNewIntent(intent);
//		Log.d(TAG, "LecturaTag.lectura()::Intent->"+intentlectura.getAction());
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intentlectura.getAction())) {

            NdefMessage[] messages = null;
            Parcelable[] rawMsgs = intentlectura.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                messages = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    messages[i] = (NdefMessage) rawMsgs[i];
                }
            }

            if (messages[0] != null) {

                // messages[0].getRecords()[1]<-Numero de registro.getPayload();
                byte[] payload = messages[0].getRecords()[0].getPayload();

                String uidTag = "";

                Tag tag = intentlectura.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                uidTag = secure.bin2hex(tag.getId());
                Log.d(TAG, "Lectura UID: " + uidTag);

                String resultado = validaTag(payload, uidTag);
                String[] elementos;

                if (resultado != null) {
                    try{
                        elementos = resultado.split("\\|");
                        String valid = elementos[elementos.length-1];
                        //Log.d(TAG, "Digito validado: " + valid);
                        if (valid.equals("4")) {
                            List<String> list = new ArrayList<String>(Arrays.asList(elementos));
                            list.remove(elementos.length-1);
                            elementos = list.toArray(elementos);
                            String [] array = new String[elementos.length-1];
                            array = list.toArray(array);
                            return array;
                        }

                    }catch(Exception e){
                        Log.e(TAG, "Exception::lectura()->"+e.getMessage());
                        return null;
                    }

                }

            }

        }
        return null;
    }

    protected String validaTag(byte[] payload, String uidTag) {

        try {

            //Log.d(TAG, "Lectura NFC Datos Cifrados: " + secure.bin2hex(payload));

            String datosLeidos = "";

            byte[] part = secure.getkey(uidTag + "ParkiNeo");
            //Log.d(TAG, "PART-"+ secure.bin2hex(part));
            byte[] decryptedData = secure.decrypt(part, payload);

            datosLeidos = new String(decryptedData);
            Log.d(TAG, "NFC::decryptedData: " + datosLeidos);

            return datosLeidos;

        } catch (Exception e) {
            Log.i(TAG,"LicenseValidator::validaTag: NFC Tag Corrupto->"+ e.getCause());
        }

        return null;
    }
}
