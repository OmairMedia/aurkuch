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
import com.basitobaid.youtubeapp.adapter.BrandsAdapter;
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
public class BrandsFragment extends Fragment implements GlobalCall.RewardCallback {

    @BindView(R.id.brand_recycler)
    RecyclerView brandRecycler;
    @BindView(R.id.no_data)
    ImageView noData;
    @BindView(R.id.no_internet)
    ImageView noInternet;
    @BindView(R.id.dots_layout)
    LinearLayout dotsLayout;
    private List<Model.Reward> rewardList;
    private Context context;
    private BrandsAdapter adapter;

    private BrandsFragment() {
        // Required empty public constructor
    }

    public static BrandsFragment newInstance() {
        return new BrandsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_brands, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rewardList = new ArrayList<>();
        GlobalCall.RewardCallback rewardCallback = this;
        context = getContext();
        adapter = new BrandsAdapter(context, rewardList);
        brandRecycler.setLayoutManager(new LinearLayoutManager(context));
        brandRecycler.setAdapter(adapter);
        if (ConnectionManager.isDeviceOnline(context)) {
            showDotsLayout();
            new GlobalCall(rewardCallback).getRewardList("1");
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
        DialogUtils.showErrorDialog(context, "error occurred");
        showNoDataLayout();
    }

    @Override
    public void onEmpty() {
        showNoDataLayout();
    }

    private void showNoInternetLayout() {
        brandRecycler.setVisibility(View.GONE);
        noInternet.setVisibility(View.VISIBLE);
        noData.setVisibility(View.GONE);
        dotsLayout.setVisibility(View.GONE);
    }

    private void showNoDataLayout() {
        brandRecycler.setVisibility(View.GONE);
        noInternet.setVisibility(View.GONE);
        noData.setVisibility(View.VISIBLE);
        dotsLayout.setVisibility(View.GONE);
    }

    private void showRecyclerLayout() {
        brandRecycler.setVisibility(View.VISIBLE);
        noInternet.setVisibility(View.GONE);
        noData.setVisibility(View.GONE);
        dotsLayout.setVisibility(View.GONE);
    }

    private void showDotsLayout() {
        dotsLayout.setVisibility(View.VISIBLE);
        brandRecycler.setVisibility(View.GONE);
        noInternet.setVisibility(View.GONE);
        noData.setVisibility(View.GONE);
    }

    @OnClick(R.id.back_button)
    public void onViewClicked() {
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStackImmediate();
    }
}
