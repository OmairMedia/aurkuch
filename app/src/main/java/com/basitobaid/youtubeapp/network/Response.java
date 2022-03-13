package com.basitobaid.youtubeapp.network;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Response {

    public class CommonResponse {
        @SerializedName("status")
        public String status;
        @SerializedName("message")
        public String message;
        @SerializedName("token")
        public String token;
    }

    public class LoginResponse extends CommonResponse {
        @SerializedName("data")
        private Model.User data;
        public Model.User getData() {
            return data;
        }
    }

    public class SignUpResponse extends CommonResponse {
        public Model.User data;
    }

    public class CategoryResponse extends CommonResponse {
        @SerializedName("data")
        public List<Model.Category> data;
    }

    public class RewardResponse extends CommonResponse {
        @SerializedName("data")
        public List<Model.Reward> data;
    }

    public class WalletResponse extends CommonResponse {
        @SerializedName("data")
        public List<Model.Wallet> data;
    }

    public class ProfileResponse extends CommonResponse {
        @SerializedName("data")
        public List<Model.Profile> data;
    }

    public class RedeemResponse extends CommonResponse {
        @SerializedName("data")
        public Model.Transaction data;
    }

    public class TransactionResponse extends CommonResponse {
        @SerializedName("data")
        public List<Model.Transaction> data;
    }

    public class ChatResponse extends CommonResponse {
        @SerializedName("data")
        public List<Model.Chat> data;
    }

    public class NotificationResponse extends CommonResponse {
        @SerializedName("data")
        public List<Model.Notification> data;
    }

    public class CategoryListResponse {
        @SerializedName("data")
        public List<Model.Category> data;
    }

    public class BannerImagesResponse extends CommonResponse {
        @SerializedName("data")
        public List<Model.BannerImage> data;
    }



    public class SettingResponse extends CommonResponse {
        @SerializedName("data")
        public Model.Setting data;

    }

    private class ListResponse {
        @SerializedName("current_page")
        public Integer currentPage;
        @SerializedName("from")
        public Integer fromPage;
        @SerializedName("last_page")
        public Integer lastPage;
        @SerializedName("total")
        public Integer total;
    }
 }
