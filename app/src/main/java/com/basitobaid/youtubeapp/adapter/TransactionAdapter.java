package com.basitobaid.youtubeapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.basitobaid.youtubeapp.R;
import com.basitobaid.youtubeapp.network.Model;
import com.basitobaid.youtubeapp.utils.DateUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.RecyclerViewHolder> {

    public Context context;
    private List<Model.Transaction> transactions;

    public TransactionAdapter(Context context, List<Model.Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.items_redeem_points, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder Holder, int position) {
        //Setting Click Listener On Item
        Holder.bindTo(transactions.get(position));
        Holder.itemView.setOnClickListener(v -> {
        });
    }

    @Override
    public int getItemCount() {
        return transactions != null ? transactions.size() : 0;
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.trans_name)
        TextView transName;
        @BindView(R.id.trans_date)
        TextView transDate;
        @BindView(R.id.status_tv)
        TextView statusTv;
        @BindView(R.id.status_cv)
        CardView statusCv;
        RecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindTo(Model.Transaction transaction) {
            transName.setText("Rs. " + transaction.tAmount);
            transDate.setText(DateUtil.calculateTimeAgo(transaction.tCreatedDate));
            statusTv.setText(transaction.tStatus);
            switch (transaction.tStatus) {
                case "Paid":
                    statusCv.setCardBackgroundColor(context.getResources().getColor(R.color.green_status));
                    break;
                case "Approved":
                    statusCv.setCardBackgroundColor(context.getResources().getColor(R.color.yellow_status));
                    break;
                default:
                    statusCv.setCardBackgroundColor(context.getResources().getColor(R.color.red_status));
                    break;
            }
        }
    }
}