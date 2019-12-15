package com.crinoid.socialloginhelper.instagram

import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import com.crinoid.socialloginhelper.R
import com.crinoid.socialloginhelper.instagram.basicdisplay.BDInstagramHelper
import com.crinoid.socialloginhelper.instagram.basicdisplay.InstagramBDUserData
import com.crinoid.utils.SharedPrefs
import kotlinx.android.synthetic.main.activity_sample_login.*


class SampleLoginActivity : AppCompatActivity(),
    BDInstagramHelper.DataListener<InstagramBDUserData> {

    val instaHelper = BDInstagramHelper()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample_login)
        BDInstagramHelper.initialiseWithContext(this)
        if (instaHelper.isLoggedIn) {
            btn_login.text = "Profile"
        }
        btn_login.setOnClickListener {
            if (instaHelper.isLoggedIn) {
                BDInstagramHelper.fetchProfileData(this, true)
            } else {
                instaHelper.performLogin(supportFragmentManager, this, "en")
            }
        }
    }



    override fun onFailure(error: String?) {
        Toast.makeText(this, "error fetching insta profile $error", LENGTH_LONG).show()
    }

    override fun onSuccess(data: InstagramBDUserData?) {
        Toast.makeText(
            this,
            "user name = " + data?.username + " , followercount = " + data?.followersCount,
            LENGTH_LONG
        ).show()
    }
}
