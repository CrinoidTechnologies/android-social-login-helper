package com.crinoid.socialloginhelper.instagram.basicdisplay;

import com.google.gson.annotations.SerializedName;

public class InstagramBDUserData extends IGBDBaseResponseData {
    private String id;
    private String username;
    @SerializedName("profile_picture")
    private
    String profilePicture;
    @SerializedName("account_type")
    private
    String accountType;
    @SerializedName("full_name")
    private
    String fullName = "";
    private String bio = "";
    private String website;
    private InstagramCount counts;

    public String getAccountType() {
        return accountType;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getFullName() {
        return fullName;
    }

    public String getBio() {
        return bio;
    }

    public String getWebsite() {
        return website;
    }

    public InstagramCount getCounts() {
        return counts;
    }

    public int getFollowersCount() {
        if (counts == null)
            return 0;
        return counts.getFollowedBy();
    }

    public class InstagramCount {
        int media;
        int follows;
        @SerializedName("followed_by")
        int followedBy;

        public int getMedia() {
            return media;
        }

        public int getFollows() {
            return follows;
        }

        private int getFollowedBy() {
//            if(BuildConfig.DEBUG){
//                return 20000;
//            }
            return followedBy;
        }
    }
}
