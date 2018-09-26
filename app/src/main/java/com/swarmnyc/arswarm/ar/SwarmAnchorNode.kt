package com.swarmnyc.arswarm.ar

import com.google.ar.core.AugmentedImage
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Renderable
import com.swarmnyc.arswarm.utils.Logger

class SwarmAnchorNode : AnchorNode() {
    companion object {
        const val thresholder = 0.001f // 1cm
    }

    var width: Float = 1f
        private set
    var height: Float = 1f
        private set

    fun init(image: AugmentedImage): SwarmAnchorNode {
        Logger.d("SwarmAnchorNode init")

        width = Math.abs(image.extentX)
        height = Math.abs(image.extentZ)

        // Set the anchor based on the center of the image.
        anchor = image.createAnchor(image.centerPose)

        SwarmAugmentedImageNode().init(this)
        MakeAppAugmentedImageNode().init(this)
        HeartAugmentedImageNode().init(this)

        return this
    }


    fun update(image: AugmentedImage): Boolean {
        val nWidth = Math.abs(image.extentX)
        val nHeight = Math.abs(image.extentZ)

        if (Math.abs(nWidth - width) > thresholder || Math.abs(nHeight - height) > thresholder) {
            width = nWidth
            height = nHeight

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
}

abstract class AugmentedImageNode(private val modelWidth: Float, private val modelHeight: Float, private val resource: Renderable) : Node() {
    var scaleWidth: Float = 1f
    var scaleHeight: Float = 1f
    var offsetX: Float = 0.0f
    var offsetY: Float = 0.0f
    var offsetZ: Float = 0.0f
    lateinit var host: SwarmAnchorNode

    fun init(node: SwarmAnchorNode): AugmentedImageNode {
        host = node
        setParent(node)

        renderable = resource

        initLayout()

        modifyLayout()

        return this
    }

    open fun initLayout() {
        scaleWidth = host.width / modelWidth
        scaleHeight = host.height / modelHeight
    }

    fun modifyLayout(config: (AugmentedImageNode.() -> Unit)? = null) {
        if (config != null) config()

        localScale = Vector3(scaleWidth, scaleWidth, scaleWidth)
        localPosition = Vector3(offsetX, offsetY, offsetZ)

        Logger.d("${javaClass.simpleName} modifyLayout: scale: ($scaleWidth, $scaleHeight), xyc: ($offsetX, $offsetY, $offsetZ)")
    }
}

class SwarmAugmentedImageNode : AugmentedImageNode(1f, 0.6667f, Renderables.swarmRendereable.getNow(null))

class MakeAppAugmentedImageNode : AugmentedImageNode(1f, 0.6667f, Renderables.makeAppRendereable.getNow(null)) {
    override fun initLayout() {
        super.initLayout()

        offsetX = -0.03738f * scaleWidth
        offsetZ = 0.2142f * scaleHeight
    }
}

class HeartAugmentedImageNode : AugmentedImageNode(1f, 0.6667f, Renderables.heartRendereable.getNow(null)) {
    override fun initLayout() {
        super.initLayout()

        offsetX = 0.22299f * scaleWidth
        offsetZ = 0.2142f * scaleHeight
    }
}