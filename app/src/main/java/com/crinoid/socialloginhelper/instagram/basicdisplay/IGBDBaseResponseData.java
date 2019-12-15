package com.crinoid.socialloginhelper.instagram.basicdisplay;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class IGBDBaseResponseData implements Serializable {
    private IGBDErrorData error;

    public IGBDErrorData getError() {
        return error;
    }

    /**
     * {
     * "error": {
     * "message": "Error validating access token: Session has expired on Wednesday, 11-Dec-19 03:00:00 PST. The current time is Wednesday, 11-Dec-19 03:05:54 PST.",
     * "type": "OAuthException",
     * "code": 190,
     * "fbtrace_id": "ACLTcAAc5Pja35FkgdMlJHA"
     * }
     * }
     */
    public static class IGBDErrorData {
        private String message;
        private String type;
        private int code;
        @SerializedName("fbtrace_id")
        private String fbtraceId;

        public String getMessage() {
            return message;
        }

        public String getType() {
            return type;
        }

        public int getCode() {
            return code;
        }

        public String getFbtraceId() {
            return fbtraceId;
        }
    }
}
