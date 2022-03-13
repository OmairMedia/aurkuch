package com.basitobaid.youtubeapp.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
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
import com.basitobaid.youtubeapp.network.Model;
import com.basitobaid.youtubeapp.network.Response;
import com.basitobaid.youtubeapp.network.RestClient;
import com.basitobaid.youtubeapp.utils.AppPreferences;
import com.basitobaid.youtubeapp.utils.ConnectionManager;
import com.basitobaid.youtubeapp.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * A simple {@link Fragment} subclass.
 */
public class InstaVideoFragment extends Fragment {


    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.prev_video_btn)
    Button prevVideoBtn;
    @BindView(R.id.next_video_btn)
    Button nextVideoBtn;
    @BindView(R.id.time_remaining)
    TextView timeRemaining;
    @BindView(R.id.no_data)
    ImageView noData;
    @BindView(R.id.no_internet)
    ImageView noInternet;
    @BindView(R.id.dots_layout)
    LinearLayout dotsLayout;
    @BindView(R.id.play_ll)
    LinearLayout playLl;

    private Context context;
    private FragmentActivity activity;
    private List<Model.Reward> rewardList;
    private int currentReward = 0;
    private boolean urlLoaded = false;
    private boolean timerStarted = false;
    private int x1,x2, y1, y2;

    private InstaVideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        activity = getActivity();
