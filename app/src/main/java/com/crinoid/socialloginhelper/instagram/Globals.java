package com.crinoid.socialloginhelper.instagram;

public class Globals {
    public static final String INSTAGRAM_REDIRECT_URL = "https://www.pesopie.com/";
    public static final String INSTAGRAM_BASE_URL = "https://api.instagram.com/";
    /**
     * @deprecated using INSTAGRAM_APP_ID instead
     */
    public static final String INSTAGRAM_CLIENT_ID = "XXXXX";

    //Basic display IG fields
    public static final String INSTAGRAM_APP_ID = "XXXXXX";
    public static final String INSTAGRAM_APP_SECRET = "XXXXX";
    public static final String INSTAGRAM_USER_FIELDS = "id,username,account_type";
    public static final String IGBD_GRANT_TYPE_EXCHANGE = "ig_exchange_token";
    public static final String IGBD_GRANT_TYPE_REFRESH = "ig_refresh_token";
    public static final String INSTAGRAM_LOGIN_SCOPES = "user_profile,user_media";
}
