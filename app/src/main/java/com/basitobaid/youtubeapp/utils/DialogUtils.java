package com.basitobaid.youtubeapp.utils;

import android.app.Activity;
import android.content.Context;

import com.basitobaid.youtubeapp.R;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;

public class DialogUtils {

    public static void showSuccessDialog(Context context, String message) {
        new FancyGifDialog.Builder((Activity) context)
                .setTitle("Transaction Successful!")
                .setMessage(message)
                .setPositiveBtnText("Ok")
                .setPositiveBtnBackground("#636fdf")
                .setGifResource(R.drawable.success)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(() -> {

                })
                .build();
    }

    public static void showCongratsDialog(Context context, String reward) {
        new FancyGifDialog.Builder((Activity) context)
                .setTitle("Congratulations!")
                .setMessage("You got Rs." + reward)
                .setPositiveBtnText("Ok")
                .setPositiveBtnBackground("#636fdf")
                .setGifResource(R.drawable.money)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(() -> {
                })
                .build();
    }

    public static void showInfoDialog(Context context, String message) {
        new FancyGifDialog.Builder((Activity) context)
                .setTitle("Sorry!")
                .setMessage(message)
                .setPositiveBtnText("Ok")
                .setPositiveBtnBackground("#636fdf")
                .setGifResource(R.drawable.info)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(() -> {

                })
                .build();
    }

    public static void showErrorDialog(Context context, String message) {
        new FancyGifDialog.Builder((Activity) context)
                .setTitle("Oops!")
                .setMessage(message)
                .setPositiveBtnText("Ok")
                .setPositiveBtnBackground("#636fdf")
                .setGifResource(R.drawable.error)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(() -> {

                })
                .build();
    }
}
