package com.ldx.weather.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Glide工具类
 */
public class GlideUtils {
//用于使用Glide加载图片并将其设置为布局的背景。
    /**
     * 加载图片
     */
    public static void loadImg(Context context, String url, ViewGroup viewGroup) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();

        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        Glide.with(context)
                .load(url)
                .override(screenWidth, screenHeight)
                .centerCrop()
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        viewGroup.setBackground(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }
}
