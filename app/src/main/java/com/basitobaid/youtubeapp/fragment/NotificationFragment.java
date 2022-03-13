package com.basitobaid.youtubeapp.fragment;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.basitobaid.youtubeapp.R;
import com.basitobaid.youtubeapp.adapter.NotificationAdapter;
import com.basitobaid.youtubeapp.network.CustomNetworkCallback;
import com.basitobaid.youtubeapp.network.Model;
import com.basitobaid.youtubeapp.network.Response;
import com.basitobaid.youtubeapp.network.RestClient;
import com.basitobaid.youtubeapp.utils.AppPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    private RecyclerView recyclerViewNotifications;
    private NotificationAdapter adapter;
    private List<Model.Notification> notifications;
    private RecyclerView.LayoutManager layoutManager;
    private boolean isLoading = false;
    private int currentPage = 1;
    private int lastPage;
    private Context context;
    private RelativeLayout noNoteLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        notifications = new ArrayList<>();
        recyclerViewNotifications = view.findViewById(R.id.recyclerViewNotifications);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerViewNotifications.setLayoutManager(layoutManager);
        noNoteLayout = view.findViewById(R.id.noNoteLayout);
        adapter = new NotificationAdapter(getContext(), notifications);
        recyclerViewNotifications.setAdapter(adapter);
//        setOnScrollListener();
        view.findViewById(R.id.icon_arrow).setOnClickListener(v -> Objects.requireNonNull(getActivity()).onBackPressed());
//        getNotifications(currentPage);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentPage == 1) {
            notifications.clear();
        }
        getNotifications(currentPage);
    }

    private void getNotifications(Integer page) {

        isLoading = true;
        String token = AppPreferences.getUserToken();
        Call<Response.NotificationResponse> call = RestClient.getClient().getNotifications(token);
        call.enqueue(new CustomNetworkCallback<Response.NotificationResponse>() {
            @Override
            public void onSuccess(Response.NotificationResponse response) {
                if (response.data.size()>0) {
                    notifications.addAll(response.data);
                    noNoteLayout.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    if (notifications.size() == 0 ) {
                        recyclerViewNotifications.setVisibility(View.GONE);
                        noNoteLayout.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onError(Throwable throwable) {
                noNoteLayout.setVisibility(View.VISIBLE);
            }
        });
    }

//    private void setOnScrollListener() {
//        recyclerViewNotifications.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                int visibleItemCount = layoutManager.getChildCount();
//                int totalItemCount = layoutManager.getItemCount();
//                new LinearLayoutManager(getContext()).findFirstVisibleItemPosition();
//                int firstVisibleItemPosition = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();
//
//                if (!isLoading && !isLastPage()) {
//                    if ((visibleItemCount + firstVisibleItemPosition) >=
//                            totalItemCount && firstVisibleItemPosition >= 0) {
//                        loadMoreItems();
//                    }
//                }
//            }
//        });
//    }

    private boolean isLastPage() {
        return currentPage == lastPage;
    }

    private void loadMoreItems() {
        isLoading = true;
        currentPage = currentPage +1;
        getNotifications(currentPage);
    }

}
