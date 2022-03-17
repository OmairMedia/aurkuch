package com.basitobaid.youtubeapp.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.basitobaid.youtubeapp.R;
import com.basitobaid.youtubeapp.adapter.OnboardingAdapter;
import com.basitobaid.youtubeapp.fragment.OnboardingItem;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {
   private OnboardingAdapter onboardingAdapter;

   protected void onCreate(Bundle savedInstanceData) {
       super.onCreate(savedInstanceData);
       setContentView(R.layout.activity_onboarding);
       setupOnboardingItems();

       ViewPager2 onboardingViewPager = findViewById(R.id.onboarding_pager);
       onboardingViewPager.setAdapter(onboardingAdapter);
   }

   private void setupOnboardingItems() {
       List<OnboardingItem> onboardingItems = new ArrayList<>();

       OnboardingItem itempayonline = new OnboardingItem();
       itempayonline.setTitle("Pay Your Bills Online");
       itempayonline.setDescription("Electric Bill Payment Made Easy");
       itempayonline.setImage(R.drawable.onboarding_1);

       OnboardingItem makeEasyMoney = new OnboardingItem();
       itempayonline.setTitle("Make Money From Home");
       itempayonline.setDescription("Making Money Made Easy With Aur Kuch App , Just Watch Videos !");
       itempayonline.setImage(R.drawable.onboarding_2);

       OnboardingItem reliablePartner = new OnboardingItem();
       itempayonline.setTitle("Your Reliable Partner");
       itempayonline.setDescription("Unlike Others Aur Kuch App Is Your Reliable Partner!");
       itempayonline.setImage(R.drawable.onboarding_3);

       onboardingItems.add(itempayonline);
       onboardingItems.add(makeEasyMoney);
       onboardingItems.add(reliablePartner);

       onboardingAdapter = new OnboardingAdapter(onboardingItems);
   }
}
