package com.basitobaid.youtubeapp.fragment;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.basitobaid.youtubeapp.R;
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
public class ApplicationDetailsFragment extends Fragment implements GlobalCall.UpdateWalletCallback {


    @BindView(R.id.topLayout)
    LinearLayout topLayout;
    @BindView(R.id.app_name)
    TextView appName;
    @BindView(R.id.app_version)
    TextView appVersion;
    @BindView(R.id.app_desc)
    TextView appDesc;
    @BindView(R.id.reward_mage)
    ImageView rewardMage;
    @BindView(R.id.reward_tv)
    TextView rewardTv;
    @BindView(R.id.rewards_left)
    TextView rewardsLeft;
    private Model.Reward reward;
    private FragmentActivity activity;
    private ProgressDialog progressDialog;
    private GlobalCall.UpdateWalletCallback callback;

    public ApplicationDetailsFragment() {
        // Required empty public constructor
    }

    public static ApplicationDetailsFragment newInstance(Model.Reward app) {
        ApplicationDetailsFragment fragment = new ApplicationDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("app", app);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        callback = this;
        if (getArguments() != null)
            reward = getArguments().getParcelable("app");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_application_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageUtils.loadImage(getContext(), rewardMage, reward.rImage);
        appName.setText(reward.cName);
        appVersion.setText(reward.cName);
        appDesc.setText(reward.rDescription);
        rewardTv.setText(String.format("You will get Rs. %s for installing this application",
                reward.rewardAmount));
        rewardsLeft.setText(String.format("%s rewards left", reward.rLeft));
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
        if (requestCode == 123) {
            String apppackage = reward.rLink.split("=")[1];
            if (isApplicationInstalled(activity.getPackageManager(), apppackage)) {
                showProgressDialog();
                new Handler().postDelayed(() -> new GlobalCall(callback).updateWallet(reward), 2000);
            } else {
                Toast.makeText(activity, "You did not installed the application", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private boolean isApplicationInstalled(PackageManager packageManager, String packageName) {
        try {
            return packageManager.getPackageInfo(packageName, 0) != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public void onSuccess(String amount) {
        hideProgressDialog();
        DialogUtils.showCongratsDialog(getContext(), reward.rewardAmount);
    }

    @Override
    public void onAlreadyGot(String message) {
        hideProgressDialog();
        DialogUtils.showInfoDialog(getContext(), message);
    }

    @Override
    public void onUpdateError() {
        hideProgressDialog();
        DialogUtils.showErrorDialog(getContext(), "error occurred");
    }

    @OnClick({R.id.buttonDownload, R.id.back_button})
    void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buttonDownload:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(reward.rLink));
                startActivityForResult(intent, 123);
                break;
            case R.id.back_button:
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStackImmediate();
                break;
        }

    }
}
