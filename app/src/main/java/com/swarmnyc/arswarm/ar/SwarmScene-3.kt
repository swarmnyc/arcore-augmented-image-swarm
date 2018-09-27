package com.swarmnyc.arswarm.ar

import android.content.Intent
import android.net.Uri
import android.view.MotionEvent
import com.google.ar.sceneform.HitTestResult


class SwarmScene3 : AugmentedImageNodeGroup() {
    override fun onInit() {
        WebImageAugmentedImageNode().init(anchorNode, this)
        VisitAugmentedImageNode().init(anchorNode, this)
    }
}

class WebImageAugmentedImageNode : AugmentedImageNode(ArResources.webImageRenderable) {
    override fun initLayout() {
        super.initLayout()

        localRotation = ArResources.viewRenderableRotation
    }


}

class VisitAugmentedImageNode : AugmentedImageNode(ArResources.visitRenderable) {
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
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.swarmnyc.com"))

        this.scene.view.context.startActivity(intent)

        return false
    }
}