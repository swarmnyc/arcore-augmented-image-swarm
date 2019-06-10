package com.swarmnyc.arswarm.ar

import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Renderable
import com.swarmnyc.arswarm.utils.Logger
import java.util.concurrent.CompletableFuture

abstract class AugmentedImageNodeGroup : Node() {
    lateinit var anchorNode: AugmentedImageAnchorNode

    fun init(anchorNode: AugmentedImageAnchorNode): AugmentedImageNodeGroup {
        this.anchorNode = anchorNode

        name = this.javaClass.simpleName.replace("AugmentedImageNodeGroup", "")

        setParent(anchorNode)

        onInit()

        return this
    }

    protected abstract fun onInit()

}

abstract class AugmentedImageNode(resource: CompletableFuture<*>? = null) : Node() {
    var scaledWidth: Float = 1f
    var scaledHeight: Float = 1f
    var scaledDeep: Float = 1f
    var offsetX: Float = 0.0f
    var offsetY: Float = 0.0f
    var offsetZ: Float = 0.0f
    lateinit var anchorNode: AugmentedImageAnchorNode

    init {
        if (resource != null) {
            renderable = resource.getNow(null) as? Renderable
        }
    }

    fun init(anchorNode: AugmentedImageAnchorNode, group: AugmentedImageNodeGroup? = null): AugmentedImageNode {
        this.anchorNode = anchorNode
        name = this.javaClass.simpleName.replace("AugmentedImageNode", "")

        setParent(group ?: anchorNode)

        initLayout()

        modifyLayout()

        return this
    }

    open fun initLayout() {
        scaledWidth = anchorNode.scaledWidth
        scaledHeight = anchorNode.scaledHeight
        scaledDeep = anchorNode.scaledWidth
    }

    fun modifyLayout(config: AugmentedImageNode.() -> Unit) {
        config()

        modifyLayout()
    }

    open fun modifyLayout() {
        localScale = Vector3(scaledWidth, scaledHeight, scaledDeep)
        localPosition = Vector3(offsetX, offsetY, offsetZ)

        Logger.d("${javaClass.simpleName} modifyLayout: scale: ($scaledWidth, $scaledHeight, $scaledDeep), xyz: ($offsetX, $offsetY, $offsetZ)")
    }
}