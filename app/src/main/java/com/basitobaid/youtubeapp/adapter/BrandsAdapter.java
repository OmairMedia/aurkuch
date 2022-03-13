package com.basitobaid.youtubeapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.basitobaid.youtubeapp.BuildConfig;
import com.basitobaid.youtubeapp.R;
import com.basitobaid.youtubeapp.fragment.ApplicationDetailsFragment;
import com.basitobaid.youtubeapp.fragment.BrandDetailsFragment;
import com.basitobaid.youtubeapp.network.Model;
import com.basitobaid.youtubeapp.utils.Global;
import com.basitobaid.youtubeapp.utils.ImageUtils;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BrandsAdapter extends RecyclerView.Adapter<BrandsAdapter.ViewHolder> {
    private Context context;
    private List<Model.Reward> rewards;

    public BrandsAdapter(Context context, List<Model.Reward> rewards) {
        this.context = context;
        this.rewards = rewards;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_application_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandsAdapter.ViewHolder holder, int position) {

        holder.bindTo(rewards.get(position));
        holder.itemView.setOnClickListener(view -> {
            Global.checkRemainingRewards(new Global.OnCountResult() {
                @Override
                public void onSuccess() {
                    //call nextfragment
                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, BrandDetailsFragment.newInstance(rewards.get(position)))
                            .addToBackStack(null)
                            .commit();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(context, "Your reward limit for today exceeded", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return rewards != null ? rewards.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rewardImage)
        ImageView rewardImage;
        @BindView(R.id.reward_name)
        TextView rewardName;
        @BindView(R.id.reward_desc)
        TextView rewardDesc;
        @BindView(R.id.rewards_left)
        TextView rewardsLeft;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindTo(Model.Reward reward) {

            ImageUtils.loadImage(context, rewardImage, reward.rImage);
            rewardName.setText(reward.cName);
            rewardDesc.setText(reward.rDescription);
            rewardsLeft.setText(String.format("%s left", reward.rLeft));
        }


    }
}
