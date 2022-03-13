package com.basitobaid.youtubeapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.basitobaid.youtubeapp.R;
import com.basitobaid.youtubeapp.network.CustomNetworkCallback;
import com.basitobaid.youtubeapp.network.Response;
import com.basitobaid.youtubeapp.network.RestClient;
import com.basitobaid.youtubeapp.utils.AppPreferences;
import com.basitobaid.youtubeapp.utils.Constant;
import com.basitobaid.youtubeapp.utils.DialogUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

public class OTPActivity extends AppCompatActivity {

    private static final String TAG = OTPActivity.class.getSimpleName();
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    @BindView(R.id.text_view)
    TextView textView;
    @BindView(R.id.otp_et)
    EditText otpEt;
    private String mVerificationId;
    FirebaseAuth mAuth;
    String mobileNo, from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        ButterKnife.bind(this);
        textView.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        if (getIntent().getStringExtra(Constant.FROM) != null) {
            from = getIntent().getStringExtra(Constant.FROM);
        }
        if (from != null && from.equals("regs")) {
            mobileNo = getIntent().getStringExtra(Constant.MOBILE_NO);
        } else {
            mobileNo = AppPreferences.getUserPhoneNo();
        }
        setupCallback();
        authorizePhone();
    }

    private void authorizePhone() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobileNo,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private void setupCallback() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
//                if ( !from.equals("regs")) {
//                    verifyPhone();
//                } else {
//                    returnToPrevActivity();
//                }
//                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                Intent intent = new Intent();
                intent.setData(Uri.parse("failed"));
                setResult(1, intent);
                finish();
//                if (e instanceof FirebaseAuthInvalidCredentialsException) {
//                    // Invalid request
//
//                    // ...
//                } else if (e instanceof FirebaseTooManyRequestsException) {
//                    // The SMS quota for the project has been exceeded
//                    // ...
//                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);
                textView.setVisibility(View.VISIBLE);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
//                mResendToken = token;

                // ...
            }
        };
    }

    private void returnToPrevActivity() {
        Intent intent = new Intent();
        intent.setData(Uri.parse("verified"));
        setResult(1, intent);
        finish();
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            if ( !from.equals("regs")) {
                                verifyPhone();
                            } else {
                                returnToPrevActivity();
                            }
                            // [START_EXCLUDE]
                            // [END_EXCLUDE]
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                otpEt.setError("Invalid code.");
                                // [END_EXCLUDE]
                            }

                        }
                    }
                });
    }

    private void verifyPhone() {
        Call<Response.CommonResponse> call = RestClient.getClient().verifyPhoneNo(AppPreferences.getUserToken());
        call.enqueue(new CustomNetworkCallback<Response.CommonResponse>() {
            @Override
            public void onSuccess(Response.CommonResponse response) {
                AppPreferences.setUserPhoneVerified(true);
                returnToPrevActivity();
            }

            @Override
            public void onError(Throwable throwable) {
                DialogUtils.showErrorDialog(OTPActivity.this, throwable.getMessage());
            }
        });
    }

    @OnClick(R.id.submit_btn)
    public void onViewClicked() {
        if (TextUtils.isEmpty(otpEt.getText())) {
            otpEt.setError("can not be empty");
            return;
        }
        verifyPhoneNumberWithCode( mVerificationId, otpEt.getText().toString());

    }
}
