package com.basitobaid.youtubeapp.fragment;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.basitobaid.youtubeapp.R;
import com.basitobaid.youtubeapp.utils.AppPreferences;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class InviteFriendsFragment extends Fragment {


    Context context;
    FragmentActivity activity;
    @BindView(R.id.referral_code)
    TextView referralCode;
    private ShareDialog shareDialog;

    private InviteFriendsFragment() {
        // Required empty public constructor
    }

    public static InviteFriendsFragment newInstance() {
        return new InviteFriendsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        activity = getActivity();
        initFBsdk();

    }

    private void initFBsdk() {
        FacebookSdk.sdkInitialize(activity.getApplicationContext());

        shareDialog = new ShareDialog(this);

//        CallbackManager callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite_friends, container, false);
        ButterKnife.bind(this, view);
        referralCode.setText(AppPreferences.getUserReferalCode());
        return view;
    }

    private void shareOnWhatsapp() {
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_string) + AppPreferences.getUserReferalCode());
        whatsappIntent.setType("text/plain");

        try {
            startActivity(whatsappIntent);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
        }
    }

    // Display App Share Dialog
    public void ShareonFacebook( final ShareDialog shareDialog) {

            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(context.getString(R.string.app_link)))
                    .setQuote(context.getString(R.string.quote_with_link) + AppPreferences.getUserReferalCode())
                    .build();

            if (ShareDialog.canShow(ShareLinkContent.class)) {
                shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
            }

    }

    public void sendOnLine() {
        String sendText = getString(R.string.share_string) + AppPreferences.getUserReferalCode();
        if(checkLineInstalled()){
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setClassName("jp.naver.line.android", "jp.naver.line.android.activity.selectchat.SelectChatActivity");
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, sendText);
            startActivity(intent);
        }else{
            Toast toast = Toast.makeText(context, "Line is not installed", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    private boolean checkLineInstalled(){
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> m_appList = pm.getInstalledApplications(0);
        boolean lineInstallFlag = false;
        for (ApplicationInfo ai : m_appList) {
            if(ai.packageName.equals("jp.naver.line.android")){
                lineInstallFlag = true;
                break;
            }
        }
        return lineInstallFlag;
    }

    private void shareRssMessage() {

        String sendText = getString(R.string.share_string) + AppPreferences.getUserReferalCode();
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:"));
        sendIntent.putExtra("sms_body",sendText);
        startActivity(sendIntent);
    }


    @OnClick({R.id.share_fb, R.id.share_whatsapp, R.id.share_line, R.id.share_general})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.share_fb:
                ShareonFacebook(shareDialog);
                break;
            case R.id.share_whatsapp:
                shareOnWhatsapp();
                break;
            case R.id.share_line:
                shareRssMessage();
                break;
            case R.id.share_general:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_string) + AppPreferences.getUserReferalCode());
                context.startActivity(shareIntent);
                break;
        }
    }
}
