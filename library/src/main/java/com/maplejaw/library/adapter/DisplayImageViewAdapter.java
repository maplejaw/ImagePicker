package com.maplejaw.library.adapter;

import android.content.Context;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by Jaeger on 16/2/24.
 *
 * Email: chjie.jaeger@gamil.com
 * GitHub: https://github.com/laobie
 */
public abstract class DisplayImageViewAdapter<T> {
    public abstract void onDisplayImage(Context context, ImageView imageView, T t);

    public void onItemImageClick(Context context, int index, List<T> list) {

    }

    public void onImageCheckL(String path,boolean isChecked) {

    }



}