package com.crinoid.retrofit

import com.crinoid.socialloginhelper.instagram.basicdisplay.AccessTokenRefreshResponseData
import com.crinoid.socialloginhelper.instagram.basicdisplay.AccessTokenWithCodeResponseData
import com.crinoid.socialloginhelper.instagram.basicdisplay.BDInstagramHelper
import com.crinoid.socialloginhelper.instagram.basicdisplay.InstagramBDUserData
import retrofit2.Call
import retrofit2.http.*

interface MyRetrofitApiInterface {

    //Instagram Basic Display APIs
    @GET(BDInstagramHelper.SELF_USER_API_END_POINT)
    fun getSelfBDInstagramProfile(@Query("access_token") accessToken: String, @Query("fields") fields: String)
            : Call<InstagramBDUserData>

    @GET(BDInstagramHelper.REFRESH_ACCESS_TOKEN_API_END_POINT)
    fun refreshIGBDAccessToken(
        @Query("access_token") accessToken: String, @Query("client_secret") clientsecret: String, @Query(
            "grant_type"
        ) grantType: String
    )
            : Call<AccessTokenRefreshResponseData>

    @GET(BDInstagramHelper.EXCHANGE_ACCESS_TOKEN_API_END_POINT)
    fun exchangeIGBDAccessToken(
        @Query("access_token") accessToken: String, @Query("client_secret") clientsecret: String, @Query(
            "grant_type"
        ) grantType: String
    )
            : Call<AccessTokenRefreshResponseData>

    @FormUrlEncoded
    @POST(BDInstagramHelper.GET_ACCESS_TOKEN_FROM_CODE_API_END_POINT)
    fun getIGAccessTokenFromCode(
        @Field("app_id") appId: String
        , @Field("app_secret") appSecret: String
        , @Field("grant_type") grantType: String
        , @Field("redirect_uri") redirectUri: String
        , @Field("code") code: String
    ): Call<AccessTokenWithCodeResponseData>

}