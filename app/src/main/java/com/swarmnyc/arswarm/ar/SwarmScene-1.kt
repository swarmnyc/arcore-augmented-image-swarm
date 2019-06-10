package com.swarmnyc.arswarm.ar

import android.animation.ObjectAnimator
import android.view.MotionEvent
import android.view.animation.LinearInterpolator
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.QuaternionEvaluator
import com.google.ar.sceneform.math.Vector3


class SwarmScene1 : AugmentedImageNodeGroup() {
    override fun onInit() {
        WallAugmentedImageNode().init(anchorNode, this)
        SwarmAugmentedImageNode().init(anchorNode, this)
        MakeAppAugmentedImageNode().init(anchorNode, this)
        HeartAugmentedImageNode().init(anchorNode, this)
//        HintSwipeAugmentedImageNode().init(anchorNode, this)
    }
}

class SwarmAugmentedImageNode : AugmentedImageNode(ArResources.swarmRendereable)

class MakeAppAugmentedImageNode : AugmentedImageNode(ArResources.makeAppRendereable) {
    override fun initLayout() {
        super.initLayout()

        offsetX = -0.03738f * scaledWidth
        offsetZ = 0.2142f * scaledHeight
    }
}

open class HeartAugmentedImageNode : AugmentedImageNode(ArResources.heartRendereable) {

    private var animation: ObjectAnimator? = null

    override fun initLayout() {
        super.initLayout()

        offsetX = 0.22299f * scaledWidth
        offsetY = 0.01f
        offsetZ = 0.2142f * scaledHeight
    }

    override fun onActivate() {
        super.onActivate()

        val orientation1 = Quaternion.axisAngle(Vector3(0.1f, 0.1f, 1.0f), 0f)
        val orientation2 = Quaternion.axisAngle(Vector3(0.1f, 0.1f, 1.0f), 120f)
        val orientation3 = Quaternion.axisAngle(Vector3(0.1f, 0.1f, 1.0f), 240f)
        val orientation4 = Quaternion.axisAngle(Vector3(0.1f, 0.1f, 1.0f), 360f)

        animation = ObjectAnimator().apply {
            setObjectValues(orientation1, orientation2, orientation3, orientation4)
            propertyName = "localRotation"

            setEvaluator(QuaternionEvaluator())

            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
            interpolator = LinearInterpolator()
            setAutoCancel(true)

            target = this@HeartAugmentedImageNode
            duration = 1000
            start()
        }
    }

    override fun onDeactivate() {
        super.onDeactivate()

        if (animation == null) return

        animation?.cancel()
        animation = null
    }
}

class WallAugmentedImageNode : AugmentedImageNode(ArResources.wallRenderable) {
    override fun modifyLayout() {
        super.modifyLayout()

        localRotation = ArResources.viewRenderableRotation
    }
}

class HintSwipeAugmentedImageNode : AugmentedImageNode(ArResources.hintSwipeRenderable) {
    override fun initLayout() {
        super.initLayout()

        // make it under
        offsetZ = anchorNode.arHeight / 2 + anchorNode.arHeight * 0.1f
    }

    override fun modifyLayout() {
        super.modifyLayout()

        localRotation = ArResources.viewRenderableRotation
    }

    override fun onTouchEvent(p0: HitTestResult?, p1: MotionEvent?): Boolean {
        isEnabled = false
        return false
    }
}