package com.basitobaid.youtubeapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.basitobaid.youtubeapp.R;
import com.basitobaid.youtubeapp.network.Model;
import com.basitobaid.youtubeapp.utils.DateUtil;

import java.util.List;

public class AdapterRecentRecived extends RecyclerView.Adapter<AdapterRecentRecived.RecyclerViewHolder> {

    public Context context;
    private List<Model.Wallet> walletList;
    public AdapterRecentRecived(Context context, List<Model.Wallet> walletList){
        this.context=context;
        this.walletList = walletList;
    }
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater  = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.items_recents_recieved,parent,false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder Holder, int position) {
        Model.Wallet wallet = walletList.get(position);

        Holder.rewardAmount.setText(String.format("Rs %s", wallet.rewardAmount));
        Holder.rewardDate.setText(DateUtil.calculateTimeAgo(wallet.wRewardDate));
        Holder.closingBalance.setText(String.format("Closing balance: Rs %s", wallet.wAmount));
        //Setting Click Listener On Item
        Holder.itemView.setOnClickListener(v -> {
        });
    }


    @Override
    public int getItemCount() {
        return walletList != null? walletList.size(): 0;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{
        TextView rewardAmount, rewardDate, closingBalance;
        public RecyclerViewHolder(View itemView){
            super(itemView);
            rewardAmount = itemView.findViewById(R.id.reward_amount);
            rewardDate = itemView.findViewById(R.id.reward_date);
            closingBalance = itemView.findViewById(R.id.closing_balance);
        }
    }
}