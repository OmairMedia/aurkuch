package com.basitobaid.youtubeapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.basitobaid.youtubeapp.R;
import com.basitobaid.youtubeapp.network.Model;
import com.basitobaid.youtubeapp.network.Response;
import com.basitobaid.youtubeapp.network.RestClient;
import com.basitobaid.youtubeapp.utils.AppPreferences;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;


public class ProfileFragment extends Fragment {


    @BindView(R.id.app_pending)
    TextView appPending;
    @BindView(R.id.app_paid)
    TextView appPaid;
    @BindView(R.id.app_approved)
    TextView appApproved;
    @BindView(R.id.app_total)
    TextView appTotal;
    @BindView(R.id.brand_pending)
    TextView brandPending;
    @BindView(R.id.brand_paid)
    TextView brandPaid;
    @BindView(R.id.brand_approved)
    TextView brandApproved;
    @BindView(R.id.brand_total)
    TextView brandTotal;
    @BindView(R.id.channel_pending)
    TextView channelPending;
    @BindView(R.id.channel_paid)
    TextView channelPaid;
    @BindView(R.id.channel_approved)
    TextView channelApproved;
    @BindView(R.id.channel_total)
    TextView channelTotal;
    @BindView(R.id.videos_pending)
    TextView videosPending;
    @BindView(R.id.videos_paid)
    TextView videosPaid;
    @BindView(R.id.videos_approved)
    TextView videosApproved;
    @BindView(R.id.videos_total)
    TextView videosTotal;
    @BindView(R.id.userName)
    TextView userName;
    @BindView(R.id.userEmail)
    TextView userEmail;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getProfile();
        userName.setText(AppPreferences.getUserAccountUserName());
        userEmail.setText(AppPreferences.getUserAccountname());

    }

    private void getProfile() {
        Call<Response.ProfileResponse> call = RestClient.getClient().getProfile(AppPreferences.getUserToken());
        call.enqueue(new Callback<Response.ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<Response.ProfileResponse> call,
                                   @NonNull retrofit2.Response<Response.ProfileResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().data != null) {
                        setData(response.body().data);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Response.ProfileResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void setData(List<Model.Profile> profiles) {
        for (int i = 0; i < 4; i++) {
            Model.Profile profile = profiles.get(i);
            if (i == 0) {
                brandPending.setText(profile.pendingAmount);
                brandPaid.setText(profile.paidAmount);
                brandApproved.setText(profile.approvedAmount);
                brandTotal.setText(profile.totalAmount);
            } else if (i == 1) {
                appPending.setText(profile.pendingAmount);
                appPaid.setText(profile.paidAmount);
                appApproved.setText(profile.approvedAmount);
                appTotal.setText(profile.totalAmount);

            } else if (i == 2) {
                channelPending.setText(profile.pendingAmount);
                channelPaid.setText(profile.paidAmount);
                channelApproved.setText(profile.approvedAmount);
                channelTotal.setText(profile.totalAmount);
            } else {
                videosPending.setText(profile.pendingAmount);
                videosPaid.setText(profile.paidAmount);
                videosApproved.setText(profile.approvedAmount);
                videosTotal.setText(profile.totalAmount);
            }
        }
    }

}
