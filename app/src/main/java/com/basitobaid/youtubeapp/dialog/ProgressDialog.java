package com.basitobaid.youtubeapp.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.basitobaid.youtubeapp.R;

public class ProgressDialog extends DialogFragment {
    static ProgressDialog dialog = null;


    public static ProgressDialog getInstance() {
//        if (dialog == null) {
//            dialog = new ProgressBarDialog();
//        }
        return new ProgressDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
//        setStyle(STYLE_NO_TITLE, 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_progress, container, false);
        ProgressBar progressBar = view.findViewById(R.id.progress_bar);

        return view;
    }
}
