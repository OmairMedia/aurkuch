package com.basitobaid.youtubeapp.fragment;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basitobaid.youtubeapp.R;
import com.basitobaid.youtubeapp.activity.OTPActivity;
import com.basitobaid.youtubeapp.adapter.AdapterRecentRecived;
import com.basitobaid.youtubeapp.network.Model;
import com.basitobaid.youtubeapp.network.Response;
import com.basitobaid.youtubeapp.network.RestClient;
import com.basitobaid.youtubeapp.utils.AppPreferences;
import com.basitobaid.youtubeapp.utils.ConnectionManager;
import com.basitobaid.youtubeapp.utils.Constant;
import com.basitobaid.youtubeapp.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * A simple {@link Fragment} subclass.
 */
public class WalletFragment extends Fragment {

    @BindView(R.id.total_wallet_amount)
    TextView totalWalletAmount;
    @BindView(R.id.RedeemPoints)
    TextView RedeemPoints;
    @BindView(R.id.filterText)
    TextView filterText;
    @BindView(R.id.filterbtn)
    ImageView filterbtn;
    @BindView(R.id.recyclerViewRecentRecived)
    RecyclerView recyclerViewRecentRecived;
    @BindView(R.id.no_data)
    ImageView noData;
    @BindView(R.id.no_internet)
    ImageView noInternet;
    @BindView(R.id.dots_layout)
    LinearLayout dotsLayout;
    private FragmentActivity activity;
    private Context context;
    private List<Model.Wallet> walletList;

    private AdapterRecentRecived adapter;

    public WalletFragment() {
        // Required empty public constructor
    }

    public static WalletFragment newInstance() {
        return new WalletFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        context = getContext();
        walletList = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        ButterKnife.bind(this, view);
        enableDisableRedeemButton();
        // Inflate the layout for this fragment
        return view;
    }

    private void enableDisableRedeemButton() {
        if (totalWalletAmount.getText().toString().equals("Rs. 0")) {
            RedeemPoints.setEnabled(false);
            RedeemPoints.setAlpha(0.5f);
        } else {
            RedeemPoints.setEnabled(true);
            RedeemPoints.setAlpha(1f);
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        RedeemPoints.setOnClickListener(v -> {
            if (ConnectionManager.isDeviceOnline(context)) {
                if (AppPreferences.getUserPhoneVerified()) {
                    redeemCash();
                } else {
                    Intent intent = new Intent(context, OTPActivity.class);
                    intent.putExtra(Constant.FROM, "wallet");
                    startActivityForResult(intent, Constant.OTP_ACTIVITY_REQUEST);
                }
            } else {
                Toast.makeText(activity, "no internet connection", Toast.LENGTH_SHORT).show();
            }
        });

        filterbtn = view.findViewById(R.id.filterbtn);
        filterbtn.setOnClickListener(v -> {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
// ...Irrelevant code for customizing the buttons and title
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_for_filter, null);
            TextView all = dialogView.findViewById(R.id.all);
            TextView pending = dialogView.findViewById(R.id.pending);
            TextView paid = dialogView.findViewById(R.id.paid);
            TextView approved = dialogView.findViewById(R.id.approved);

            dialogBuilder.setView(dialogView);
            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
            all.setOnClickListener(view1 -> {
                getWallet(null);
                alertDialog.dismiss();
            });
            pending.setOnClickListener(view1 -> {
                getWallet("0");
                alertDialog.dismiss();
            });
            paid.setOnClickListener(view1 -> {
                getWallet("2");
                alertDialog.dismiss();
            });
            approved.setOnClickListener(view1 -> {
                getWallet("1");
                alertDialog.dismiss();
            });
        });
        recyclerViewRecentRecived = view.findViewById(R.id.recyclerViewRecentRecived);
        adapter = new AdapterRecentRecived(context, walletList);
        recyclerViewRecentRecived.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewRecentRecived.setAdapter(adapter);
        if (ConnectionManager.isDeviceOnline(context)) {
            showDotsLayout();
            getWallet(null);
        } else {
            showNoInternetLayout();
        }
    }

