package com.crinoid.socialloginhelper.instagram.basicdisplay;

import com.google.gson.annotations.SerializedName;

public class IGBDAccessTokenBaseResponseData extends IGBDBaseResponseData {

    @SerializedName("access_token")
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }
}
