package com.basitobaid.youtubeapp.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.basitobaid.youtubeapp.R;
import com.basitobaid.youtubeapp.activity.PrivacyPolicyActivity;
import com.basitobaid.youtubeapp.utils.AppPreferences;
import com.basitobaid.youtubeapp.utils.Constant;
import com.basitobaid.youtubeapp.utils.SocialMediaIntents;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {


    private Context context;
    private FragmentActivity activity;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getContext();
        activity = getActivity();
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.notification_tv, R.id.contact_tv, R.id.privacy_tv, R.id.tac_tv, R.id.aboutus_tv, R.id.share_tv,
            R.id.btn_contact_us, R.id.fb_tv, R.id.twitter_tv, R.id.insta_tv})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.notification_tv:
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new NotificationFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.contact_tv:
            case R.id.btn_contact_us:
                activity.startActivity(new Intent(context, ChatActivity.class));
                break;
            case R.id.privacy_tv:
                intent = new Intent(context, PrivacyPolicyActivity.class);
                intent.putExtra(Constant.FROM, Constant.PRIVACY);
                context.startActivity(intent);
                break;
            case R.id.tac_tv:
                intent = new Intent(context, PrivacyPolicyActivity.class);
                intent.putExtra(Constant.FROM, Constant.TAC);
                context.startActivity(intent);
                break;
            case R.id.aboutus_tv:
                intent = new Intent(context, PrivacyPolicyActivity.class);
                intent.putExtra(Constant.FROM, Constant.ABOUT_US);
                context.startActivity(intent);
                break;
            case R.id.share_tv:
                shareApp();
                break;
            case R.id.fb_tv:
                if (AppPreferences.getFacebookLink() != null) {
                    startActivity(SocialMediaIntents.getOpenFacebookIntent(context, AppPreferences.getFacebookLink()));
                }
//                startActivity(SocialMediaIntents.getOpenFacebookIntent(context, FB_PAGE_LINK));
                break;
            case R.id.twitter_tv:
                if (AppPreferences.getTwitterUserid() != null && AppPreferences.getTwitterUsername() != null) {
                    startActivity(SocialMediaIntents.getOpenTwitterIntent(context, AppPreferences.getTwitterUsername()
                            , AppPreferences.getTwitterUserid()));
                }
//                startActivity(SocialMediaIntents.getOpenTwitterIntent(context, TWITTER_NAME
//                        , TWITTER_ID));
                break;
            case R.id.insta_tv:
                if (AppPreferences.getInstaLink() != null) {
                    startActivity(SocialMediaIntents.getOpenInstagramIntent(context, AppPreferences.getInstaLink()));
                }
//                startActivity(SocialMediaIntents.getOpenInstagramIntent(context, INSTARAM_NAME));
                break;
        }
    }
    public static final String TWITTER_NAME= "hamzashafqaat";
    public static final String INSTARAM_NAME= "eventsia.pk";
    public static final String TWITTER_ID = "1061150305738481670";
    public static final String FB_PAGE_LINK = "junaid.akram/?__tn__=kC-R&eid=ARA2Y3hmatpjk4Fxijtg_KlWLD4l8q-MSebBVFnn6RnT86UmAA8mXugm8g44ySnzY0oiz1wvkvJws_Ad&hc_ref=ART-MNhNpBS7zRs6wqE4ewGb3KGSQiWMIbqg050Kx78hrL1_cXRJDM-hFYJe0OA-RPw&fref=nf&__xts__[0]=68.ARD6QGPsFbSgFt6d4N5Lqi25aj7TM-aWkLM2dHnBLqn4PKxO9oP_JhP40sa-l_vZsJfW1wMn-C-GYiLWhGqJpPuOyisfVykR-TeWW27XrdBJlbMMuCAezn4EA-eWMqjT-N33hY1FJBEYZaJyNLyaKemG0AwKmPyiSrecgojENQQD5-98YMcFmV0viBD36vVk5PQ2ruOfwWFa7UFr5k4KqYDYvJ-1Ihv886Lj4wixzPJF72odnSgCdAiwNMK_YDLvYB7omBrpK9Kas2rrflCYXaIA94YIHjgvwQYzgGTFlG5FS7FL8xP_MiPntwvnY68gv9FiH7idSwwoibUaehzgHbH12hntxTliqkERKnw__oqWNZiSvWjDeEo0gkHIwTUg";


    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_string) + AppPreferences.getUserReferalCode());
        context.startActivity(shareIntent);
    }

}
