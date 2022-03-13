package com.basitobaid.youtubeapp.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.basitobaid.youtubeapp.R;
import com.basitobaid.youtubeapp.utils.ImageUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class slider extends Fragment {
    View view;
    private Context context;
    private String imageUrl;

    private slider() {
        // Required empty public constructor
    }

    public static slider getInstance(String imageUrl) {
        slider slider = new slider();
        Bundle args = new Bundle();
        args.putString("imageUrl", imageUrl);
        slider.setArguments(args);
        return slider;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        if (args != null) {
            this.imageUrl = args.getString("imageUrl");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       view =   inflater.inflate(R.layout.fragment_slider, container, false);
        ImageView imageView = view.findViewById(R.id.image_view);
        ImageUtils.loadImageWithExternalUrl(context, imageView, imageUrl);
       return view;
    }

}
