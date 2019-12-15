package com.crinoid.socialloginhelper.instagram;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.crinoid.pesopie.R;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static com.crinoid.pesopie.instagram.Globals.INSTAGRAM_REDIRECT_URL;

@SuppressLint("ValidFragment")
public class AuthenticationDialog extends DialogFragment {

    private final String redirect_url;
    private final String request_url;
    private AuthenticationListener listener;

    WebViewClient webViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(redirect_url)) {
                AuthenticationDialog.this.dismiss();
                return true;
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                    url = java.net.URLDecoder.decode(url, StandardCharsets.UTF_8.name());

                } else {
                    url = java.net.URLDecoder.decode(url);

                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (url.contains("access_token=")) {
                String access_token = url.split("access_token=")[1];
                if (listener != null) {
                    listener.onTokenReceived(access_token);
                }
            } else if (url.contains("?code=")) {
//                Uri uri = Uri.parse(url);
//                String accessCode = uri.getEncodedFragment();
                String accessCode = url.split("\\?code=")[1].split("#")[0];
                if (listener != null) {
                    listener.onCodeReceived(accessCode);
                }
            }
        }


        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            if (listener != null) {
                if (errorResponse != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (!TextUtils.isEmpty(errorResponse.getReasonPhrase())) {
                            Toast.makeText(view.getContext(), errorResponse.getReasonPhrase(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
//                        Toast.makeText(view.getContext(),errorResponse.getReasonPhrase(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    public AuthenticationDialog(AuthenticationListener listener, String requestUrl, String languageCode) {
        super();
        this.listener = listener;
        this.redirect_url = INSTAGRAM_REDIRECT_URL;//context.getResources().getString(R.string.redirect_url);
        this.request_url = requestUrl;
        Log.d("AuthenticationDialog", "AuthenticationDialog() = [" + request_url + "]");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (this.listener != null) {
            listener.onComplete();
        }
    }


    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        if (listener != null) {
            listener.onError("Cancelled");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.auth_dialog, container, false);

    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        WebView webView = view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(webViewClient);
        webView.clearCache(true);
        webView.getSettings().setAppCacheEnabled(false);
        CookieSyncManager.createInstance(getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.flush();
        }
        webView.loadUrl(request_url);
//        view.findViewById(R.id.iv_close_ad).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//                if (listener != null) {
//                    listener.onError("Cancelled");
//                }
//            }
//        });

    }

    public interface AuthenticationListener {
        /**
         * @deprecated use onCodeReceived instead
         */
        void onTokenReceived(String authToken);

        void onCodeReceived(String code);

        void onError(String error);

        void onComplete();
    }

}