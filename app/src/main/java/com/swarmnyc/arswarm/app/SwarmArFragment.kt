package com.swarmnyc.arswarm.app

import android.os.Bundle
import android.view.*
import android.widget.Toast
import com.google.ar.core.*
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.ux.ArFragment
import com.swarmnyc.arswarm.ar.AugmentedImageAnchorNode
import com.swarmnyc.arswarm.ar.ArResources
import com.swarmnyc.arswarm.ar.SwarmAnchorNode
import com.swarmnyc.arswarm.utils.Logger

class SwarmArFragment : ArFragment() {
    private val trackableMap = mutableMapOf<String, AugmentedImageAnchorNode>()

    var setOnStarted: (() -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view!!.visibility = View.GONE

        // Turn off the plane discovery since we're only looking for ArImages
        planeDiscoveryController.hide()
        planeDiscoveryController.setInstructionView(null)
        arSceneView.planeRenderer.isEnabled = false
        arSceneView.scene.setOnTouchListener { _, motionEvent ->
            swipeAnGestureDetector.onTouchEvent(motionEvent)
        }

        arSceneView.scene.addOnUpdateListener(::onUpdateFrame)

        ArResources.init(this.context!!).handle { _, _ ->
            setOnStarted?.invoke()

            view.visibility = View.VISIBLE
        }

        return view
    }

    override fun onPause() {
        super.onPause()
        trackableMap.forEach {
            arSceneView.scene.removeChild(it.value)
        }

        trackableMap.clear()
    }

    override fun getSessionConfiguration(session: Session): Config {
        val config = super.getSessionConfiguration(session)
        config.focusMode = Config.FocusMode.AUTO

        config.augmentedImageDatabase = AugmentedImageDatabase.deserialize(session, context!!.resources.assets.open("ar.imgdb"))

        return config
    }

    private fun createArNode(image: AugmentedImage) {
        Logger.d("create : ${image.name}(${image.index}), pose: ${image.centerPose}, ex: ${image.extentX}, ez: ${image.extentZ}")

        when (image.name) {
            "swarm" -> {
                val node = SwarmAnchorNode().init(image)
                trackableMap[image.name] = node
                arSceneView.scene.addChild(node)

                Toast.makeText(context, "add swarm", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun onUpdateFrame(@Suppress("UNUSED_PARAMETER") frameTime: FrameTime?) {
        val frame = arSceneView.arFrame

        // If there is no frame or ARCore is not tracking yet, just return.
        if (frame == null || frame.camera.trackingState != TrackingState.TRACKING) {
            return
        }

        frame.getUpdatedTrackables(AugmentedImage::class.java).forEach { image ->
            when (image.trackingState) {
                TrackingState.TRACKING -> if (trackableMap.contains(image.name)) {
                    if (trackableMap[image.name]?.update(image) == true) {
                        Logger.d("update node: ${image.name}(${image.index}), pose: ${image.centerPose}, ex: ${image.extentX}, ez: ${image.extentZ}")
                    }
                } else {
                    createArNode(image)
                }
                TrackingState.STOPPED -> {
                    Logger.d("remove note: ${image.name}(${image.index})")

                    trackableMap.remove(image.name)
                }
                else -> {
                }
            }
        }
    }

    private val swipeAnGestureDetector = GestureDetector(null, object : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_DISTANCE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            val swarmAN = trackableMap["swarm"] as? SwarmAnchorNode

            if (swarmAN != null && swarmAN.isActive) {
                val distanceX = e2.x - e1.x
                val distanceY = e2.y - e1.y
                if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (distanceX > 0) {
                        swarmAN.forwardScene()
                    } else {
                        swarmAN.backwardScene()
                    }

                    return true
                }
            }

            return false
        }
    })
}