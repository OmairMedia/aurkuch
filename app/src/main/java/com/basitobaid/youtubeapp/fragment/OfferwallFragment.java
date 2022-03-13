package com.basitobaid.youtubeapp.fragment;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.basitobaid.youtubeapp.R;
import com.basitobaid.youtubeapp.utils.AppPreferences;
import com.offertoro.sdk.OTOfferWallSettings;
import com.offertoro.sdk.interfaces.OfferWallListener;
import com.offertoro.sdk.sdk.OffersInit;

/**
 * A simple {@link Fragment} subclass.
 */
public class OfferwallFragment extends Fragment {

    public static final String YOUR_OW_SECRET_KEY = "cf20bbd6784bd0a74e7c335660a18dae"; //set your value
    public static final String YOUR_OW_APP_ID = "8670"; //set your value
    public static final String YOUR_OW_USER_ID = ""; //set your value
    private FragmentActivity activity;

    public OfferwallFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        OTOfferWallSettings.getInstance().configInit(YOUR_OW_APP_ID,
                YOUR_OW_SECRET_KEY, AppPreferences.getUserId());

        OffersInit.getInstance().create(activity);
        OffersInit.getInstance().showOfferWall(activity);

        OffersInit.getInstance().setOfferWallListener(new OfferWallListener() {
            @Override
            public void onOTOfferWallInitSuccess() {
                Toast.makeText(activity, "", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onOTOfferWallInitFail(String s) {
                Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onOTOfferWallOpened() {

            }

            @Override
            public void onOTOfferWallCredited(double v, double v1) {

            }

            @Override
            public void onOTOfferWallClosed() {
                Toast.makeText(activity, "closed", Toast.LENGTH_SHORT).show();
                activity.getSupportFragmentManager()
                        .popBackStackImmediate();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_offerwall, container, false);
    }

}
