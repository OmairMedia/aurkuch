package com.basitobaid.youtubeapp.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class SocialMediaIntents {
    public static Intent getOpenFacebookIntent(Context context, String uri) {

        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href="+uri));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"+uri));
        }
    }

    public static Intent getOpenTwitterIntent(Context context, String username, String userId) {
        Intent intent;
        try{
            // Get Twitter app
            context.getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id="+ userId));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // If no Twitter app found, open on browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/"+ username));
        }
        return intent;
    }

    public static Intent getOpenInstagramIntent(Context context, String uri) {

        try {
//            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/"+uri));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/"+uri));
        }
    }
}
