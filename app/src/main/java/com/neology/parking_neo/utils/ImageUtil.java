package com.neology.parking_neo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

/**
 * Created by Cesar Segura on 11/05/2017.
 */

public class ImageUtil {
    public static void setImg(byte[] b, ImageView imageView) {
        byte[] decodedString = Base64.decode(b, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imageView.setImageBitmap(bitmap);
    }

}
