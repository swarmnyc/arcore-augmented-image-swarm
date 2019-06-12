package com.swarmnyc.arswarm.ar


class SwarmScene2 : AugmentedImageNodeGroup() {
    override fun onInit() {
        VideoAugmentedImageNode().init(anchorNode, this)
    }
}

class VideoAugmentedImageNode : AugmentedImageNode(ArResources.videoRenderable) {
    override fun initLayout() {
        super.initLayout()

        // the renderable is rectangle, so it have to scale to r
        val videoRatio = ArResources.videoPlayer.videoWidth.toFloat() / ArResources.videoPlayer.videoHeight

        offsetZ = (anchorNode.arHeight / 2.0f)
        offsetZ *= 0.8f

        // make video a little bigger to cover the while image
        scaledWidth *= 1.0f
        scaledHeight = scaledHeight * 1.1f / videoRatio
        scaledDeep = 1f
        localRotation = ArResources.viewRenderableRotation
    }

    override fun onActivate() {
        super.onActivate()

        if (!ArResources.videoPlayer.isPlaying) {
            ArResources.videoPlayer.start()
        }
    }

    override fun onDeactivate() {
        super.onDeactivate()

        if (ArResources.videoPlayer.isPlaying) {
            ArResources.videoPlayer.pause()
        }
    }
}