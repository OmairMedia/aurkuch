package com.basitobaid.youtubeapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.basitobaid.youtubeapp.R;
import com.basitobaid.youtubeapp.dialog.ProgressDialog;
import com.basitobaid.youtubeapp.network.CustomNetworkCallback;
import com.basitobaid.youtubeapp.network.Model;
import com.basitobaid.youtubeapp.network.Response;
import com.basitobaid.youtubeapp.network.RestClient;
import com.basitobaid.youtubeapp.utils.AppPreferences;
import com.basitobaid.youtubeapp.utils.ConnectionManager;
import com.basitobaid.youtubeapp.utils.Constant;
import com.basitobaid.youtubeapp.utils.DialogUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.textfield.TextInputEditText;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;

public class RegistrationActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    TelephonyManager telephonyManager;
    private String imei;
    private TextInputEditText nameEt, mobileEt, emailEt;
    private String email;
    private Context context;
    private Activity activity;
    private ProgressDialog progressDialog;
    GoogleAccountCredential mCredential;
    private static final String[] SCOPES = { YouTubeScopes.YOUTUBE_READONLY };
    private String userToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        context = RegistrationActivity.this;
        activity = RegistrationActivity.this;
        email = getIntent().getStringExtra(Constant.EMAIL);
        nameEt = findViewById(R.id.name_et);
        emailEt = findViewById(R.id.email_et);
        mobileEt = findViewById(R.id.mobile_et);

        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        new Handler().postDelayed(this::initializeAccountCredentials, 1000);
        findViewById(R.id.submit_btn).setOnClickListener(view -> {
            if (null != imei) {
                registerUser();
            } else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initializeAccountCredentials() {
        // Initialize credentials and service object.
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!ConnectionManager.isDeviceOnline(context)) {
            Toast.makeText(context, getString(R.string.no_connection_string), Toast.LENGTH_SHORT).show();
        } else {
            startMainActivity();
        }
    }

    @AfterPermissionGranted(Constant.REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = AppPreferences.getUserAccountname();
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                email = accountName;
                emailEt.setText(email);
                getDeviceIme();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        Constant.REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    Constant.REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        switch(requestCode) {
            case Constant.REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(context, getString(R.string.google_play_services_string), Toast.LENGTH_SHORT).show();
                } else {
                    initializeAccountCredentials();
                }
                break;
            case Constant.REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        email = accountName;
                        emailEt.setText(email);
                        getDeviceIme();
                    }
                }
                break;
            case Constant.REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    startMainActivity();
                }
                break;
            case  Constant.OTP_ACTIVITY_REQUEST:
                if (data.getData() != null) {
                    Uri uri = data.getData();
                    Log.d("TAG", "onActivityResult: "+ uri);
                    if (uri.toString().equals("verified")) {
                        callRegisteration();
                    } else {
                        DialogUtils.showErrorDialog(context, "Your phone number is invalid. Please provide a valid number");

                    }
                }
                break;
        }
    }

    private void verifyPhone() {
        Call<Response.CommonResponse> call = RestClient.getClient().verifyPhoneNo(AppPreferences.getUserToken());
        call.enqueue(new CustomNetworkCallback<Response.CommonResponse>() {
            @Override
            public void onSuccess(Response.CommonResponse response) {
                AppPreferences.setUserPhoneVerified(true);
                startMainActivity();
            }

            @Override
            public void onError(Throwable throwable) {
                DialogUtils.showErrorDialog(RegistrationActivity.this, throwable.getMessage());
            }
        });
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                activity,
                connectionStatusCode,
                Constant.REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private void registerUser() {
        if (isEmpty(nameEt) || isEmpty(mobileEt) || mobileEt.getText().toString().length() < 10) {
            if (isEmpty(nameEt)) {
                setError(nameEt);
            }
            if (isEmpty(mobileEt)) {
                setError(mobileEt);
            } else if (getEtString(mobileEt).length() < 10) {
                setMobileError(mobileEt);
            }
        } else {
            if (ConnectionManager.isDeviceOnline(RegistrationActivity.this)) {
                callRegisteration();
//                Log.d("TAG", "registerUser: calling OTP");
//                Intent intent = new Intent(context, OTPActivity.class);
//                intent.putExtra(Constant.FROM, "regs");
//                intent.putExtra(Constant.MOBILE_NO, "+92" + getEtString(mobileEt));
//                startActivityForResult(intent, Constant.OTP_ACTIVITY_REQUEST);

            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void callRegisteration() {
        showProgressDialog();
        Call<Response.LoginResponse> call = RestClient.getClient().registerUser(email,
                getEtString(nameEt), getEtString(mobileEt), imei );
        call.enqueue(new Callback<Response.LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<Response.LoginResponse> call,
                                   @NonNull retrofit2.Response<Response.LoginResponse> response) {

                hideProgressDialog();
                if (response.body() != null && response.body().status.equals("200")) {
                    userToken = response.body().token;
                    saveDataInPrefs(response.body().getData());
                } else {
                    Toast.makeText(RegistrationActivity.this, "error occurred", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<Response.LoginResponse> call, @NonNull Throwable t) {
                hideProgressDialog();
                Toast.makeText(RegistrationActivity.this, "error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgressDialog() {
        progressDialog = ProgressDialog.getInstance();
        progressDialog.show(getSupportFragmentManager(), "pDialog");
        progressDialog.setCancelable(false);
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isVisible()) {
            progressDialog.dismiss();
        }
    }

    private void saveDataInPrefs(Model.User user) {
        AppPreferences.setUserAccountName(user.uEmail);
        AppPreferences.setUserAccountUserName(user.uName);
        AppPreferences.setUserPhoneNo(user.uPhoneNo);
        AppPreferences.setUserStatus(user.uStatus);
        AppPreferences.setUserPhoneIMEI(user.uPhoneImei);
        AppPreferences.setUserPhoneVerified(!(user.uPhoneVerified.isEmpty() || user.uPhoneVerified.equals("0")) );
        AppPreferences.setUserEmailVerified(!(user.uEmailVerified.equals("0") || user.uEmailVerified.isEmpty()));
        AppPreferences.setUserReferalCode(user.uReferalCode);
        AppPreferences.setUserId(user.uId);
        Log.d("TAG", "saveDataInPrefs: " + userToken);
        AppPreferences.setUserToken(userToken);
        startMainActivity();
//        verifyPhone();
    }

    private void startMainActivity() {
        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
        finish();
    }

    private boolean isEmpty(TextInputEditText editText) {
        return Objects.requireNonNull(editText.getText()).toString().isEmpty();
    }

    private void setError(TextInputEditText editText) {
        editText.setError(getString(R.string.required));
    }

    private void setMobileError(TextInputEditText editText) {
        editText.setError("Invalid mobile no.");
    }


    private String getEtString(TextInputEditText editText) {
        return Objects.requireNonNull(editText.getText()).toString();
    }

    private boolean hasPhoneStatePermission() {
        return EasyPermissions.hasPermissions(RegistrationActivity.this, Manifest.permission.READ_PHONE_STATE);
    }

    @AfterPermissionGranted(Constant.READ_PHONE_STATE_REQUEST)
    private void getDeviceIme() {
        if (hasPhoneStatePermission()) {
            imei = getDeviceImei();
        } else {
            EasyPermissions.requestPermissions(RegistrationActivity.this,
                    "Phone state request",
                    Constant.READ_PHONE_STATE_REQUEST,
                    Manifest.permission.READ_PHONE_STATE);
        }
    }

    @SuppressLint("MissingPermission")
    private String getDeviceImei() {
        return Build.VERSION_CODES.O <= Build.VERSION.SDK_INT ? telephonyManager.getImei() : telephonyManager.getDeviceId();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, RegistrationActivity.this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}
