/*******************************************************************************
 * Copyright(C) 2021 SEERSLAB Inc. All Rights Reserved.
 *
 * PROPRIETARY/CONFIDENTIAL
 *
 * This software is the confidential and proprietary information of
 * SEERSLAB Inc. You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license
 * agreement you entered into with SEERSLAB Inc.
 *
 * "SEERSLAB Inc." MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY
 * OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT. "SEERSLAB Inc." SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS
 * SOFTWARE OR ITS DERIVATIVES.
 ******************************************************************************/
package com.seerslab.argearv3.sample.ui

import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.seerslab.argearv3.sample.R
import com.seerslab.argearv3.sample.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var dataBinding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val uriString = intent.getStringExtra(INTENT_URI)

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_player)

        //Creating MediaController
        val mediaController = MediaController(this)
        mediaController.setAnchorView(dataBinding.videoView)

        //specify the location of media file
        val uri = Uri.parse(uriString)

        dataBinding.videoView.setOnPreparedListener { mp -> mp.isLooping = true }
        //Setting MediaController and URI, then starting the videoView
        dataBinding.videoView.setMediaController(mediaController)
        dataBinding.videoView.setVideoURI(uri)
        dataBinding.videoView.requestFocus()
        dataBinding.videoView.start()
    }

    override fun onPause() {
        super.onPause()
        if (dataBinding.videoView.isPlaying) {
            dataBinding.videoView.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!dataBinding.videoView.isPlaying) {
            dataBinding.videoView.resume()
            dataBinding.videoView.start()
        }
    }

    companion object {
        var INTENT_URI = "player_uri"
    }
}