//        onPreparedListener = this;
    }


    public static InstaVideoFragment newInstance() {
        return new InstaVideoFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_insta_video, container, false);
        ButterKnife.bind(this, view);
        // Inflate the layout for this fragment
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rewardList = new ArrayList<>();
        if (ConnectionManager.isDeviceOnline(context)) {
            showDotsLayout();
            getVideoList();

        } else {
            showNoInternetLayout();
        }

        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                urlLoaded = true;
            }
        });
        webview.setOnTouchListener((view12, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x1 = (int)motionEvent.getRawX();
                    y1 = (int)motionEvent.getRawY();
                    Log.d("TAG", "onViewCreated: x1: " + x1 + " y1: " + y1);
                    break;
                case MotionEvent.ACTION_UP:
                    x2 = (int)motionEvent.getRawX();
                    y2 = (int)motionEvent.getRawY();

                    Log.d("TAG", "onViewCreated: x2: " + x2 + " y2: " + y2);
                    if (y2-y1 == 0 && x2 - x1 == 0) {
                        if (urlLoaded && !timerStarted) {
                            timerStarted = true;
                            playLl.setClickable(true);
                            startTimer();
                        }
                    }
                    break;

            }
            return false;
        });


        nextVideoBtn.setOnClickListener(view1 -> {
            ++currentReward;
            startVideo();
        });
        prevVideoBtn.setOnClickListener(view1 -> {
            --currentReward;
            startVideo();
        });

    }

    private void getVideoList() {
        Call<Response.RewardResponse> call = RestClient.getClient().getRewards(AppPreferences.getUserToken(), "7");
        call.enqueue(new Callback<Response.RewardResponse>() {
            @Override
            public void onResponse(@NonNull Call<Response.RewardResponse> call,
                                   @NonNull retrofit2.Response<Response.RewardResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().data != null) {
                        if (response.body().data.size() > 0) {
                            rewardList.addAll(response.body().data);
                            showMainLayout();
                            startVideo();
                        } else {
                            showNoDataLayout();
                        }
                    } else {
                        showNoDataLayout();
                    }
                } else {
                    showNoDataLayout();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Response.RewardResponse> call
                    , @NonNull Throwable t) {

                DialogUtils.showErrorDialog(context, "error occurred");
                showNoDataLayout();
            }
        });
    }

    private ProgressDialog progressDialog;

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

    private void updateWallet() {
        showProgressDialog();
        Call<Response.CommonResponse> call = RestClient.getClient().updateWallet(AppPreferences.getUserToken(),
                rewardList.get(currentReward).rId, null);
        call.enqueue(new Callback<Response.CommonResponse>() {
            @Override
            public void onResponse(@NonNull Call<Response.CommonResponse> call,
                                   @NonNull retrofit2.Response<Response.CommonResponse> response) {
                hideProgressDialog();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().status.equals("200")) {
                            DialogUtils.showCongratsDialog(context, rewardList.get(currentReward).rewardAmount);
                        } else {
                            DialogUtils.showInfoDialog(context, response.body().message);
                        }
                    } else {
                        DialogUtils.showInfoDialog(context, "no response");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Response.CommonResponse> call,
                                  @NonNull Throwable t) {
                hideProgressDialog();
                DialogUtils.showErrorDialog(context, "error occurred");
            }
        });
    }

    private void startVideo() {
        if (rewardList != null && rewardList.size() > currentReward) {
            if (currentReward == 0)
                disablePrev();
            else
                enablePrev();
            if (currentReward == rewardList.size() - 1)
                disableNext();
            else
                enableNext();

            urlLoaded = false;
            timerStarted = false;
            playLl.setClickable(false);
            webview.loadUrl(rewardList.get(currentReward).rDescription);

//            rewardTv.setText(String.format("Reward amount: Rs. %s",
//                    rewardList.get(currentReward).rewardAmount));
//            rewardsLeft.setText(String.format("%s rewards left", rewardList.get(currentReward).rLeft));
        } else {
            showNoDataLayout();
        }

    }

    private void disablePrev() {
        prevVideoBtn.setEnabled(false);
        prevVideoBtn.setAlpha(0.5f);
    }

    private void enablePrev() {
        prevVideoBtn.setAlpha(1);
        prevVideoBtn.setEnabled(true);
    }

    private void disableNext() {
        nextVideoBtn.setEnabled(false);
        nextVideoBtn.setAlpha(0.5f);
    }

    private void enableNext() {
        nextVideoBtn.setAlpha(1);
        nextVideoBtn.setEnabled(true);
    }

    private void showMainLayout() {
        webview.setVisibility(View.VISIBLE);
        nextVideoBtn.setVisibility(View.VISIBLE);
        prevVideoBtn.setVisibility(View.VISIBLE);
        timeRemaining.setVisibility(View.VISIBLE);

        noInternet.setVisibility(View.GONE);
        noData.setVisibility(View.GONE);
        dotsLayout.setVisibility(View.GONE);
    }

    private void showNoDataLayout() {
        webview.setVisibility(View.GONE);
        nextVideoBtn.setVisibility(View.GONE);
        prevVideoBtn.setVisibility(View.GONE);
        timeRemaining.setVisibility(View.GONE);

        noInternet.setVisibility(View.GONE);
        noData.setVisibility(View.VISIBLE);
        dotsLayout.setVisibility(View.GONE);
    }

    private void showDotsLayout() {
        dotsLayout.setVisibility(View.VISIBLE);
        webview.setVisibility(View.GONE);
        nextVideoBtn.setVisibility(View.GONE);
        prevVideoBtn.setVisibility(View.GONE);
        timeRemaining.setVisibility(View.GONE);

        noInternet.setVisibility(View.GONE);
        noData.setVisibility(View.GONE);
    }

    private void showNoInternetLayout() {
        webview.setVisibility(View.GONE);
        nextVideoBtn.setVisibility(View.GONE);
        prevVideoBtn.setVisibility(View.GONE);
        timeRemaining.setVisibility(View.GONE);


        noInternet.setVisibility(View.VISIBLE);
        noData.setVisibility(View.GONE);
        dotsLayout.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (timer != null)
            timer.cancel();

    }

    @Override
    public void onStop() {
        super.onStop();
        timer = null;
    }

    private CountDownTimer timer;

    private void startTimer() {
        timer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long l) {
                if (context != null)
                    Log.d("TAG", "onTick: " + l / 1000);
                timeRemaining.setText(String.format(Locale.getDefault(), "Remaining Time: %d sec", (l / 1000)));
            }

            @Override
            public void onFinish() {
                if (context != null) {
                    if (ConnectionManager.isDeviceOnline(context)) {
                        if (rewardList.size() > (currentReward + 1))
                            enableNext();
                        updateWallet();
                    } else {
                        showNoInternetLayout();
                    }
                }

                Log.d("TAG", "onFinish: ");
            }
        }.start();
    }
}
