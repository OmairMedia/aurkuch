package com.basitobaid.youtubeapp.fragment;


import android.accounts.Account;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.basitobaid.youtubeapp.R;
import com.basitobaid.youtubeapp.activity.WebviewActivity;
import com.basitobaid.youtubeapp.app.App;
import com.basitobaid.youtubeapp.dialog.ProgressDialog;
import com.basitobaid.youtubeapp.network.CustomNetworkCallback;
import com.basitobaid.youtubeapp.network.Model;
import com.basitobaid.youtubeapp.network.Response;
import com.basitobaid.youtubeapp.network.RestClient;
import com.basitobaid.youtubeapp.utils.AppPreferences;
import com.basitobaid.youtubeapp.utils.Constant;
import com.basitobaid.youtubeapp.utils.DateUtil;
import com.basitobaid.youtubeapp.utils.DialogUtils;
import com.basitobaid.youtubeapp.utils.Global;
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
import com.offertoro.sdk.OTOfferWallSettings;
import com.offertoro.sdk.interfaces.OfferWallListener;
import com.offertoro.sdk.sdk.OffersInit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoyFragment extends Fragment {

    public static final String YOUR_OW_SECRET_KEY = "cf20bbd6784bd0a74e7c335660a18dae"; //set your value
    public static final String YOUR_OW_APP_ID = "8670"; //set your value
    public static final String YOUR_OW_USER_ID = "21757"; //set your value

    private CardView appCv, brandCv, channelCv, videoCv, ownChannelCv, offerWallCv, fbVideoCv, instaVideoCv;
    private TextView appTitle, brandTitle, channelTitle, videoTitle, fbVideoTitle, instaVideoTitle;
    private TextView appDesc, brandDesc, channelDesc, videoDesc, fbVideoDesc, instaVideoDesc;
    private TextView appRewardCount, brandRewardCount, channelRewardCount, videoRewardCount, fbRewardCound, instaRewardCount;
    private Context context;
    private FragmentActivity activity;
    private ViewPager mPager;
    private LinearLayout dotsLayout;
    private ScrollView scrollView;
    private ImageView noData;
//    https://www.youtube.com/channel/UCeWqACGRU5gT0BXeFhrixWA
    private String channelId = "https://www.youtube.com/channel/UCYxKfAJg8GbbVQ-uF3ueyNA";
    private String channel = "https://www.youtube.com/channel/UCYxKfAJg8GbbVQ-uF3ueyNA";
    private GoogleAccountCredential mCredential;
    private ProgressDialog progressDialog;
    private static final String[] SCOPES = {YouTubeScopes.YOUTUBE_READONLY};
    private List<String> bannerlist;

    private CategoyFragment() {
        // Required empty public constructor
    }

    public static CategoyFragment newInstance() {
        return new CategoyFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getContext();
        activity = getActivity();
        channelId = AppPreferences.getYoutubeLink();
        bannerlist = new ArrayList<>();
//        channelId = AppPreferences.getYoutubeLink();
//        bannerlist.add("https://homepages.cae.wisc.edu/~ece533/images/airplane.png");
//        bannerlist.add("https://homepages.cae.wisc.edu/~ece533/images/arctichare.png");
//        bannerlist.add("https://homepages.cae.wisc.edu/~ece533/images/baboon.png");
//        bannerlist.add("https://homepages.cae.wisc.edu/~ece533/images/boat.png");
//        bannerlist.add("https://homepages.cae.wisc.edu/~ece533/images/frymire.png");
        createOfferWall();
        return inflater.inflate(R.layout.fragment_categoy, container, false);
    }

    private void createOfferWall() {
        OTOfferWallSettings.getInstance().configInit(YOUR_OW_APP_ID,
                YOUR_OW_SECRET_KEY, YOUR_OW_USER_ID);

        OffersInit.getInstance().create(activity);

        OffersInit.getInstance().setOfferWallListener(new OfferWallListener() {
            @Override
            public void onOTOfferWallInitSuccess() {
            }

            @Override
            public void onOTOfferWallInitFail(String s) {
//                Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onOTOfferWallOpened() {

            }

            @Override
            public void onOTOfferWallCredited(double v, double v1) {

            }

            @Override
            public void onOTOfferWallClosed() {
//                Toast.makeText(activity, "closed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mPager = view.findViewById(R.id.viewPagger);
        dotsLayout = view.findViewById(R.id.dots_layout);
        scrollView = view.findViewById(R.id.scroll_layout);
        noData = view.findViewById(R.id.no_data);
        mCredential = GoogleAccountCredential.usingOAuth2(
                context, Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        getBannerList();


        appCv = view.findViewById(R.id.app_cardView);
        brandCv = view.findViewById(R.id.brand_cardView);
        channelCv = view.findViewById(R.id.channel_cardView);
        videoCv = view.findViewById(R.id.vide_cardView);
        ownChannelCv = view.findViewById(R.id.own_channel_cardView);
        offerWallCv = view.findViewById(R.id.offer_cardView);
        fbVideoCv = view.findViewById(R.id.fb_cardView);
        instaVideoCv = view.findViewById(R.id.insta_cardView);


        appTitle = view.findViewById(R.id.app_cat_name);
        brandTitle = view.findViewById(R.id.brand_cat_name);
        channelTitle = view.findViewById(R.id.channel_cat_name);
        videoTitle = view.findViewById(R.id.videos_cat_name);
        fbVideoTitle = view.findViewById(R.id.fb_cat_name);
        instaVideoTitle = view.findViewById(R.id.insta_cat_name);

        appDesc = view.findViewById(R.id.app_cat_desc);
        brandDesc = view.findViewById(R.id.brand_cat_desc);
        channelDesc = view.findViewById(R.id.channel_cat_desc);
        videoDesc = view.findViewById(R.id.videos_cat_desc);
        fbVideoDesc = view.findViewById(R.id.fb_cat_desc);
        instaVideoDesc = view.findViewById(R.id.insta_cat_desc);

        appRewardCount = view.findViewById(R.id.app_reward_count);
        brandRewardCount = view.findViewById(R.id.brand_reward_count);
        channelRewardCount = view.findViewById(R.id.channel_reward_count);
        videoRewardCount = view.findViewById(R.id.videos_reward_count);
        fbRewardCound= view.findViewById(R.id.fb_reward_count);
        instaRewardCount = view.findViewById(R.id.insta_reward_count);

        getRewardCategories();
        setClickListeners();
    }

    class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private int NUM_PAGES;
        private List<String> bannerList;

        ScreenSlidePagerAdapter(FragmentManager fm, List<String> bannerList) {
            super(fm);
            this.bannerList = bannerList;
            this.NUM_PAGES = bannerList.size();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return slider.getInstance(bannerList.get(position));
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    private void setClickListeners() {
        appCv.setOnClickListener(view -> {
            Global.checkRemainingRewards(new Global.OnCountResult() {
                @Override
                public void onSuccess() {
                    startChannelFragment(BrandsFragment.newInstance());
                }

                @Override
                public void onFailure() {
                    Toast.makeText(context, "Your reward limit for today exceeded", Toast.LENGTH_SHORT).show();
                }
            });
        });
        brandCv.setOnClickListener(view -> {
            Global.checkRemainingRewards(new Global.OnCountResult() {
                @Override
                public void onSuccess() {
                    startChannelFragment(ApplicationsFragment.newInstance());
                }

                @Override
                public void onFailure() {
                    Toast.makeText(context, "Your reward limit for today exceeded", Toast.LENGTH_SHORT).show();
                }
            });
        });
        channelCv.setOnClickListener(view -> {
            Global.checkRemainingRewards(new Global.OnCountResult() {
                @Override
                public void onSuccess() {
                    mCredential.setSelectedAccountName(AppPreferences.getUserAccountname());
                    String[] array = channelId.split("/");
                    String channelIdd = array[array.length - 1];
                    if (mCredential.getSelectedAccountName() != null) {
                        showProgressDialog();
                        new GetSubscriptions(mCredential, false).execute(channelIdd);
                    } else {
                        showProgressDialog();
                        mCredential.setSelectedAccount(new Account(AppPreferences.getUserAccountname(), context.getApplicationContext().getPackageName()));
                        new GetSubscriptions(mCredential, false).execute(channelIdd);
                    }
                }

                @Override
                public void onFailure() {
                    Toast.makeText(context, "Your reward limit for today exceeded", Toast.LENGTH_SHORT).show();
                }
            });
        });
        videoCv.setOnClickListener(view -> startChannelFragment(VideosFragment.newInstance()));
        ownChannelCv.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(channelId));
            startActivity(intent);
        });

        offerWallCv.setOnClickListener(view -> {

            OffersInit.getInstance().showOfferWall(activity);
        });

        fbVideoCv.setOnClickListener(view -> {
            startChannelFragment(FBvideoFragment.newInstance());
        });

        instaVideoCv.setOnClickListener(view -> startChannelFragment(InstaVideoFragment.newInstance()));
    }

    private void startChannelFragment(Fragment fragment) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void getBannerList() {
        Call<Response.BannerImagesResponse> call = RestClient.getClient().getBannerImages();
        call.enqueue(new CustomNetworkCallback<Response.BannerImagesResponse>() {
            @Override
            public void onSuccess(Response.BannerImagesResponse response) {
                for (int i = 0; i< response.data.size(); i++)
                    bannerlist.add(response.data.get(i).bannerUrl);
                setSliderImages();

            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }

    private void setSliderImages() {
        ScreenSlidePagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(activity.getSupportFragmentManager(), bannerlist);
        mPager.setAdapter(pagerAdapter);
        final int[] i = {0};
        final Handler h = new Handler(Looper.getMainLooper());
        final Runnable r = new Runnable() {
            public void run() {
                mPager.setCurrentItem(++i[0], true);
                h.postDelayed(this, 5000);
                if (i[0] == bannerlist.size()) {
                    i[0] = -1;
                }
            }
        };
        h.postDelayed(r, 4000);
    }

    private void getRewardCategories() {
        showDotsLayout();
        Call<Response.CategoryResponse> call = RestClient.getClient().getCategories(AppPreferences.getUserToken());
        call.enqueue(new Callback<Response.CategoryResponse>() {
            @Override
            public void onResponse(@NonNull Call<Response.CategoryResponse> call,
                                   @NonNull retrofit2.Response<Response.CategoryResponse> response) {
                if (response.body() != null && response.body().data != null && response.body().data.size() > 0) {
                    setUpData(response.body().data);
                    showScrollLayout();
                } else {
                    showNoDataLayout();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Response.CategoryResponse> call,
                                  @NonNull Throwable t) {
                showNoDataLayout();
                DialogUtils.showErrorDialog(context, "error occurred");
            }
        });
    }

    private void setUpData(List<Model.Category> categoryList) {
        for (int i = 0; i < 6; i++) {
            Model.Category category = categoryList.get(i);
            if (i == 0) {
                appTitle.setText(category.cName);
                appDesc.setText(DateUtil.calculateTimeAgo(category.cDate));
                appRewardCount.setText(category.cRewardCount);
            } else if (i == 1) {
                brandTitle.setText(category.cName);
                brandDesc.setText(DateUtil.calculateTimeAgo(category.cDate));
                brandRewardCount.setText(category.cRewardCount);
            } else if (i == 2) {
                channelTitle.setText(category.cName);
                channelDesc.setText(DateUtil.calculateTimeAgo(category.cDate));
                channelRewardCount.setText(category.cRewardCount);
            } else if (i == 3){
                videoTitle.setText(category.cName);
                videoDesc.setText(DateUtil.calculateTimeAgo(category.cDate));
                videoRewardCount.setText(category.cRewardCount);
            } else if (i == 4){
                fbVideoTitle.setText(category.cName);
                fbVideoDesc.setText(DateUtil.calculateTimeAgo(category.cDate));
                fbRewardCound.setText(category.cRewardCount);
            } else {
                instaVideoTitle.setText(category.cName);
                instaVideoDesc.setText(DateUtil.calculateTimeAgo(category.cDate));
                instaRewardCount.setText(category.cRewardCount);
            }
        }
    }

    private void showDotsLayout() {
        scrollView.setVisibility(View.GONE);
        dotsLayout.setVisibility(View.VISIBLE);
        noData.setVisibility(View.GONE);
    }

    private void showScrollLayout() {
        scrollView.setVisibility(View.VISIBLE);
        dotsLayout.setVisibility(View.GONE);
        noData.setVisibility(View.GONE);
    }

    private void showNoDataLayout() {
        noData.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
        dotsLayout.setVisibility(View.GONE);
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
        if (requestCode == 12) {
            String[] array = channelId.split("/");
            String channelId = array[array.length - 1];
            mCredential.setSelectedAccountName(AppPreferences.getUserAccountname());
            if (mCredential.getSelectedAccountName() != null) {
                hideProgressDialog();
                new GetSubscriptions(mCredential, true).execute(channelId);
            } else {
                hideProgressDialog();
                mCredential.setSelectedAccount(new Account(AppPreferences.getUserAccountname(), context.getApplicationContext().getPackageName()));
                new GetSubscriptions(mCredential, true).execute(channelId);
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    public class GetSubscriptions extends AsyncTask<String, Void, SubscriptionListResponse> {

        private YouTube youTubeDataApi;
        private Exception mLastError;
        private boolean fromactivityResult;

        GetSubscriptions(GoogleAccountCredential credential, boolean fromactivityResult) {
            this.fromactivityResult = fromactivityResult;
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
                if (fromactivityResult) {
                    Toast.makeText(context, "You did not subscribed the channel", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(context, WebviewActivity.class);
                    intent.putExtra(Constant.URL, channelId);
                    startActivityForResult(intent, 12);
                }
            } else {

                startChannelFragment(ChannelFragment.newInstance());

            }
        }

        @Override
        protected void onCancelled() {
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
                    hideProgressDialog();
                    Toast.makeText(context, mLastError.toString(), Toast.LENGTH_SHORT).show();
                }
            } else {
                hideProgressDialog();
                Toast.makeText(context, "unknown error", Toast.LENGTH_SHORT).show();
            }
        }
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

}
