package com.basitobaid.youtubeapp.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.basitobaid.youtubeapp.R;

public class CongratulationDialog extends DialogFragment {


    private TextView creditText;
    private CongratulationDialog() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme_transparent);
    }

    public static CongratulationDialog newInstance(String points) {
        CongratulationDialog dialog = new CongratulationDialog();
        Bundle args = new Bundle();
        args.putString("points", points);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.congrats_dialog, container, false);
        creditText = view.findViewById(R.id.credit_text);
        creditText.setText(String.format("You got Rs. %s", getArguments().getString("points")));
        view.findViewById(R.id.ok_btn).setOnClickListener(view1 -> dismiss());
        return view;
    }
}
