package com.basitobaid.youtubeapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.basitobaid.youtubeapp.R;
import com.basitobaid.youtubeapp.fragment.ChatActivity;
import com.basitobaid.youtubeapp.fragment.WalletFragment;
import com.basitobaid.youtubeapp.network.CustomNetworkCallback;
import com.basitobaid.youtubeapp.network.Model;
import com.basitobaid.youtubeapp.network.Response;
import com.basitobaid.youtubeapp.network.RestClient;
import com.basitobaid.youtubeapp.utils.AppPreferences;
import com.basitobaid.youtubeapp.utils.DateUtil;

import java.util.List;
import retrofit2.Call;


public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private List<Model.Notification> notificationList;

    private boolean isLoadingAdded = false;
    private static final int LOADING = 775;
    private static final int ITEM = 665;

    public NotificationAdapter(Context context, List<Model.Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
//        View view = LayoutInflater.from(context).inflate(R.layout.item_notification1, viewGroup, false);
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case ITEM:
                View view = inflater.inflate(R.layout.item_notificaton, parent, false);
                viewHolder = new ViewHolder(view);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new ProgressHolder(v2);
                break;
        }
//        View view = inflater.inflate(R.layout.order_raw_items, parent, false);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (getItemViewType(position)) {
            case ITEM:
                ViewHolder holder1 = (ViewHolder) holder;
                Model.Notification notification = notificationList.get(position);
                Log.d("TAG", "onBindViewHolder: "+ notification.desc + "___" + notification.nDate);
                holder1.title.setText(notification.desc);
                if (notification.nDate != null) {
                    holder1.desc.setText(DateUtil.calculateTimeAgo(notification.nDate));
                }
                if (notification.nStatus != null && notification.nStatus.equals("Seen")) {
                    holder1.container.setBackgroundColor(context.getResources().getColor(R.color.white));
                } else {
                    holder1.container.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                }
                holder1.itemView.setOnClickListener(v -> {
                    if (!notification.nStatus.equals("Seen")){
                        updateNotification(notification, position);
                    }
                    nextActivity(notification, position);
                });
                break;
            case LOADING:
                break;
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, desc;
        RelativeLayout container;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.notification_image);
            title = itemView.findViewById(R.id.notification_title);
            desc = itemView.findViewById(R.id.notification_desc);
            container = itemView.findViewById(R.id.activity_chat_header);
        }
    }

    protected class ProgressHolder extends RecyclerView.ViewHolder {
        //Declare ItemView Items;

        ProgressHolder(final View itemView) {
            super(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM;
    }

    private void nextActivity(Model.Notification note, int position) {
        if (note.type.equals("Referral")){
            ((FragmentActivity)context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, WalletFragment.newInstance())
                    .addToBackStack(null)
                    .commit();

        } else {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("from", "notifications");
            context.startActivity(intent);

        }
    }

    private void updateNotification(Model.Notification note, int position) {

        Call<Response.CommonResponse> call = RestClient.getClient().updateNotification(AppPreferences.getUserToken()
                , note.id);
        call.enqueue(new CustomNetworkCallback<Response.CommonResponse>() {
            @Override
            public void onSuccess(Response.CommonResponse response) {

            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }


    //HELPER MOTHODS
    public void add(Model.Notification note) {
        notificationList.add(note);
        notifyItemInserted(notificationList.size() - 1);
    }

    public boolean isEmpty() { return getItemCount() == 0; }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Model.Notification());
    }

    public boolean isFooterLoaded() {
        return isLoadingAdded;
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = notificationList.size() - 1;
        Model.Notification item = notificationList.get(position);
        if (item != null) {
            notificationList.remove(position);
            notifyItemRemoved(position);
        }
    }
}
