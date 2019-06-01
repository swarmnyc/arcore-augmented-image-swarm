package com.swarmnyc.arswarm.app

import android.app.Activity
import android.os.Bundle
import com.swarmnyc.arswarm.BuildConfig
import com.swarmnyc.arswarm.R
import com.swarmnyc.arswarm.utils.Logger
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration
import kotlinx.android.synthetic.main.activity_guest.*

class GuestActivity : Activity() {
    private lateinit var mRtcEngine: RtcEngine
    private  val mRtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
            Logger.d("onFirstRemoteVideoDecoded $uid")

            runOnUiThread { setupRemoteVideo(uid) }
        }

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            super.onJoinChannelSuccess(channel, uid, elapsed)
            Logger.d("onJoinChannelSuccess $channel, $uid")
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            super.onUserJoined(uid, elapsed)
            Logger.d("onUserJoined $uid")
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            Logger.d("onUserOffline $uid")
            runOnUiThread { root.removeViewAt(1) }
        }

        override fun onError(err: Int) {
            super.onError(err)
            Logger.d("onStreamMessageError $err")
        }

        override fun onStreamMessageError(uid: Int, streamId: Int, error: Int, missed: Int, cached: Int) {
            super.onStreamMessageError(uid, streamId, error, missed, cached)
            Logger.d("onStreamMessageError $uid, $error")
        }

        override fun onWarning(warn: Int) {
            super.onWarning(warn)
            Logger.d("onWarning $warn")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( R.layout.activity_guest)

        initRtcEngine()
    }

    private fun initRtcEngine() {
        mRtcEngine = RtcEngine.create(baseContext, BuildConfig.AGORA_APP_ID, mRtcEventHandler)
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
        mRtcEngine.enableVideo()
        mRtcEngine.setClientRole(Constants.CLIENT_ROLE_AUDIENCE)

        mRtcEngine.joinChannel(null, "ar-core", "", 2)
    }

    private fun setupRemoteVideo(uid: Int) {
        if (root.childCount >= 1) {
            return
        }

        val surfaceView = RtcEngine.CreateRendererView(baseContext)
        root.addView(surfaceView)
        mRtcEngine.setupRemoteVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid))
    }

    override fun onDestroy() {
        mRtcEngine.leaveChannel()
        RtcEngine.destroy()

        super.onDestroy()
    }
}

