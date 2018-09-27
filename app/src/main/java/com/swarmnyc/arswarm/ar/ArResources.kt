package com.swarmnyc.arswarm.ar

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ExternalTexture
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.swarmnyc.arswarm.R
import java.util.concurrent.CompletableFuture

object ArResources {
    fun init(context: Context): CompletableFuture<Void> {
        swarmRendereable = ModelRenderable.builder().setSource(context, Uri.parse("swarm.sfb")).build()
        makeAppRendereable = ModelRenderable.builder().setSource(context, Uri.parse("we-make-app.sfb")).build()
        heartRendereable = ModelRenderable.builder().setSource(context, Uri.parse("heart.sfb")).build()
        wallRenderable = ViewRenderable.builder().setView(context, R.layout.view_wall)
                .setVerticalAlignment(ViewRenderable.VerticalAlignment.CENTER)
                .build()

        hintSwipeRenderable = ViewRenderable.builder().setView(context, R.layout.view_hint_swipe)
                .setVerticalAlignment(ViewRenderable.VerticalAlignment.TOP)
                .build()


        val texture = ExternalTexture()
        videoPlayer = MediaPlayer.create(context, R.raw.video)
        videoPlayer.setSurface(texture.surface)
        videoPlayer.isLooping = true

        videoRenderable = ModelRenderable.builder().setSource(context, com.google.ar.sceneform.rendering.R.raw.sceneform_view_renderable).build().also {
            it.thenAccept { renderable ->
                renderable.material.setExternalTexture("viewTexture", texture)
            }
        }

        webImageRenderable = ViewRenderable.builder().setView(context, R.layout.view_web_image)
                .setVerticalAlignment(ViewRenderable.VerticalAlignment.CENTER)
                .build()

        visitRenderable = ViewRenderable.builder().setView(context, R.layout.view_visit)
                .setVerticalAlignment(ViewRenderable.VerticalAlignment.TOP)
                .build()

        return CompletableFuture.allOf(swarmRendereable,
                makeAppRendereable,
                heartRendereable,
                wallRenderable,
                hintSwipeRenderable,
                videoRenderable,
                webImageRenderable
        )
    }

    lateinit var videoPlayer: MediaPlayer

    lateinit var swarmRendereable: CompletableFuture<ModelRenderable>
    lateinit var makeAppRendereable: CompletableFuture<ModelRenderable>
    lateinit var heartRendereable: CompletableFuture<ModelRenderable>
    lateinit var wallRenderable: CompletableFuture<ViewRenderable>
    lateinit var videoRenderable: CompletableFuture<ModelRenderable>
    lateinit var webImageRenderable: CompletableFuture<ViewRenderable>

    lateinit var hintSwipeRenderable: CompletableFuture<ViewRenderable>
    lateinit var visitRenderable: CompletableFuture<ViewRenderable>


    val viewRenderableRotation = Quaternion(Vector3(1f, 0f, 0f), -90f)
}