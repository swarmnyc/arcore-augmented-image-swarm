package com.swarmnyc.arswarm.ar

class BannerAnchorNode : SwarmAnchorNode() {
    companion object{
        // TODO: change the multiplier to match the size
        // the bigger factor that get smaller 3D models
        const val ScaleFactor = 1.1f
    }

    override val imageWidth: Float = 1F * ScaleFactor
    override val imageHeight: Float = 0.6667f * ScaleFactor

    override fun onInit() {
        sceneList.add(BannerScene1().init(this))
        sceneList.add(SwarmScene2().init(this))
        sceneList.add(SwarmScene3().init(this))
    }
}