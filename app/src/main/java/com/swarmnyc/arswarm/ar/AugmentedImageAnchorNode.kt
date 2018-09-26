package com.swarmnyc.arswarm.ar

import com.google.ar.core.AugmentedImage
import com.google.ar.sceneform.AnchorNode
import com.swarmnyc.arswarm.utils.Logger

abstract class AugmentedImageAnchorNode : AnchorNode() {
    companion object {
        const val sizeChangeThresholder = 0.001f // 1cm
    }

    // the real image size
    abstract val imageWidth: Float
    abstract val imageHeight: Float

    // the size get from AugmentedImage
    var arWidth: Float = 1f
        private set
    var arHeight: Float = 1f
        private set

    // get scaled size from  arSize / imageSize
    var scaledWidth: Float = 1f
        private set

    var scaledHeight: Float = 1f
        private set

    fun init(image: AugmentedImage): AugmentedImageAnchorNode {
        Logger.d("${javaClass.simpleName} inited")

        // Set the anchor based on the center of the image.
        anchor = image.createAnchor(image.centerPose)

        updateSize(Math.abs(image.extentX), Math.abs(image.extentZ))

        addNodes()

        return this
    }

    fun update(image: AugmentedImage): Boolean {
        val nWidth = Math.abs(image.extentX)
        val nHeight = Math.abs(image.extentZ)

        if (Math.abs(nWidth - arWidth) > sizeChangeThresholder || Math.abs(nHeight - arHeight) > sizeChangeThresholder) {
            updateSize(nWidth, nHeight)

            this.children.forEach {
                if (it is AugmentedImageNode) {
                    it.initLayout()
                    it.modifyLayout()
                }
            }
            return true
        }

        return false
    }

    private fun updateSize(width: Float, height: Float) {
        arWidth = width
        arHeight = height

        scaledWidth = arWidth / imageWidth
        scaledHeight = arHeight / imageHeight
    }

    abstract fun addNodes()
}