package com.crinoid.socialloginhelper.instagram.basicdisplay

import android.content.Context
import android.text.TextUtils
import androidx.fragment.app.FragmentManager
import com.crinoid.retrofit.MyRetrofitApiInterface
import com.crinoid.socialloginhelper.instagram.AuthenticationDialog
import com.crinoid.socialloginhelper.instagram.Globals.*
import com.crinoid.utils.SharedPrefs
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class BDInstagramHelper : AuthenticationDialog.AuthenticationListener {
    private var mAuhListener: DataListener<InstagramBDUserData>? = null

    private var isLoggingIn = false

    val isLoggedIn: Boolean
        get() = !TextUtils.isEmpty(sharedPrefs.localData.AccessToken?.accessToken) && sharedPrefs.localData.instaUserBd != null

    val userData: InstagramBDUserData?
        get() = sharedPrefs.localData.instaUserBd

    fun logout() {
        sharedPrefs.localData.AccessToken = null
        sharedPrefs.localData.instaUserBd = null
        sharedPrefs.saveDataLocally()
    }

    fun performLogin(
        fragmentManager: FragmentManager,
        authListener: DataListener<InstagramBDUserData>?,
        languageCode: String?
    ) {
        if (isLoggedIn) {
            authListener?.onSuccess(sharedPrefs.localData.instaUserBd)
            return
        }
        if (isLoggingIn) {
            return
        }

        isLoggingIn = true
        this.mAuhListener = authListener
        val authenticationDialog = AuthenticationDialog(
            this,
            INSTAGRAM_BASE_URL +
                    "oauth/authorize/?app_id=" +
                    INSTAGRAM_APP_ID +
                    "&redirect_uri=" + INSTAGRAM_REDIRECT_URL +
                    "&response_type=code&display=touch&scope=" + INSTAGRAM_LOGIN_SCOPES,
            languageCode
        )
        val ft = fragmentManager.beginTransaction()
        val prev = fragmentManager.findFragmentByTag("dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        authenticationDialog.isCancelable = true
        authenticationDialog.show(ft, "dialog")
    }


    override fun onCodeReceived(code: String?) {
        sharedPrefs.localData.instaAcessCode = code
        sharedPrefs.saveDataLocally()
        fetchShortLivedAccessToken(object : DataListener<AccessTokenWithCodeResponseData> {
            override fun onSuccess(data: AccessTokenWithCodeResponseData?) {
                data?.let {
                    fetchLongLivedAccessToken(object :
                        DataListener<AccessTokenRefreshResponseData> {
                        override fun onSuccess(data: AccessTokenRefreshResponseData?) {
                            fetchProfileData(object : DataListener<InstagramBDUserData> {
                                override fun onSuccess(data: InstagramBDUserData?) {
                                    isLoggingIn = false
                                    if (mAuhListener != null) {
                                        mAuhListener!!.onSuccess(data)
                                    }
                                }

                                override fun onFailure(error: String?) {
                                    isLoggingIn = false
                                    sharedPrefs.localData.AccessToken = null
                                    sharedPrefs.saveDataLocally()
                                    mAuhListener?.onFailure(error)
                                }
                            }, true)
                        }

                        override fun onFailure(error: String?) {
                            mAuhListener?.onFailure(error)
                        }
                    }, it.accessToken)
                }
            }

            override fun onFailure(error: String?) {
                mAuhListener?.onFailure(error)
            }
        })
    }


    override fun onTokenReceived(auth_token: String) {

    }

    override fun onError(error: String) {
        isLoggingIn = false
        if (mAuhListener != null) {
            mAuhListener!!.onFailure(error)
        }
    }

    override fun onComplete() {
        isLoggingIn = false
    }

    interface DataListener<T> {
        fun onSuccess(data: T?)
        fun onFailure(error: String?)
    }

    companion object {
        private const val API_END_POINT = "https://graph.instagram.com/"
        private const val USER_API_END_POINT = API_END_POINT
        const val SELF_USER_API_END_POINT = USER_API_END_POINT + "me/"
        const val EXCHANGE_ACCESS_TOKEN_API_END_POINT = API_END_POINT + "access_token"
        const val REFRESH_ACCESS_TOKEN_API_END_POINT = API_END_POINT + "refresh_access_token"
        const val GET_ACCESS_TOKEN_FROM_CODE_API_END_POINT =
            INSTAGRAM_BASE_URL + "oauth/access_token"

        var API_BASE_URL = "https://your.api-base.url"
        var READ_OUT_TIME = 50000
        var WRITE_OUT_TIME = 50000
        private val apiInterface: MyRetrofitApiInterface
        private lateinit var sharedPrefs: SharedPrefs

        fun initialiseWithContext(context: Context) {
            sharedPrefs = SharedPrefs(context = context)
        }

        init {
            var httpClient = OkHttpClient.Builder()
            httpClient.readTimeout(READ_OUT_TIME.toLong(), TimeUnit.MILLISECONDS)
            httpClient.connectTimeout(WRITE_OUT_TIME.toLong(), TimeUnit.MILLISECONDS)
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addInterceptor(logging)

            var builder =
                Retrofit.Builder().baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
            builder.client(httpClient.build())
            var retrofit = builder.build()
            apiInterface = retrofit.create(MyRetrofitApiInterface::class.java)
        }

        fun fetchProfileData(listener: DataListener<InstagramBDUserData>?, forceRefresh: Boolean) {
            if (!forceRefresh && sharedPrefs.localData.instaUserBd != null) {
                listener?.onSuccess(sharedPrefs.localData.instaUserBd)
                return
            }

            apiInterface.getSelfBDInstagramProfile(
                sharedPrefs.localData.AccessToken?.accessToken!!,
                INSTAGRAM_USER_FIELDS
            )
                .enqueue(object : Callback<InstagramBDUserData> {
                    override fun onResponse(
                        call: Call<InstagramBDUserData>,
                        response: Response<InstagramBDUserData>
                    ) {
                        if (response.body() != null) {
                            sharedPrefs.localData.instaUserBd = response.body()
                            sharedPrefs.saveDataLocally()
                            listener?.onSuccess(sharedPrefs.localData.instaUserBd)
                        } else {
                            listener?.onFailure("")
                        }
                    }

                    override fun onFailure(
                        call: Call<InstagramBDUserData>,
                        t: Throwable
                    ) {
                        listener?.onFailure(t.message)
                    }
                })
        }

        fun fetchShortLivedAccessToken(
            listener: DataListener<AccessTokenWithCodeResponseData>?
        ) {
            if (sharedPrefs.localData.instaAcessCode == null) {
                listener?.onFailure("Access code is not set. Please authorize app by login first")
                return
            }

            apiInterface.getIGAccessTokenFromCode(
                INSTAGRAM_APP_ID,
                INSTAGRAM_APP_SECRET,
                "authorization_code",
                INSTAGRAM_REDIRECT_URL,
                sharedPrefs.localData.instaAcessCode!!
            )
                .enqueue(object : Callback<AccessTokenWithCodeResponseData> {
                    override fun onResponse(
                        call: Call<AccessTokenWithCodeResponseData>,
                        response: Response<AccessTokenWithCodeResponseData>
                    ) {
                        if (response.body() != null) {
                            if (response.body()?.errorMessage == null) {
                                listener?.onSuccess(response.body())
                            } else {
                                listener?.onFailure(response.body()?.errorMessage)
                            }
                        } else {
                            var tmp = response.errorBody()?.string()
                            listener?.onFailure(response.errorBody()?.string())
                        }
                    }

                    override fun onFailure(
                        call: Call<AccessTokenWithCodeResponseData>,
                        t: Throwable
                    ) {
                        listener?.onFailure(t.message)
                    }
                })
        }

        fun fetchLongLivedAccessToken(
            listener: DataListener<AccessTokenRefreshResponseData>?, shortLivedAccessToken: String
        ) {
            apiInterface.exchangeIGBDAccessToken(
                shortLivedAccessToken,
                INSTAGRAM_APP_SECRET,
                IGBD_GRANT_TYPE_EXCHANGE
            )
                .enqueue(object : Callback<AccessTokenRefreshResponseData> {
                    override fun onResponse(
                        call: Call<AccessTokenRefreshResponseData>,
                        response: Response<AccessTokenRefreshResponseData>
                    ) {
                        if (response.body() != null) {
                            if (response.body()?.error == null) {
                                sharedPrefs.localData.AccessToken = response.body()
                                listener?.onSuccess(response.body())
                            } else {
                                listener?.onFailure(response.body()?.error?.message)
                            }
                        } else {
                            listener?.onFailure("")
                        }
                    }

                    override fun onFailure(
                        call: Call<AccessTokenRefreshResponseData>,
                        t: Throwable
                    ) {
                        listener?.onFailure(t.message)
                    }
                })
        }

        fun refreshTokenIfNeeded(listener: DataListener<AccessTokenRefreshResponseData>?): Boolean {
            if (sharedPrefs.isLoggedIn()) {
                sharedPrefs.localData.AccessToken?.let {
                    var diff =
                        System.currentTimeMillis() - sharedPrefs.localData.iGBDTokenRefreshTime
                    if (diff > it.expiresInMillis * 0.8f) {
                        refreshLongLivedAccessToken(listener, it.accessToken)
                        return true
                    }
                }
            }
            return false
        }

        fun refreshLongLivedAccessToken(
            listener: DataListener<AccessTokenRefreshResponseData>?, expiredAccessToken: String
        ) {
            apiInterface.refreshIGBDAccessToken(
                expiredAccessToken,
                INSTAGRAM_APP_SECRET,
                IGBD_GRANT_TYPE_REFRESH
            )
                .enqueue(object : Callback<AccessTokenRefreshResponseData> {
                    override fun onResponse(
                        call: Call<AccessTokenRefreshResponseData>,
                        response: Response<AccessTokenRefreshResponseData>
                    ) {
                        if (response.body() != null) {
                            if (response.body()?.error == null) {
                                sharedPrefs.localData.AccessToken = response.body()
                                listener?.onSuccess(response.body())
                            } else {
                                listener?.onFailure(response.body()?.error?.message)
                            }
                        } else {
                            listener?.onFailure("")
                        }
                    }

                    override fun onFailure(
                        call: Call<AccessTokenRefreshResponseData>,
                        t: Throwable
                    ) {
                        listener?.onFailure(t.message)
                    }
                })
        }

    }
}
