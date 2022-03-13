package com.basitobaid.youtubeapp.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basitobaid.youtubeapp.R;
import com.basitobaid.youtubeapp.adapter.ApplicationsAdapter;
import com.basitobaid.youtubeapp.network.GlobalCall;
import com.basitobaid.youtubeapp.network.Model;
import com.basitobaid.youtubeapp.utils.ConnectionManager;
import com.basitobaid.youtubeapp.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class ApplicationsFragment extends Fragment implements GlobalCall.RewardCallback {

    @BindView(R.id.app_recycler)
    RecyclerView appRecycler;
    @BindView(R.id.no_data)
    ImageView noData;
    @BindView(R.id.no_internet)
    ImageView noInternet;
    @BindView(R.id.dots_layout)
    LinearLayout dotsLayout;
    private List<Model.Reward> rewardList;
    private Context context;
    private ApplicationsAdapter adapter;

    private ApplicationsFragment() {
        // Required empty public constructor
    }

    public static ApplicationsFragment newInstance() {
        return new ApplicationsFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_applications, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rewardList = new ArrayList<>();
        GlobalCall.RewardCallback rewardCallback = this;
        context = getContext();
        adapter = new ApplicationsAdapter(context, rewardList);
        appRecycler.setLayoutManager(new LinearLayoutManager(context));
        appRecycler.setAdapter(adapter);
        if (ConnectionManager.isDeviceOnline(context)) {
            showDotsLayout();
            new GlobalCall(rewardCallback).getRewardList("2");
        } else {
            showNoInternetLayout();
        }
    }

    /**
     * Reward list callbacks
     */
    @Override
    public void getRewardList(List<Model.Reward> rewards) {
        rewardList.addAll(rewards);
        adapter.notifyDataSetChanged();
        showRecyclerLayout();
    }

    @Override
    public void onError() {
        showNoDataLayout();
        DialogUtils.showErrorDialog(context, "error occurred");
    }

    @Override
    public void onEmpty() {
        showNoDataLayout();
    }

    private void showNoInternetLayout() {
        appRecycler.setVisibility(View.GONE);
        noInternet.setVisibility(View.VISIBLE);
        noData.setVisibility(View.GONE);
        dotsLayout.setVisibility(View.GONE);
    }

    private void showNoDataLayout() {
        appRecycler.setVisibility(View.GONE);
        noInternet.setVisibility(View.GONE);
        noData.setVisibility(View.VISIBLE);
        dotsLayout.setVisibility(View.GONE);
    }

    private void showRecyclerLayout() {
        appRecycler.setVisibility(View.VISIBLE);
        noInternet.setVisibility(View.GONE);
        noData.setVisibility(View.GONE);
        dotsLayout.setVisibility(View.GONE);
    }

    private void showDotsLayout() {
        dotsLayout.setVisibility(View.VISIBLE);
        appRecycler.setVisibility(View.GONE);
        noInternet.setVisibility(View.GONE);
        noData.setVisibility(View.GONE);
    }

    @OnClick(R.id.back_button)
    public void onViewClicked() {
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStackImmediate();
    }
}
