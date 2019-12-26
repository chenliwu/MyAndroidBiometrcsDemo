package com.chenlw.android.biometrics.demo.biometrics;

/**
 * 生物识别回调接口
 *
 * @author chenlw
 * @date 2019/12/26
 */
public interface IBiometricsAuthentication {

    void onAuthenticationSuccess();

    void onAuthenticationFailure();

}
