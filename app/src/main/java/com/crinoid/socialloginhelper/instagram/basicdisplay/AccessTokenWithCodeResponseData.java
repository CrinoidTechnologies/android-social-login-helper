package com.crinoid.socialloginhelper.instagram.basicdisplay;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AccessTokenWithCodeResponseData implements Serializable {

    @SerializedName("user_id")
    private String userId;
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("error_type")
    private String errorType;
    @SerializedName("code")
    private int code;
    @SerializedName("error_message")
    private String errorMessage;

    public String getErrorType() {
        return errorType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public int getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getUserId() {
        return userId;
    }
}
