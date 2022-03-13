package com.basitobaid.youtubeapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.basitobaid.youtubeapp.R;
import com.basitobaid.youtubeapp.network.CustomNetworkCallback;
import com.basitobaid.youtubeapp.network.Model;
import com.basitobaid.youtubeapp.network.Response;
import com.basitobaid.youtubeapp.network.RestClient;
import com.basitobaid.youtubeapp.utils.AppPreferences;

import java.util.List;
import java.util.StringTokenizer;

import retrofit2.Call;
import retrofit2.Callback;

public class ChatMsgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Model.Chat> chatModelList;
    private Context context;
    private String date1;

    private boolean isLoadingAdded = false;
    private static final int LOADING = 775;
    private static final int ITEM = 665;

    private Model.Chat msgDto;

    public ChatMsgAdapter(Context context, List<Model.Chat> msgDtoList) {

        this.chatModelList = msgDtoList;
        this.context = context;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        switch (getItemViewType(position)) {
            case ITEM:
                msgDto = chatModelList.get(position);

                ChatAppMsgViewHolder chatHolder = (ChatAppMsgViewHolder) holder;
                // If the message is a received message.
                if(msgDto.messageType.equals(Model.Chat.MSG_TYPE_RECEIVED))
                {
                    String dateTime = msgDto.messageDate;
                    ((ChatAppMsgViewHolder) holder).leftMsgLayout.setVisibility(View.VISIBLE);

                    StringTokenizer token = new StringTokenizer(dateTime);
                    date1 = token.nextToken();
                    String time1 = token.nextToken();


                    chatHolder.rightMsgLayout.setVisibility(LinearLayout.GONE);
                    chatHolder.leftMsgTextView.setText(msgDto.messageText);
                    chatHolder.leftMsgLayout.setVisibility(LinearLayout.VISIBLE);



                    chatHolder.recTiming.setText(""+time1.split(":")[0]+":"+time1.split(":")[1]);
                    if (!msgDto.messageStatus.equals("Seen")) {
                        setMessageSeen(msgDto.messageId, position);
                    }
                    System.out.println("Time Display: " +time1); // <-- I got result here


                }
                // If the message is a sent message.
                else if(msgDto.messageType.equals(Model.Chat.MSG_TYPE_SENT))
                {
                    // Show sent message in right linearlayout.
                    chatHolder.rightMsgLayout.setVisibility(LinearLayout.VISIBLE);
                    chatHolder.rightMsgTextView.setText(msgDto.messageText);
                    chatHolder.leftMsgLayout.setVisibility(LinearLayout.GONE);
                    StringTokenizer token = new StringTokenizer(msgDto.messageDate);
                    String date1 = token.nextToken();
                    String time = token.nextToken();

                    chatHolder.sendTime.setText(time.split(":")[0]+":"+time.split(":")[1]);
                    if (msgDto.messageStatus.equals("Seen")) {
                        chatHolder.textTick.setText("✓✓");
                        chatHolder.textTick.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                    } else {
                        chatHolder.textTick.setText("✓✓");
                        chatHolder.textTick.setTextColor(context.getResources().getColor(android.R.color.black));
                    }
                }
                break;
            case LOADING:
                break;
        }

    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType)
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_chat_list, parent, false);

        return  new ChatAppMsgViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return chatModelList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ChatAppMsgViewHolder extends RecyclerView.ViewHolder {

        LinearLayout leftMsgLayout;
        LinearLayout rightMsgLayout;
        TextView leftMsgTextView, recTiming, textTick, sendTime;
        TextView rightMsgTextView;



        public ChatAppMsgViewHolder(View itemView) {
            super(itemView);

            leftMsgLayout = itemView.findViewById(R.id.chat_left_msg_layout);
            rightMsgLayout = itemView.findViewById(R.id.chat_right_msg_layout);

            leftMsgTextView = itemView.findViewById(R.id.chat_left_msg_text_view);
            rightMsgTextView = itemView.findViewById(R.id.chat_right_msg_text_view);
            recTiming = itemView.findViewById(R.id.recTiming);
            textTick = itemView.findViewById(R.id.textTick);
            sendTime = itemView.findViewById(R.id.sendMesgCurrentTime);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return (position == chatModelList.size()-1 && isLoadingAdded) ? LOADING : ITEM;
    }

    private void setMessageSeen(String msgId, int position) {
        Call<Response.CommonResponse> call = RestClient.getClient().updateMessage(AppPreferences.getUserToken(), msgId);
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
    public void add(Model.Chat chat) {
        chatModelList.add(chat);
        notifyItemInserted(chatModelList.size() - 1);
    }

    public boolean isEmpty() { return getItemCount() == 0; }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Model.Chat());
    }

    public boolean isFooterLoaded() {
        return isLoadingAdded;
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = chatModelList.size() - 1;
        Model.Chat item = chatModelList.get(position);
        if (item != null) {
            chatModelList.remove(position);
            notifyItemRemoved(position);
        }
    }

}

