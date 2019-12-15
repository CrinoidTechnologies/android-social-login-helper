package com.crinoid.socialloginhelper.instagram.basicdisplay;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class AccessTokenRefreshResponseData extends IGBDAccessTokenBaseResponseData {

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("expires_in")
    private String expiresIn; //The number of seconds until the long-lived token expires.

    public String getTokenType() {
        return tokenType;
    }

    private String getExpiresIn() {
        return expiresIn;
    }

    public long getExpiresInMillis() {
        if (TextUtils.isEmpty(expiresIn))
            return 0;
        return Integer.parseInt(expiresIn) * 1000;
    }
}