    private void getWallet(String type) {

        walletList.clear();
        Call<Response.WalletResponse> call = RestClient.getClient().getWallet(AppPreferences.getUserToken(), type);
        call.enqueue(new Callback<Response.WalletResponse>() {
            @Override
            public void onResponse(@NonNull Call<Response.WalletResponse> call,
                                   @NonNull retrofit2.Response<Response.WalletResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().data != null) {
                        if (response.body().data.size() > 0) {
                            if (response.body().data.get(0).wRewardStatus.equalsIgnoreCase("pending")) {
                                totalWalletAmount.setText(String.format("Rs. %s", response.body().data.get(0).wAmount));
                            }
                            walletList.addAll(response.body().data);
                            showRecyclerLayout();
                            enableDisableRedeemButton();
                        } else {
                            enableDisableRedeemButton();
                            showNoDataLayout();
                        }
                    } else {
                        if (response.body() != null) {
                            enableDisableRedeemButton();
                            showNoDataLayout();
                        }
                    }
                    setFilterText(type);
                    adapter.notifyDataSetChanged();
                } else {
                    showNoDataLayout();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Response.WalletResponse> call,
                                  @NonNull Throwable t) {
                DialogUtils.showErrorDialog(context, "error occurred");
                showNoDataLayout();
            }
        });
    }

    private void setFilterText(String type) {
        if (type == null) {
            filterText.setText("Recent Rewards");
            return;
        }
        switch (type) {
            case "0":
                filterText.setText("Recent Pending");
                break;
            case "1":
                filterText.setText("Recent Approved");
                break;
            case "2":
                filterText.setText("Recent Paid");
                break;
            default:
                filterText.setText("Recent Rewards");
                break;
        }
    }

    private void redeemCash() {
        Call<Response.RedeemResponse> call = RestClient.getClient().requestRedeem(AppPreferences.getUserToken());
        call.enqueue(new Callback<Response.RedeemResponse>() {
            @Override
            public void onResponse(@NonNull Call<Response.RedeemResponse> call,
                                   @NonNull retrofit2.Response<Response.RedeemResponse> response) {
                if (response.body() != null) {
                    if (response.body().status.equals("200")) {
                        String message = "Your transaction of amount\n" + response.body().data.tAmount + "\n has been approved";
                        DialogUtils.showSuccessDialog(context, message);
                    } else if (response.body().status.equals("203")) {
                        DialogUtils.showInfoDialog(context, response.body().message);
                    } else if (response.body().status.equals("202")) {
                        DialogUtils.showInfoDialog(context, response.body().message);
                    }
                } else {
                    DialogUtils.showErrorDialog(context, response.message());
                }

            }

            @Override
            public void onFailure(@NonNull Call<Response.RedeemResponse> call, @NonNull Throwable t) {
                DialogUtils.showErrorDialog(context, t.getMessage());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.OTP_ACTIVITY_REQUEST) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                Log.d("TAG", "onActivityResult: "+ uri);
                if (uri.toString().equals("verified")) {
                    redeemCash();
                } else {
                    DialogUtils.showErrorDialog(context, "Your phone number is invalid. contact to support to provide valid number");

                }
            }
        }
    }

    @OnClick(R.id.view_transactions)
    void onViewClicked() {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, TransactionsFragment.newInstance(totalWalletAmount.getText().toString()))
                .addToBackStack(null)
                .commit();
    }

    private void showNoInternetLayout() {
        recyclerViewRecentRecived.setVisibility(View.GONE);
        noInternet.setVisibility(View.VISIBLE);
        noData.setVisibility(View.GONE);
        dotsLayout.setVisibility(View.GONE);
    }

    private void showNoDataLayout() {
        recyclerViewRecentRecived.setVisibility(View.GONE);
        noInternet.setVisibility(View.GONE);
        noData.setVisibility(View.VISIBLE);
        dotsLayout.setVisibility(View.GONE);
    }

    private void showRecyclerLayout() {
        recyclerViewRecentRecived.setVisibility(View.VISIBLE);
        noInternet.setVisibility(View.GONE);
        noData.setVisibility(View.GONE);
        dotsLayout.setVisibility(View.GONE);
    }

    private void showDotsLayout() {
        dotsLayout.setVisibility(View.VISIBLE);
        recyclerViewRecentRecived.setVisibility(View.GONE);
        noInternet.setVisibility(View.GONE);
        noData.setVisibility(View.GONE);
    }

}
