package com.basitobaid.youtubeapp.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.basitobaid.youtubeapp.R;

public class ReferralCodeDialog extends DialogFragment {


    private EditText referralEt;
    private TextView congratsTv;
    private ReferralDialogCallback callback;


    public ReferralCodeDialog(ReferralDialogCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme_transparent);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_referral_code, container, false);
        referralEt = view.findViewById(R.id.referral_et);
        congratsTv= view.findViewById(R.id.top_text);
        congratsTv.setText("Please enter referral code to get cash");
        referralEt.setHint("Referral code..");
        view.findViewById(R.id.ok_btn).setOnClickListener(view1 -> {
            if (!referralEt.getText().toString().isEmpty()) {
                callback.onSubmit(referralEt.getText().toString());
                dismiss();
            }
        });
        view.findViewById(R.id.cancel_button).setOnClickListener(view1 -> {
            callback.onCancel();
            dismiss();
        });
        return view;
    }

    public interface ReferralDialogCallback {
        void onSubmit(String code);
        void onCancel();
    }
}
