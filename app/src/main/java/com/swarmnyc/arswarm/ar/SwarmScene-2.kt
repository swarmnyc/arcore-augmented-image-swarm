package com.swarmnyc.arswarm.ar


class SwarmScene2 : AugmentedImageNodeGroup() {
    override fun onInit() {
        VideoAugmentedImageNode().init(anchorNode, this)
    }
}

class VideoAugmentedImageNode : AugmentedImageNode(ArResources.videoRenderable) {
    override fun initLayout() {
        super.initLayout()

        offsetZ = (anchorNode.arHeight / 2.0f)
        // make it a little bigger
        scaledWidth *= 1.1f
        // make the ratio to 4/3
        scaledHeight = scaledHeight * 1.1f / 1.6f
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