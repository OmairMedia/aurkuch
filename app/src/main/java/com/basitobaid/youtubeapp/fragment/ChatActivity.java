package com.basitobaid.youtubeapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basitobaid.youtubeapp.R;
import com.basitobaid.youtubeapp.adapter.ChatMsgAdapter;
import com.basitobaid.youtubeapp.network.CustomNetworkCallback;
import com.basitobaid.youtubeapp.network.Model;
import com.basitobaid.youtubeapp.network.Response;
import com.basitobaid.youtubeapp.network.RestClient;
import com.basitobaid.youtubeapp.utils.AppPreferences;
import com.basitobaid.youtubeapp.utils.ConnectionManager;
import com.basitobaid.youtubeapp.utils.DialogUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;

public class ChatActivity extends AppCompatActivity {


    @BindView(R.id.chat_recycler_view)
    RecyclerView chatRecyclerView;
    @BindView(R.id.message_et)
    EditText messageEt;
    ChatMsgAdapter adapter;
    private List<Model.Chat> chatList;
    String chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        initRecycler();
    }

    private void initRecycler() {
        chatList = new ArrayList<>();
        adapter = new ChatMsgAdapter(this, chatList);
        chatRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        chatRecyclerView.setLayoutManager(layoutManager);
        if (ConnectionManager.isDeviceOnline(this) ) {
            getMessages();
        } else {
            Toast.makeText(this, "no internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick({R.id.send_iv, R.id.back_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.send_iv:
                if (!messageEt.getText().toString().isEmpty()) {
                    sendMessage(messageEt.getText().toString());
                }
                break;
        }
    }

    public void sendMessage(String msg) {
        String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date());
        Model.Chat msgDto = new Model.Chat();
        msgDto.messageText = msg;
        msgDto.messageType = Model.Chat.MSG_TYPE_SENT;
        msgDto.messageDate = currentDateandTime;
        msgDto.messageStatus = "Delivered";
        chatList.add(0,msgDto);
        int newMsgPosition = 0;
        adapter.notifyItemInserted(newMsgPosition);
        messageEt.setText(" ");
        chatRecyclerView.scrollToPosition(newMsgPosition);

        String token = AppPreferences.getUserToken();


        Call<Response.CommonResponse> call = RestClient.getClient().sendMessage(token, msg, chatId);
        call.enqueue(new Callback<Response.CommonResponse>() {
            @Override
            public void onResponse(@NonNull Call<Response.CommonResponse> call,
                                   @NonNull retrofit2.Response<Response.CommonResponse> response) {
                if (response.isSuccessful()) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Response.CommonResponse> call, @NonNull Throwable t) {
                Toast.makeText(ChatActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void getMessages() {
        String token = AppPreferences.getUserToken();
        Call<Response.ChatResponse> call = RestClient.getClient().getChat(token);
        call.enqueue(new CustomNetworkCallback<Response.ChatResponse>() {
            @Override
            public void onSuccess(Response.ChatResponse response) {
                if (response.data != null) {
//                    chatModelList.clear();
//                    if (adapter.isFooterLoaded()) {adapter.removeLoadingFooter();}
                    for (int i = 0; i< response.data.size(); i++) {
                        Model.Chat message = response.data.get(i);
                        chatId = message.chatId;
                        if (message.senderId.equals(AppPreferences.getUserId())) {
                            message.messageType = Model.Chat.MSG_TYPE_SENT;
                        } else {
                            message.messageType = Model.Chat.MSG_TYPE_RECEIVED;
                        }
                        chatList.add(message);
                    }

                    // Notify recycler view insert one new data.
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onError(Throwable throwable) {
                DialogUtils.showErrorDialog(ChatActivity.this, "error occurred");
            }
        });

    }
}
