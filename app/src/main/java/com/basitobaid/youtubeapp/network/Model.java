package com.basitobaid.youtubeapp.network;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Model {

    public Model() {
    }

    //TODO signup model
    public static class SignUpCallModel {
        private String email, name, mobile, imei;

        public SignUpCallModel(String email, String name, String mobile, String imei) {
            this.email = email;
            this.name = name;
            this.mobile = mobile;
            this.imei = imei;
        }
    }


    public class User {
        @SerializedName("user_id")
        public String uId;

        @SerializedName("user_name")
        public String uName;

        @SerializedName("user_email")
        public String uEmail;

        @SerializedName("user_status")
        public String uStatus;

        @SerializedName("user_password")
        public String uPassword;

        @SerializedName("user_phone")
        public String uPhoneNo;

        @SerializedName("user_is_phone_verified")
        public String uPhoneVerified;

        @SerializedName("user_is_email_verified")
        public String uEmailVerified;

        @SerializedName("user_refferal_code")
        public String uReferalCode;

        @SerializedName("user_phone_iemi")
        public String uPhoneImei;

    }

    public class Category {
        @SerializedName("category_id")
        public String cId;

        @SerializedName("category_name")
        public String cName;

        @SerializedName("image")
        public String cImage;

        @SerializedName("category_date")
        public String cDate;

        @SerializedName("reward_count")
        public String cRewardCount;
    }

    public static class Reward implements Parcelable {
        @SerializedName("reward_id")
        public String rId;

        @SerializedName("reward_category_id")
        public String rCatId;

        @SerializedName("reward_name")
        public String cName;

        @SerializedName("image")
        public String rImage;

        @SerializedName("reward")
        public String rewardAmount;

        @SerializedName("reward_time")
        public String rTime;

        @SerializedName("reward_numbers")
        public String rNumber;

        @SerializedName("reward_description")
        public String rDescription;

        @SerializedName("reward_qr")
        public String rLink;

        @SerializedName("reward_status")
        public String rStatus;

        @SerializedName("rewards_left")
        public String rLeft;

        protected Reward(Parcel in) {
            rId = in.readString();
            rCatId = in.readString();
            cName = in.readString();
            rImage = in.readString();
            rewardAmount = in.readString();
            rTime = in.readString();
            rNumber = in.readString();
            rDescription = in.readString();
            rLink = in.readString();
            rStatus = in.readString();
            rLeft = in.readString();
        }

        public static final Creator<Reward> CREATOR = new Creator<Reward>() {
            @Override
            public Reward createFromParcel(Parcel in) {
                return new Reward(in);
            }

            @Override
            public Reward[] newArray(int size) {
                return new Reward[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(rId);
            parcel.writeString(rCatId);
            parcel.writeString(cName);
            parcel.writeString(rImage);
            parcel.writeString(rewardAmount);
            parcel.writeString(rTime);
            parcel.writeString(rNumber);
            parcel.writeString(rDescription);
            parcel.writeString(rLink);
            parcel.writeString(rStatus);
            parcel.writeString(rLeft);
        }
    }

    public class Wallet {
        @SerializedName("wallet_id")
        public String wId;

        @SerializedName("wallet_user_id")
        public String wUserId;

        @SerializedName("wallet_reward_id")
        public String wRewardId;

        @SerializedName("wallet_total_amount")
        public String wAmount;

        @SerializedName("wallet_reward_date")
        public String wRewardDate;

        @SerializedName("wallet_reward_status")
        public String wRewardStatus;

        @SerializedName("reward_id")
        public String rId;

        @SerializedName("reward_category_id")
        public String rCatId;

        @SerializedName("reward_name")
        public String cName;

        @SerializedName("image")
        public String rImage;

        @SerializedName("reward")
        public String rewardAmount;

        @SerializedName("reward_time")
        public String rTime;

        @SerializedName("reward_numbers")
        public String rNumber;

        @SerializedName("reward_description")
        public String rDescription;

        @SerializedName("reward_qr")
        public String rLink;

        @SerializedName("reward_status")
        public String rStatus;
    }


    public class Profile {
        @SerializedName("pending")
        public String pendingAmount;

        @SerializedName("approved")
        public String approvedAmount;

        @SerializedName("paid")
        public String paidAmount;

        @SerializedName("total")
        public String totalAmount;
    }

    public class Transaction {
        @SerializedName("transaction_id")
        public String tId;

        @SerializedName("transaction_user_id")
        public String userId;

        @SerializedName("transaction_wallet_amount")
        public String tAmount;

        @SerializedName("transaction_status")
        public String tStatus;

        @SerializedName("transaction_created_date")
        public String tCreatedDate;

        @SerializedName("transaction_delivered_date")
        public String tDeliveredDate;
    }

    public static class Chat {
        public static final String  MSG_TYPE_RECEIVED = "received";
        public static final String  MSG_TYPE_SENT = "sent";
        @SerializedName("message_id")
        public String messageId;
        @SerializedName("chat_id")
        public String chatId;
        @SerializedName("sender_id")
        public String senderId;
        @SerializedName("message_text")
        public String messageText;
        @SerializedName("message_datetime")
        public String messageDate;
        @SerializedName("message_status")
        public String messageStatus;
        public String messageType;
    }

    public static class Notification {
        @SerializedName("notification_id")
        public String id;
        @SerializedName("notification_user_id")
        public String nUserId;
        @SerializedName("notification_message")
        public String desc;
        @SerializedName("notification_title")
        public String title;
        @SerializedName("notification_status")
        public String nStatus;
        @SerializedName("notification_dattime")
        public String nDate;
        @SerializedName("type")
        public String type;
    }

    public class BannerImage {
        @SerializedName("slider_id")
        public String id;
        @SerializedName("image")
        public String bannerUrl;
    }



    public class Setting {
        @SerializedName("facebook")//facebook pagelink
        public String facebookLink;
        @SerializedName("twitter_username")//twitter username
        public String twitterUsername;
        @SerializedName("twitter_userid")//twitter userId
        public String twitterId;
        @SerializedName("instagram")//instagram accountname
        public String instagramLink;
        @SerializedName("term_condition")
        public String termAndConditions;
        @SerializedName("privacy_policy")
        public String privacyPolicy;
        @SerializedName("about_us")
        public String aboutUs;
        @SerializedName("youtube")
        public String youtube;
    }
}
