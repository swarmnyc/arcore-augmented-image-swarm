package com.swarmnyc.arswarm

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import java.util.concurrent.CompletableFuture


class MainActivity : AppCompatActivity() {
    private lateinit var arFragment: ArFragment
    private val trackableMap = mutableMapOf<String, AnchorNode>()
    private var selectNode: AugmentedImageNode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment

        arFragment.arSceneView.scene.apply {
            addOnUpdateListener(::onUpdateFrame)
            setOnTouchListener(::handleTouch)
        }

        debugInit()
    }

    private fun onUpdateFrame(frameTime: FrameTime?) {
        val frame = arFragment.arSceneView.arFrame

        // If there is no frame or ARCore is not tracking yet, just return.
        if (frame == null || frame.camera.trackingState != TrackingState.TRACKING) {
            return
        }

//        Logger.d("frame updated")
        frame.getUpdatedTrackables(AugmentedImage::class.java).forEach { image ->

            when (image.trackingState) {
                TrackingState.TRACKING -> if (!trackableMap.contains(image.name)) {
                    createArNode(image)
                }
                TrackingState.STOPPED -> trackableMap.remove(image.name)
                else -> {
                }
            }
        }
    }

    private fun handleTouch(hitTestResult: HitTestResult, event: MotionEvent): Boolean {
        Logger.d("touched: ${hitTestResult.node}")

        hitTestResult.node?.also {
            if (it.parent is AugmentedImageNode) {
                selectNode = it.parent as AugmentedImageNode
            }
        }

        return true
    }

    private fun createArNode(image: AugmentedImage) {
        Logger.d("createArNode: ${image.name}(${image.index}), state: ${image.trackingState}, pose: ${image.centerPose}, ex: ${image.extentX}, ez: ${image.extentZ}")

        when (image.name) {
            "swarm" -> {
                val node = SwarmAugmentedImageNode(this)
                node.init(image)
                trackableMap[image.name] = node
                arFragment.arSceneView.scene.addChild(node)

                Toast.makeText(this, "add swarm", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun debugInit() {
        if (BuildConfig.DEBUG) {
            findViewById<View>(R.id.add_x).setOnClickListener {
                selectNode?.update {
                    offsetX += 0.01f
                }
            }
            findViewById<View>(R.id.add_y).setOnClickListener {
                selectNode?.update {
                    offsetY += 0.01f
                }
            }
            findViewById<View>(R.id.add_z).setOnClickListener {
                selectNode?.update {
                    offsetZ += 0.01f
                }
            }
            findViewById<View>(R.id.minus_x).setOnClickListener {
                selectNode?.update {
                    offsetX -= 0.01f
                }
            }
            findViewById<View>(R.id.minus_y).setOnClickListener {
                selectNode?.update {
                    offsetY -= 0.01f
                }
            }
            findViewById<View>(R.id.minus_z).setOnClickListener {
                selectNode?.update {
                    offsetZ -= 0.01f
                }
            }

            findViewById<View>(R.id.scale_up).setOnClickListener {
                selectNode?.update {
                    offsetScale += 0.01f
                }
            }

            findViewById<View>(R.id.scale_down).setOnClickListener {
                selectNode?.update {
                    offsetScale -= 0.01f
                }
            }
        } else {
            findViewById<View>(R.id.debug_panel).visibility = View.GONE
        }
    }
}

class SwarmArFragment : ArFragment() {
    companion object {
        private val ArImages = mapOf("swarm" to "swarm.png")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        // Turn off the plane discovery since we're only looking for ArImages
        planeDiscoveryController.hide()
        planeDiscoveryController.setInstructionView(null)
        arSceneView.planeRenderer.isEnabled = false

        return view
    }

    override fun getSessionConfiguration(session: Session): Config {
        val config = super.getSessionConfiguration(session)
        config.focusMode = Config.FocusMode.AUTO
        // add swarm img
        val db = AugmentedImageDatabase(session)

        ArImages.forEach {
            db.addImage(it.key, BitmapFactory.decodeStream(context!!.resources.assets.open(it.value)))
        }

        config.augmentedImageDatabase = db

        Logger.d("augmented images add")

        return config
    }
}

abstract class AugmentedImageNode(context: Context) : AnchorNode() {
    var offsetX: Float = 0.0f
    var offsetY: Float = 0.0f
    var offsetZ: Float = 0.0f
    var baseEx: Float = 1f
    var baseEz: Float = 1f

    var offsetScale: Float = 1f

    protected var image: AugmentedImage? = null


    open fun init(image: AugmentedImage) {
        this.image = image
    }

    fun update(initUpdate: AugmentedImageNode.() -> Unit) {
        this.initUpdate()
        this.updateInternal()
    }

    protected open fun updateInternal() {}
}

class SwarmAugmentedImageNode(context: Context) : AugmentedImageNode(context) {
    companion object {
        private var wordRendereable: CompletableFuture<ModelRenderable>? = null
    }

    init {
        if (wordRendereable == null) {
            wordRendereable = ModelRenderable.builder().setSource(context, Uri.parse("swarm.sfb")).build()
        }

        baseEx = 0.24f
        baseEz = 0.16f
        offsetZ = -0.01f
        offsetScale = 0.3f
    }

    lateinit var workNode: Node


    override fun init(image: AugmentedImage) {
        super.init(image)

        if (!wordRendereable!!.isDone) {
            CompletableFuture.allOf(wordRendereable)
                    .thenAccept { _ -> init(image) }
                    .exceptionally { throwable ->
                        Logger.e("Exception loading", throwable)
                        null
                    }
            return
        }

        Logger.d("Set image")

        // Set the anchor based on the center of the image.
        anchor = image.createAnchor(image.centerPose)

        workNode = Node()
        workNode.setParent(this)
        updateInternal()
        offsetScale = offsetScale * image.extentX / baseEx
        workNode.renderable = wordRendereable!!.getNow(null)
    }


    override fun updateInternal() {
        workNode.localScale = Vector3(offsetScale, offsetScale, offsetScale)
        workNode.localPosition = Vector3(offsetX, offsetY, offsetZ)

        Logger.d("updateInternal: offset: ($offsetX, $offsetY, $offsetZ), scale: $offsetScale")
    }
}
