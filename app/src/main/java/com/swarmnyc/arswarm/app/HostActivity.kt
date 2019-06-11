package com.swarmnyc.arswarm.app

import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.view.PixelCopy
import android.widget.Toast
import com.swarmnyc.arswarm.BuildConfig
import com.swarmnyc.arswarm.utils.Logger
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.mediaio.IVideoFrameConsumer
import io.agora.rtc.mediaio.IVideoSource
import io.agora.rtc.mediaio.MediaIO
import io.agora.rtc.video.VideoEncoderConfiguration
import java.nio.ByteBuffer


class HostActivity : ArBaseActivity() {
    private lateinit var mSource: ArVideoSource
    private lateinit var mRtcEngine: RtcEngine
    private lateinit var mSenderHandler: Handler
    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            super.onJoinChannelSuccess(channel, uid, elapsed)
            Logger.d("onJoinChannelSuccess $channel, $uid")
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            super.onUserJoined(uid, elapsed)
            Logger.d("onUserJoined $uid")
            runOnUiThread {
                Toast.makeText(baseContext, "A guest connected", Toast.LENGTH_LONG).show()
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            Logger.d("onUserOffline $uid")
            runOnUiThread {
                Toast.makeText(baseContext, "A guest disconnected", Toast.LENGTH_LONG).show()
            }
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

        override fun onFirstLocalVideoFrame(width: Int, height: Int, elapsed: Int) {
            super.onFirstLocalVideoFrame(width, height, elapsed)
            Logger.d("onFirstLocalVideoFrame $width, $height")
        }
    }
    private lateinit var arFragment: SwarmArFragment

    override val viewId: Int = com.swarmnyc.arswarm.R.layout.activity_host

    override fun startAr() {
        arFragment = SwarmArFragment()

        supportFragmentManager.beginTransaction().replace(com.swarmnyc.arswarm.R.id.ar_fragment, arFragment).commit()

        mSenderHandler = Handler(HandlerThread("SenderHandler").apply { start() }.looper)
        arFragment.onStarted = ::initRtcEngine

        arFragment.onFrameUpdate = ::onFrameUpdate
    }

    private fun initRtcEngine() {
        mRtcEngine = RtcEngine.create(baseContext, BuildConfig.AGORA_APP_ID, mRtcEventHandler)

//        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
        mRtcEngine.enableDualStreamMode(true)
        mRtcEngine.setVideoEncoderConfiguration(VideoEncoderConfiguration(VideoEncoderConfiguration.VD_1280x720,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE))
//        mRtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)

        mRtcEngine.disableAudio()
        mRtcEngine.enableVideo()
        mSource = ArVideoSource()
        mRtcEngine.setVideoSource(mSource)
        mRtcEngine.joinChannel(null, "ar-core", "", 1)
    }

    private fun onFrameUpdate() {
        val view = arFragment.arSceneView
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)

        PixelCopy.request(view, bitmap, { copyResult ->
            if (copyResult == PixelCopy.SUCCESS) {
                if (mSource.consumer != null) {
                    // Logger.d("Send Frame")
                    val nb = Bitmap.createScaledBitmap(bitmap, 720, 1280, false)
                    val size = nb.rowBytes * nb.height
                    val byteBuffer = ByteBuffer.allocate(size)
                    nb.copyPixelsToBuffer(byteBuffer)
                    mSource.consumer!!.consumeByteArrayFrame(byteBuffer.array(), MediaIO.PixelFormat.RGBA.intValue(), nb.width, nb.height, 0, System.currentTimeMillis())
                }
            }
        }, mSenderHandler)
    }

    override fun onDestroy() {
        mSenderHandler.looper.quit()
        mRtcEngine.leaveChannel()
        RtcEngine.destroy()

        super.onDestroy()
    }
}

class ArVideoSource : IVideoSource {
    var consumer: IVideoFrameConsumer? = null
        private set

    override fun onInitialize(iVideoFrameConsumer: IVideoFrameConsumer): Boolean {
        consumer = iVideoFrameConsumer
        return true
    }

    override fun onStart(): Boolean {
        return true
    }

    override fun onStop() {}

    override fun onDispose() {}

    override fun getBufferType(): Int {
        return MediaIO.BufferType.BYTE_ARRAY.intValue()
    }
}

