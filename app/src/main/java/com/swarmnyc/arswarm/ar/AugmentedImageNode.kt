package com.swarmnyc.arswarm.ar

import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Renderable
import com.swarmnyc.arswarm.utils.Logger
import java.util.concurrent.CompletableFuture

abstract class AugmentedImageNode(resource: CompletableFuture<*>) : Node() {
    var scaledWidth: Float = 1f
    var scaledHeight: Float = 1f
    var scaledDeep: Float = 1f
    var offsetX: Float = 0.0f
    var offsetY: Float = 0.0f
    var offsetZ: Float = 0.0f
    lateinit var host: AugmentedImageAnchorNode

    init {
        renderable = resource.getNow(null) as? Renderable
    }

    fun init(node: AugmentedImageAnchorNode) {
        host = node
        name = this.javaClass.simpleName.replace("AugmentedImageNode", "")

        setParent(node)

        initLayout()

        modifyLayout()
    }

    open fun initLayout() {
        scaledWidth = host.scaledWidth
        scaledHeight = host.scaledHeight
        scaledDeep = host.scaledWidth
    }

    fun modifyLayout(config: (AugmentedImageNode.() -> Unit)? = null) {
        if (config != null) config()

        localScale = Vector3(scaledWidth, scaledWidth, scaledDeep)
        localPosition = Vector3(offsetX, offsetY, offsetZ)

        Logger.d("${javaClass.simpleName} modifyLayout: scale: ($scaledWidth, $scaledHeight, $scaledDeep), xyc: ($offsetX, $offsetY, $offsetZ)")
    }
}