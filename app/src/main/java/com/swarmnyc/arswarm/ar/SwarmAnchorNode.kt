package com.swarmnyc.arswarm.ar

import android.view.GestureDetector
import android.view.MotionEvent
import com.swarmnyc.arswarm.utils.Logger

class SwarmAnchorNode : AugmentedImageAnchorNode() {
    override val imageWidth: Float = 1F // 100 cm
    override val imageHeight: Float = 0.6667f // 66.7 cm

    private val sceneList = mutableListOf<AugmentedImageNodeGroup>()
    private var currentSceneIndex = 0

    override fun onInit() {
        sceneList.add(SwarmScene1().init(this))
        sceneList.add(SwarmScene2().init(this))
        sceneList.add(SwarmScene3().init(this))


        initTouchEvent()
    }

    override fun onActivate() {
        super.onActivate()

        currentSceneIndex = 0
        changeScene()
    }

    private fun initTouchEvent() {
        setOnTouchListener { _, motionEvent ->
            gestureDetector.onTouchEvent(motionEvent)
        }
    }

    private val gestureDetector = GestureDetector(null, object : GestureDetector.OnGestureListener {
        private val SWIPE_DISTANCE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onShowPress(e: MotionEvent?) {}

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return true
        }

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            val distanceX = e2.x - e1.x
            val distanceY = e2.y - e1.y
            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0) {
                    currentSceneIndex = (currentSceneIndex + 1) % sceneList.size
                    changeScene()
                } else {
                    currentSceneIndex = if (currentSceneIndex == 0) {
                        sceneList.size - 1
                    } else {
                        currentSceneIndex - 1
                    }

                    changeScene()
                }

                return true
            }

            return false
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            return true
        }

        override fun onLongPress(e: MotionEvent?) {}
    })

    private fun changeScene() {
        Logger.d("changeScene $currentSceneIndex")

        sceneList.forEachIndexed { index, scene ->
            scene.isEnabled = index == currentSceneIndex
        }
    }
}