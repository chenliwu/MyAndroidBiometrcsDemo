package com.chenlw.android.biometrics.demo;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chenlw.android.biometrics.demo.biometrics.FingerprintDialogFragment;
import com.chenlw.android.biometrics.demo.biometrics.IBiometricsAuthenticationCallback;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class LoginActivity extends AppCompatActivity implements IBiometricsAuthenticationCallback, View.OnClickListener {

    private static final String DEFAULT_KEY_NAME = "default_key";

    KeyStore keyStore;
    /**
     * 测试生物识别接口
     */
    private Button mBtnTestBiometrics;
    /**
     * 进入测试生物识别接口的activity
     */
    private Button mBtnToTestActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        if (supportFingerprint()) {
            initKey();
            initCipher();
        }
    }

    public boolean supportFingerprint() {
        boolean result = true;
        String msg = null;
        if (Build.VERSION.SDK_INT < 23) {
            msg = "您的系统版本过低，不支持指纹功能";
            Toast.makeText(this, "您的系统版本过低，不支持指纹功能", Toast.LENGTH_SHORT).show();
            result = false;
        } else {
            try{
                KeyguardManager keyguardManager = getSystemService(KeyguardManager.class);
//                FingerprintManager fingerprintManager = getSystemService(FingerprintManager.class);
                final FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
                if (fingerprintManager != null) {
                    if (!fingerprintManager.isHardwareDetected()) {
                        msg = "您的手机不支持指纹功能";
                        result = false;
                    } else if (!keyguardManager.isKeyguardSecure()) {
                        msg = "您还未设置锁屏，请先设置锁屏并添加一个指纹";
                        result = false;
                    } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                        msg = "您至少需要在系统设置中添加一个指纹";
                        result = false;
                    }
                }
            }catch (Exception e){
                result = false;
                msg = "异常："+e.getMessage();
            }

        }
        if (!result) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    @TargetApi(23)
    private void initKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(DEFAULT_KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);
            keyGenerator.init(builder.build());
            keyGenerator.generateKey();
        } catch (Exception e) {
            Toast.makeText(this, "错误："+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @TargetApi(23)
    private void initCipher() {
        try {
            SecretKey key = (SecretKey) keyStore.getKey(DEFAULT_KEY_NAME, null);
            Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            showFingerPrintDialog(cipher);
        } catch (Exception e) {
            Toast.makeText(this, "错误："+e.getMessage(), Toast.LENGTH_LONG).show();
            // throw new RuntimeException(e);
        }
    }

    private void showFingerPrintDialog(Cipher cipher) {
        FingerprintDialogFragment fragment = new FingerprintDialogFragment();
        fragment.setCipher(cipher);
        fragment.setIBiometricsAuthentication(this);
        fragment.show(getFragmentManager(), "fingerprint");
    }

    @Override
    public void onAuthenticationSuccess() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAuthenticationFailure() {

    }

    private void initView() {
        mBtnTestBiometrics = (Button) findViewById(R.id.btn_test_biometrics);
        mBtnTestBiometrics.setOnClickListener(this);
        mBtnToTestActivity = (Button) findViewById(R.id.btn_to_test_activity);
        mBtnToTestActivity.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test_biometrics:
                testBiometrics();
                break;
            case R.id.btn_to_test_activity:
                Intent intent = new Intent(this, BiometricPromptActivity.class);
                startActivity(intent);
                break;
            default:
                break;

        }
    }

    public void testBiometrics() {
        if (supportFingerprint()) {
            initKey();
            initCipher();
        }
    }


}
