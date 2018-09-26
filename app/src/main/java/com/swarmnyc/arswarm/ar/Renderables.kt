package com.swarmnyc.arswarm.ar

import android.content.Context
import android.net.Uri
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.swarmnyc.arswarm.R
import java.util.concurrent.CompletableFuture

object Renderables {
    fun init(context: Context): CompletableFuture<Void> {
        swarmRendereable = ModelRenderable.builder().setSource(context, Uri.parse("swarm.sfb")).build()
        makeAppRendereable = ModelRenderable.builder().setSource(context, Uri.parse("we-make-app.sfb")).build()
        heartRendereable = ModelRenderable.builder().setSource(context, Uri.parse("heart.sfb")).build()
        wallRenderable = ViewRenderable.builder().setView(context, R.layout.view_wall).build()

        return CompletableFuture.allOf(swarmRendereable, makeAppRendereable, heartRendereable, wallRenderable)
    }

    lateinit var swarmRendereable: CompletableFuture<ModelRenderable>
    lateinit var makeAppRendereable: CompletableFuture<ModelRenderable>
    lateinit var heartRendereable: CompletableFuture<ModelRenderable>
    lateinit var wallRenderable: CompletableFuture<ViewRenderable>
}