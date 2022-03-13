package com.basitobaid.youtubeapp.fragment;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.basitobaid.youtubeapp.R;
import com.basitobaid.youtubeapp.activity.QRCodeScanActivity;
import com.basitobaid.youtubeapp.dialog.ProgressDialog;
import com.basitobaid.youtubeapp.network.GlobalCall;
import com.basitobaid.youtubeapp.network.Model;
import com.basitobaid.youtubeapp.utils.DialogUtils;
import com.basitobaid.youtubeapp.utils.ImageUtils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class BrandDetailsFragment extends Fragment implements GlobalCall.UpdateWalletCallback {


    private static final int BARCODE_READER_ACTIVITY_REQUEST = 223;
    @BindView(R.id.topLayout)
    LinearLayout topLayout;
    @BindView(R.id.brand_name)
    TextView brandName;
    @BindView(R.id.brand_offer)
    TextView brandOffer;
    @BindView(R.id.brand_desc)
    TextView brandDesc;
    @BindView(R.id.brand_image)
    ImageView brandImage;
    @BindView(R.id.reward_tv)
    TextView rewardTv;
    @BindView(R.id.rewards_left)
    TextView rewardsLeft;
    private Context context;
    private FragmentActivity activity;
    private ProgressDialog progressDialog;
    private Model.Reward reward;
    private GlobalCall.UpdateWalletCallback callback;

    private BrandDetailsFragment() {
        // Required empty public constructor
    }

    public static BrandDetailsFragment newInstance(Model.Reward reward) {
        BrandDetailsFragment fragment = new BrandDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("brand", reward);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        activity = getActivity();
        callback = this;
        if (getArguments() != null) {
            reward = getArguments().getParcelable("brand");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_brand_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageUtils.loadImage(getContext(), brandImage, reward.rImage);
        brandName.setText(reward.cName);
        brandDesc.setText(reward.cName);
        brandOffer.setText(reward.rDescription);
        rewardTv.setText(String.format("You will get Rs. %s for using this offer",
                reward.rewardAmount));
        rewardsLeft.setText(String.format("%s rewards left", reward.rLeft));
    }

    @OnClick(R.id.button_scan_qr)
    void onViewClicked() {

    }

    private void showProgressDialog() {
        progressDialog = ProgressDialog.getInstance();
        progressDialog.show(activity.getSupportFragmentManager(), "pDialog");
        progressDialog.setCancelable(false);
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isVisible()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BARCODE_READER_ACTIVITY_REQUEST && data != null) {
            Uri resultURi = data.getData();
            if (resultURi != null) {
                String[] rewardData = reward.rLink.split("/");
                String rewardSt = rewardData[rewardData.length - 1].split("-")[0];
                if (rewardSt.equals(resultURi.toString())) {
                    showProgressDialog();
                    new GlobalCall(callback).updateWallet(reward);
                } else {
                    DialogUtils.showInfoDialog(context, "invalid code");
                }
            }
        }
    }

    @Override
    public void onSuccess(String amount) {
        hideProgressDialog();
        DialogUtils.showCongratsDialog(context, reward.rewardAmount);
    }

    @Override
    public void onAlreadyGot(String message) {
        hideProgressDialog();
        DialogUtils.showInfoDialog(context, message);
    }

    @Override
    public void onUpdateError() {
        hideProgressDialog();
        DialogUtils.showErrorDialog(context, "error occurred");
    }

    @OnClick({R.id.back_button, R.id.button_scan_qr})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStackImmediate();
                break;
            case R.id.button_scan_qr:
                startActivityForResult(new Intent(context, QRCodeScanActivity.class), BARCODE_READER_ACTIVITY_REQUEST);
                break;
        }
    }
}
