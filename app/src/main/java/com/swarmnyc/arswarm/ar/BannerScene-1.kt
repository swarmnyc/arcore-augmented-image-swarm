package com.swarmnyc.arswarm.ar

import android.animation.ObjectAnimator
import android.view.MotionEvent
import android.view.animation.LinearInterpolator
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.QuaternionEvaluator
import com.google.ar.sceneform.math.Vector3


class BannerScene1 : AugmentedImageNodeGroup() {
    override fun onInit() {
        BannerWallAugmentedImageNode().init(anchorNode, this)
        BannerSwarmAugmentedImageNode().init(anchorNode, this)
        BannerMakeAppAugmentedImageNode().init(anchorNode, this)
        BannerHeartAugmentedImageNode().init(anchorNode, this)
    }
}

class BannerWallAugmentedImageNode : AugmentedImageNode(ArResources.wallRenderable) {

    override fun initLayout() {
        super.initLayout()
        scaledHeight *= 0.8F

        offsetZ = -0.1F * scaledHeight
    }

    override fun modifyLayout() {
        super.modifyLayout()

        localRotation = ArResources.viewRenderableRotation
    }
}

class BannerSwarmAugmentedImageNode : AugmentedImageNode(ArResources.swarmRendereable) {
    companion object {
        // TODO: change the multiplier to match the size
        const val ScaleFactor = 0.80f
    }

    override fun initLayout() {
        scaledWidth = anchorNode.scaledWidth * ScaleFactor
        scaledHeight = anchorNode.scaledHeight * ScaleFactor
        scaledDeep = anchorNode.scaledWidth * ScaleFactor

        offsetZ = -0.091f * scaledHeight

    }
}

class BannerMakeAppAugmentedImageNode : AugmentedImageNode(ArResources.makeAppRendereable) {
    companion object {
        // TODO: change the multiplier to match the size
        const val ScaleFactor = 0.8f
    }

    override fun initLayout() {
        scaledWidth = anchorNode.scaledWidth * ScaleFactor
        scaledHeight = anchorNode.scaledHeight * ScaleFactor
        scaledDeep = anchorNode.scaledWidth * ScaleFactor

        offsetX = -0.03738f * scaledWidth
        offsetZ = 0.15f * scaledHeight
    }
}

class BannerHeartAugmentedImageNode : HeartAugmentedImageNode() {
    companion object {
        const val ScaleFactor = 0.8f
    }

    override fun initLayout() {
        scaledWidth = anchorNode.scaledWidth * ScaleFactor
        scaledHeight = anchorNode.scaledHeight * ScaleFactor
        scaledDeep = anchorNode.scaledWidth * ScaleFactor

        offsetX = 0.25f * scaledWidth
        offsetY = 0.01f
        offsetZ = 0.15f * scaledHeight
    }
}