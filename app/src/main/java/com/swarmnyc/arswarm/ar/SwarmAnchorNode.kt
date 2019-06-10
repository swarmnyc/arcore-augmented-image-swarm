package com.swarmnyc.arswarm.ar

import com.swarmnyc.arswarm.utils.Logger

open class SwarmAnchorNode : AugmentedImageAnchorNode() {
    override val imageWidth: Float = 1F // 100 cm
    override val imageHeight: Float = 0.6667f // 66.7 cm

    protected val sceneList = mutableListOf<AugmentedImageNodeGroup>()
    protected var currentSceneIndex = 0

    override fun onInit() {
        sceneList.add(SwarmScene1().init(this))
        sceneList.add(SwarmScene2().init(this))
        sceneList.add(SwarmScene3().init(this))
    }

    override fun onActivate() {
        super.onActivate()

        changeScene(0)
    }

    fun forwardScene() {
        changeScene((currentSceneIndex + 1) % sceneList.size)
    }

    fun backwardScene() {
        changeScene((currentSceneIndex - 1 + sceneList.size) % sceneList.size)
    }

    private fun changeScene(index: Int) {
        currentSceneIndex = index

        Logger.d("changeScene($currentSceneIndex)")

        sceneList.forEachIndexed { i, scene ->
            scene.isEnabled = i == currentSceneIndex
        }
    }
}