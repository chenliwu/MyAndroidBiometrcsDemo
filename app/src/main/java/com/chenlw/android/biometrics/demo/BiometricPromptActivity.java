package com.chenlw.android.biometrics.demo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 如果Android版本小于Android P，APP运行会报错。
 */
public class BiometricPromptActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "chenlw";

    private BiometricPrompt mBiometricPrompt;
    private CancellationSignal mCancellationSignal;
    private BiometricPrompt.AuthenticationCallback mAuthenticationCallback;
    /**
     * 测试生物识别接口
     */
    private Button mBtnTestBiometrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometric_prompt);
        initView();

        initBiometrics();
    }

    private void initView() {
        mBtnTestBiometrics = (Button) findViewById(R.id.btn_test_biometrics);
        mBtnTestBiometrics.setOnClickListener(this);
    }

    @TargetApi(Build.VERSION_CODES.P)
    private void initBiometrics() {
        final Activity activity = BiometricPromptActivity.this;
        mBiometricPrompt = new BiometricPrompt.Builder(this)
                .setTitle("指纹验证")
                .setDescription("描述")
                .setNegativeButton("取消", getMainExecutor(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i(TAG, "Cancel button clicked");
                    }
                })
                .build();

        mCancellationSignal = new CancellationSignal();
        mCancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                //handle cancel result
                Log.i(TAG, "Canceled");
            }
        });

        mAuthenticationCallback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(activity, "onAuthenticationError:" + errString, Toast.LENGTH_LONG).show();
                Log.i(TAG, "onAuthenticationError " + errString);
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(activity, result.toString(), Toast.LENGTH_LONG).show();
                Log.i(TAG, "onAuthenticationSucceeded " + result.toString());
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(activity, "onAuthenticationFailed", Toast.LENGTH_LONG).show();
                Log.i(TAG, "onAuthenticationFailed ");
            }
        };
    }

    @TargetApi(Build.VERSION_CODES.P)
    private void authenticate() {
        mBiometricPrompt.authenticate(mCancellationSignal, getMainExecutor(), mAuthenticationCallback);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test_biometrics:
                authenticate();
                break;
            default:
                break;
        }
    }
}
