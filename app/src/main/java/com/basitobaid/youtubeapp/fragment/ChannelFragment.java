package com.basitobaid.youtubeapp.fragment;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.basitobaid.youtubeapp.BuildConfig;
import com.basitobaid.youtubeapp.R;
import com.basitobaid.youtubeapp.activity.WebviewActivity;
import com.basitobaid.youtubeapp.dialog.ProgressDialog;
import com.basitobaid.youtubeapp.network.Model;
import com.basitobaid.youtubeapp.network.Response;
import com.basitobaid.youtubeapp.network.RestClient;
import com.basitobaid.youtubeapp.utils.AppPreferences;
import com.basitobaid.youtubeapp.utils.ConnectionManager;
import com.basitobaid.youtubeapp.utils.Constant;
import com.basitobaid.youtubeapp.utils.DialogUtils;
import com.basitobaid.youtubeapp.utils.Global;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.SubscriptionListResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;


public class ChannelFragment extends Fragment {

    @BindView(R.id.channel_image)
    ImageView channelImage;
    @BindView(R.id.channel_name)
    TextView channelName;
    @BindView(R.id.subscribe_btn)
    Button subscribeBtn;
    @BindView(R.id.next_btn)
    Button nextBtn;
    @BindView(R.id.main_layout)
    RelativeLayout mainLayout;
    @BindView(R.id.no_data)
    ImageView noData;
    @BindView(R.id.no_internet)
    ImageView noInternet;
    @BindView(R.id.dots_layout)
    LinearLayout dotsLayout;
    @BindView(R.id.prev_btn)
    Button prevBtn;
    @BindView(R.id.reward_tv)
    TextView rewardTv;
    @BindView(R.id.rewards_left)
    TextView rewardsLeft;


    private List<Model.Reward> rewardList;
    private int currentChannel = 0;
    private Context context;
    private FragmentActivity activity;
    private ProgressDialog progressDialog;
    private GoogleAccountCredential mCredential;
    private static final String[] SCOPES = {YouTubeScopes.YOUTUBE_READONLY};

    public ChannelFragment() {
        // Required empty public constructor
    }

