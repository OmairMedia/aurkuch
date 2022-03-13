package com.basitobaid.youtubeapp.utils;

import android.content.Context;
import android.widget.ImageView;

import com.basitobaid.youtubeapp.BuildConfig;
import com.basitobaid.youtubeapp.R;
import com.bumptech.glide.Glide;

public class ImageUtils {


    public static void loadImage(Context context, ImageView imageView, String imageUrl) {
        Glide.with(context)
                .load(BuildConfig.BASE_IMAGE_URL+imageUrl)
                .placeholder(R.drawable.icon_coins)
                .circleCrop()
                .into(imageView);
    }

    public static void loadImageWithExternalUrl(Context context, ImageView imageView, String imageUrl) {
        Glide.with(context)
                .load(BuildConfig.BASE_IMAGE_URL+imageUrl)
                .placeholder(R.drawable.blank_image)
                .into(imageView);
    }
}
