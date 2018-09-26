package com.swarmnyc.arswarm.ar

import android.content.Context
import android.net.Uri
import com.google.ar.sceneform.rendering.ModelRenderable
import java.util.concurrent.CompletableFuture

object Renderables {
    fun init(context: Context) : CompletableFuture<Void> {
        swarmRendereable = ModelRenderable.builder().setSource(context, Uri.parse("swarm.sfb")).build()
        makeAppRendereable = ModelRenderable.builder().setSource(context, Uri.parse("we-make-app.sfb")).build()
        heartRendereable = ModelRenderable.builder().setSource(context, Uri.parse("heart.sfb")).build()

        return CompletableFuture.allOf(swarmRendereable, makeAppRendereable, heartRendereable)
    }

    lateinit var swarmRendereable : CompletableFuture<ModelRenderable>
    lateinit var makeAppRendereable : CompletableFuture<ModelRenderable>
    lateinit var heartRendereable : CompletableFuture<ModelRenderable>
}