package com.basitobaid.youtubeapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;


public class VideosFragment extends Fragment {

    @BindView(R.id.elapsed_duration)
    TextView elapsedDuration;
    @BindView(R.id.total_duration)
    TextView totalDuration;
    @BindView(R.id.prev_video_btn)
    Button prevVideoBtn;
    @BindView(R.id.next_video_btn)
    Button nextVideoBtn;
    @BindView(R.id.reward_tv)
    TextView rewardTv;
    @BindView(R.id.main_layout)
    RelativeLayout mainLayout;
    @BindView(R.id.no_data)
    ImageView noData;
    @BindView(R.id.no_internet)
    ImageView noInternet;
    @BindView(R.id.dots_layout)
    LinearLayout dotsLayout;
    @BindView(R.id.back_button)
    ImageView backButton;
    @BindView(R.id.rewards_left)
    TextView rewardsLeft;
    private YouTubePlayerView youTubePlayerView;
    private List<Model.Reward> rewardList;
    private int currentReward = 0;
    private int durationToWatch = 10;
    private Context context;
    private FragmentActivity activity;
    private boolean videoWatched = false;
    private ProgressDialog progressDialog;

    public VideosFragment() {
        // Required empty public constructor
    }


    public static VideosFragment newInstance() {
        return new VideosFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos, container, false);
        ButterKnife.bind(this, view);
        elapsedDuration.setText(String.format("You have watched %s seconds", String.valueOf(0)));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        context = getContext();
        activity = getActivity();
        rewardList = new ArrayList<>();
        disablePrev();
        nextVideoBtn.setOnClickListener(view1 -> {
            ++currentReward;
            startVideo();
        });
        prevVideoBtn.setOnClickListener(view1 -> {
            --currentReward;
            startVideo();
        });
        if (ConnectionManager.isDeviceOnline(context)) {
            showDotsLayout();
            getVideoList();
            setYouTubePlayerView(view);

        } else {
            showNoInternetLayout();
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

    private void setYouTubePlayerView(View view) {
        youTubePlayerView = view.findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);
        youTubePlayerView.getPlayerUiController().showVideoTitle(false);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
//                String videoId =  "S0Q4gqBUs7c";
            }

            @Override
            public void onCurrentSecond(@NonNull YouTubePlayer youTubePlayer, float second) {
                if (!videoWatched) {
                    if ((int) second == durationToWatch) {
                        if (ConnectionManager.isDeviceOnline(context)) {
                            elapsedDuration.setText(String.format("Watched: %s seconds", String.valueOf((int) second)));
                            totalDuration.setText(String.format("To watch: %s seconds", String.valueOf(durationToWatch - (int) second)));
                            videoWatched = true;
                            enableNext();
                            updateWallet();
                        } else {
                            showNoInternetLayout();
                        }
                    } else {
                        elapsedDuration.setText(String.format("Watched: %s seconds", String.valueOf((int) second)));
                        totalDuration.setText(String.format("To watch: %s seconds", String.valueOf(durationToWatch - (int) second)));
                    }
                }
            }

            @Override
            public void onStateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState state) {
                if (state.equals(PlayerConstants.PlayerState.ENDED)) {
                    youTubePlayer.play();
                }
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
            videoWatched = false;
            youTubePlayerView.getYouTubePlayerWhenReady(youTubePlayer ->
                    youTubePlayer.cueVideo(rewardList.get(currentReward).rDescription, 0));

            elapsedDuration.setText(String.format("Watched: %s seconds", String.valueOf(0)));
            totalDuration.setText(String.format("To watch: %s seconds", String.valueOf(durationToWatch)));
            rewardTv.setText(String.format("Reward amount: Rs. %s",
                    rewardList.get(currentReward).rewardAmount));
            rewardsLeft.setText(String.format("%s rewards left", rewardList.get(currentReward).rLeft));
        } else {
            youTubePlayerView.getYouTubePlayerWhenReady(YouTubePlayer::pause);
            showNoDataLayout();
        }
    }

    private void getVideoList() {
        Call<Response.RewardResponse> call = RestClient.getClient().getRewards(AppPreferences.getUserToken(), "4");
        call.enqueue(new Callback<Response.RewardResponse>() {
            @Override
            public void onResponse(@NonNull Call<Response.RewardResponse> call,
                                   @NonNull retrofit2.Response<Response.RewardResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().data != null) {
                        if (response.body().data.size() > 0) {
                            rewardList.addAll(response.body().data);
                            startVideo();
                            showRecyclerLayout();
                        } else {
                            showNoDataLayout();
                        }
                    } else {
                        DialogUtils.showInfoDialog(context, "no response");
                        showNoDataLayout();
                    }
                } else {
                    DialogUtils.showInfoDialog(context, "no response");
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

    private void showNoInternetLayout() {
        mainLayout.setVisibility(View.GONE);
        noInternet.setVisibility(View.VISIBLE);
        noData.setVisibility(View.GONE);
        dotsLayout.setVisibility(View.GONE);
    }

    private void showNoDataLayout() {
        mainLayout.setVisibility(View.GONE);
        noInternet.setVisibility(View.GONE);
        noData.setVisibility(View.VISIBLE);
        dotsLayout.setVisibility(View.GONE);
    }

    private void showRecyclerLayout() {
        mainLayout.setVisibility(View.VISIBLE);
        noInternet.setVisibility(View.GONE);
        noData.setVisibility(View.GONE);
        dotsLayout.setVisibility(View.GONE);
    }

    private void showDotsLayout() {
        dotsLayout.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.GONE);
        noInternet.setVisibility(View.GONE);
        noData.setVisibility(View.GONE);
    }


    @OnClick(R.id.back_button)
    public void onViewClicked() {
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }
}
