package com.swarmnyc.arswarm.ar

import android.animation.ObjectAnimator
import android.view.animation.LinearInterpolator
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.QuaternionEvaluator
import com.google.ar.sceneform.math.Vector3

class SwarmAnchorNode : AugmentedImageAnchorNode() {
    override val imageWidth: Float = 1F // 100 cm
    override val imageHeight: Float = 0.6667f // 66.7 cm

    override fun addNodes() {
        WallAugmentedImageNode().init(this)
        SwarmAugmentedImageNode().init(this)
        MakeAppAugmentedImageNode().init(this)
        HeartAugmentedImageNode().init(this)
    }
}

class SwarmAugmentedImageNode : AugmentedImageNode( Renderables.swarmRendereable)

class MakeAppAugmentedImageNode : AugmentedImageNode(Renderables.makeAppRendereable) {
    override fun initLayout() {
        super.initLayout()

        offsetX = -0.03738f * scaledWidth
        offsetZ = 0.2142f * scaledHeight
    }
}

class HeartAugmentedImageNode : AugmentedImageNode(Renderables.heartRendereable) {

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

class WallAugmentedImageNode : AugmentedImageNode(Renderables.wallRenderable){
    override fun modifyLayout() {
        super.modifyLayout()

        localRotation = Quaternion(Vector3(1f, 0f, 0f), -90f)
    }
}