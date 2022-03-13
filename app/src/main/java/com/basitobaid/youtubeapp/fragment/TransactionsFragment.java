package com.basitobaid.youtubeapp.fragment;

import android.content.Context;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basitobaid.youtubeapp.R;
import com.basitobaid.youtubeapp.adapter.TransactionAdapter;
import com.basitobaid.youtubeapp.network.CustomNetworkCallback;
import com.basitobaid.youtubeapp.network.Model;
import com.basitobaid.youtubeapp.network.Response;
import com.basitobaid.youtubeapp.network.RestClient;
import com.basitobaid.youtubeapp.utils.AppPreferences;
import com.basitobaid.youtubeapp.utils.ConnectionManager;
import com.basitobaid.youtubeapp.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;


public class TransactionsFragment extends Fragment {
    @BindView(R.id.back_button)
    ImageView backButton;
    @BindView(R.id.recyclerViewRedeemPoints)
    RecyclerView recyclerViewRedeemPoints;
    @BindView(R.id.no_data)
    ImageView noData;
    @BindView(R.id.no_internet)
    ImageView noInternet;
    @BindView(R.id.dots_layout)
    LinearLayout dotsLayout;
    @BindView(R.id.amount_tv)
    TextView amountTv;
    private FragmentActivity activity;
    private Context context;
    private List<Model.Transaction> transactions;
    private TransactionAdapter adapter;

    public TransactionsFragment() {
        // Required empty public constructor
    }


    public static TransactionsFragment newInstance(String amount) {
        TransactionsFragment fragment = new TransactionsFragment();
        Bundle args = new Bundle();
        args.putString("amount", amount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        activity = getActivity();
        transactions = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_redeem_points, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null)
            amountTv.setText(getArguments().getString("amount"));
        recyclerViewRedeemPoints.setLayoutManager(new LinearLayoutManager(context));
        adapter = new TransactionAdapter(context, transactions);

        recyclerViewRedeemPoints.setAdapter(adapter);
        if (ConnectionManager.isDeviceOnline(context)) {
            showDotsLayout();
            getTrans();
        } else {
            showNoInternetLayout();
        }
    }

    private void getTransactions() {
        Call<Response.TransactionResponse> call = RestClient.getClient().getTransactions(AppPreferences.getUserToken());
        call.enqueue(new Callback<Response.TransactionResponse>() {
            @Override
            public void onResponse(@NonNull Call<Response.TransactionResponse> call,
                                   @NonNull retrofit2.Response<Response.TransactionResponse> response) {

            }

            @Override
            public void onFailure(@NonNull Call<Response.TransactionResponse> call,
                                  @NonNull Throwable t) {

            }
        });
    }

    private void getTrans() {
        Call<Response.TransactionResponse> call = RestClient.getClient().getTransactions(AppPreferences.getUserToken());
        call.enqueue(new CustomNetworkCallback<Response.TransactionResponse>() {
            @Override
            public void onSuccess(Response.TransactionResponse response) {
                if (response.data.size() > 0) {
                    transactions.addAll(response.data);
                    adapter.notifyDataSetChanged();
                    showRecyclerLayout();
                } else {
                    showNoDataLayout();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                showNoDataLayout();
            }
        });
    }

    private void showNoInternetLayout() {
        recyclerViewRedeemPoints.setVisibility(View.GONE);
        noInternet.setVisibility(View.VISIBLE);
        noData.setVisibility(View.GONE);
        dotsLayout.setVisibility(View.GONE);
    }

    private void showNoDataLayout() {
        recyclerViewRedeemPoints.setVisibility(View.GONE);
        noInternet.setVisibility(View.GONE);
        noData.setVisibility(View.VISIBLE);
        dotsLayout.setVisibility(View.GONE);
    }

    private void showRecyclerLayout() {
        recyclerViewRedeemPoints.setVisibility(View.VISIBLE);
        noInternet.setVisibility(View.GONE);
        noData.setVisibility(View.GONE);
        dotsLayout.setVisibility(View.GONE);
    }

    private void showDotsLayout() {
        dotsLayout.setVisibility(View.VISIBLE);
        recyclerViewRedeemPoints.setVisibility(View.GONE);
        noInternet.setVisibility(View.GONE);
        noData.setVisibility(View.GONE);
    }

    @OnClick(R.id.back_button)
    void onViewClicked() {
        activity.getSupportFragmentManager().popBackStackImmediate();
    }
}
