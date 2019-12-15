package com.crinoid.utils
import com.crinoid.socialloginhelper.instagram.basicdisplay.AccessTokenRefreshResponseData
import com.crinoid.socialloginhelper.instagram.basicdisplay.InstagramBDUserData

class LocalData {
    var instaUserBd: InstagramBDUserData? = null
    private var iGBDAccessToken: AccessTokenRefreshResponseData? = null
    var AccessToken: AccessTokenRefreshResponseData?
        set(value) {
            if (value != null) {
                iGBDTokenRefreshTime = System.currentTimeMillis()
                iGBDAccessToken = value
            }
        }
        get() {
            return iGBDAccessToken
        }

    var instaAcessCode: String? = null
    var iGBDTokenRefreshTime: Long = 0

}