    public static ChannelFragment newInstance() {

        return new ChannelFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getContext();
        activity = getActivity();
        mCredential = GoogleAccountCredential.usingOAuth2(
                context, Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        rewardList = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_channel, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        channelImage = view.findViewById(R.id.channel_image);
        channelName = view.findViewById(R.id.channel_name);
        subscribeBtn = view.findViewById(R.id.subscribe_btn);
        nextBtn = view.findViewById(R.id.next_btn);
        nextBtn.setOnClickListener(view1 -> {
            Global.checkRemainingRewards(new Global.OnCountResult() {
                @Override
                public void onSuccess() {
                    ++currentChannel;
                    showChannelData();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(context, "Your reward limit for today exceeded", Toast.LENGTH_SHORT).show();
                }
            });

        });
        prevBtn.setOnClickListener(view1 -> {
            Global.checkRemainingRewards(new Global.OnCountResult() {
                @Override
                public void onSuccess() {
                    --currentChannel;
                    showChannelData();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(context, "Your reward limit for today exceeded", Toast.LENGTH_SHORT).show();
                }
            });
        });
        subscribeBtn.setOnClickListener(view1 -> {
            Global.checkRemainingRewards(new Global.OnCountResult() {
                @Override
                public void onSuccess() {
                    Intent intent = new Intent(context, WebviewActivity.class);
                    intent.putExtra(Constant.URL, rewardList.get(currentChannel).rLink);
                    startActivityForResult(intent, 12);
                }

                @Override
                public void onFailure() {
                    Toast.makeText(context, "Your reward limit for today exceeded", Toast.LENGTH_SHORT).show();
                }
            });
        });
        if (ConnectionManager.isDeviceOnline(context)) {
            showDotsLayout();
            getChannelList();
        } else {
            showNoInternetLayout();
        }
    }

    private void disableNext() {
        nextBtn.setEnabled(false);
        nextBtn.setAlpha(0.5f);
    }

    private void enableNext() {
        nextBtn.setAlpha(1);
        nextBtn.setEnabled(true);
    }

    private void disablePrev() {
        prevBtn.setEnabled(false);
        prevBtn.setAlpha(0.5f);
    }

    private void enablePrev() {
        prevBtn.setAlpha(1);
        prevBtn.setEnabled(true);
    }


    private void getChannelList() {
        Call<Response.RewardResponse> call = RestClient.getClient().getRewards(AppPreferences.getUserToken(), "3");
        call.enqueue(new Callback<Response.RewardResponse>() {
            @Override
            public void onResponse(@NonNull Call<Response.RewardResponse> call,
                                   @NonNull retrofit2.Response<Response.RewardResponse> response) {
                if (response.body() != null && response.body().data != null) {
                    if (response.body().data.size() > 0) {
                        rewardList.addAll(response.body().data);
                        showChannelData();
                        showRecyclerLayout();
                    } else {
                        showNoDataLayout();
                    }
                } else {
                    showNoDataLayout();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Response.RewardResponse> call,
                                  @NonNull Throwable t) {
                DialogUtils.showErrorDialog(context, "error occurred");
                showNoDataLayout();
            }
        });
    }

    private void showChannelData() {
        if (rewardList.size() > currentChannel && currentChannel >= 0) {
            Model.Reward reward = rewardList.get(currentChannel);
            loadImage(channelImage, reward.rImage);
            channelName.setText(reward.rDescription);
            rewardTv.setText(String.format("Reward amount: Rs. %s",
                    reward.rewardAmount));
            rewardsLeft.setText(String.format("%s rewards left", reward.rLeft));
            if (rewardList.size() == currentChannel + 1) {
                disableNext();
            } else {
                enableNext();
            }
            if (currentChannel == 0) {
                disablePrev();
            } else {
                enablePrev();
            }
        } else {
            showNoDataLayout();
            Toast.makeText(context, "No more channels for now", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadImage(ImageView view, String image) {
        Glide.with(context)
                .load(BuildConfig.BASE_IMAGE_URL + image)
                .placeholder(R.drawable.icon_coins)
                .circleCrop()
                .into(view);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12) {
            String[] array = rewardList.get(currentChannel).rLink.split("/");
            String channelId = array[array.length - 1];
            mCredential.setSelectedAccountName(AppPreferences.getUserAccountname());
            showProgressDialog();
            if (mCredential.getSelectedAccountName() != null) {
                new GetSubscriptions(mCredential).execute(channelId);
            } else {
                mCredential.setSelectedAccount(new Account(AppPreferences.getUserAccountname(), context.getApplicationContext().getPackageName()));
                new GetSubscriptions(mCredential).execute(channelId);
            }
        }
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

    @OnClick(R.id.back_button)
    public void onViewClicked() {
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStackImmediate();
    }

    @SuppressLint("StaticFieldLeak")
    public class GetSubscriptions extends AsyncTask<String, Void, SubscriptionListResponse> {

        private YouTube youTubeDataApi;
        private Exception mLastError;

        GetSubscriptions(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            youTubeDataApi = new YouTube.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName(Constant.APP_NAME)
                    .build();
        }


        @Override
        protected SubscriptionListResponse doInBackground(String... params) {
            try {
                return youTubeDataApi.subscriptions()
                        .list("snippet")
//                    .setChannelId(params[0])
//                    .setChannelId(AppPreferences.getUserChannelId())//user channel id
                        .setForChannelId(params[0])//channel id
                        .setMine(true)
                        .setFields("items(snippet(resourceId(channelId)))")
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        @Override
        protected void onPostExecute(SubscriptionListResponse subscriptionListResponse) {
            hideProgressDialog();
            if (subscriptionListResponse.getItems() == null || subscriptionListResponse.getItems().size() == 0) {
                Toast.makeText(context, "You have not subscribed the channel", Toast.LENGTH_LONG).show();
            } else {
                updateWallet();

            }
        }

        @Override
        protected void onCancelled() {
            activity.runOnUiThread(ChannelFragment.this::hideProgressDialog);
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            Constant.REQUEST_AUTHORIZATION);
                } else {
                    Toast.makeText(context, mLastError.toString(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "unknown error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateWallet() {
        showProgressDialog();
        Call<Response.CommonResponse> call = RestClient.getClient().updateWallet(AppPreferences.getUserToken(),
                rewardList.get(currentChannel).rId, null);
        call.enqueue(new Callback<Response.CommonResponse>() {
            @Override
            public void onResponse(@NonNull Call<Response.CommonResponse> call,
                                   @NonNull retrofit2.Response<Response.CommonResponse> response) {
                hideProgressDialog();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().status.equals("200")) {
                            DialogUtils.showCongratsDialog(context, rewardList.get(currentChannel).rewardAmount);
                        } else {
                            DialogUtils.showInfoDialog(context, response.body().message);
                        }
                    } else {
                        DialogUtils.showErrorDialog(context, "no response");
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


    private void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                getActivity(),
                connectionStatusCode,
                Constant.REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
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
}
