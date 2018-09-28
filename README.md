# AR SWARM

We made this Android app to test Augmented Image of Google ARCore Android. 

### run the app and scan this image to see its 3D version
![logo](/app/models/images/swarm.png)

### or see the demo
![screenshot](https://s3.amazonaws.com/swarm-dev/ar-swarm-demo.gif)

### build
In order to build the project, you have to download a mp4 video and put it on `./app/src/main/res/raw/` and name it `video.mp4`.
You can use ours [video](https://s3.amazonaws.com/swarm-website-uploads/SWARM+MAIN+VIDEO.mp4)

# Tutorial
This project used [ARCore](https://developers.google.com/ar/) library provided by Google to generate an augmented reality experiences app. The app can recognize our computer logo and plane 3D models and views on top of our logo and animate or switch them. These stuffs all provided by ARCore. ARCore provides very high level APIs that let you generate augmented reality experiences without knowing or touching any 3D Computer Graphics. The fact that in this project we haven't call any graphical APIs like OpenGL to complete the demo.

The main features of ARCore we used in this projects are
- [Augmented Image](https://developers.google.com/ar/develop/java/augmented-images/)
- [Anchors](https://developers.google.com/ar/develop/developer-guides/anchors)

**Augmented Image** can recognize real world objects via cameras by the reference images you provided. In this project is our company logo. Once ARCore finds objects matched referred images. It gives a [pose](https://developers.google.com/ar/reference/java/com/google/ar/core/Pose), world coordinate space, for each objects. Then we can use these poses to create **Anchors** and place models or views based on an anchor. ARCore will take care all stuffs like motion tracking and transforming for these models views.

![demo](/images/1-demo.png) <br/>In this demo we only placed models and views with the anchor. ARCore tracked my motion and changed the perspectives of the virtual world.

## Step 1: Prepare reference images
You can use any tools to generate these reference images and use [arcoreimg tool](https://developers.google.com/ar/develop/java/augmented-images/arcoreimg) to evaluate these images the quality scores. The highest score is 100. As higher score as ARCore can have higher change to recognize the objects. This is a hard step for our. Our logo scored 0 originally, so we adjusts our logo many times to gain higher score.

![compare logos](/images/2-compare.png)<br/> we have tried many adjustments such as changed colors, changed to wireframe and cropping. We found change the words size gain the highest score.

[Download](https://github.com/google-ar/arcore-android-sdk/tree/master/tools/arcoreimg) the arcoreimg tool.

**Commands**
``` bash
# evaluate image score
arcoreimg eval-img --input_image_path=/path/to/image

# build image database
arcoreimg build-db --input_images_directory=/path/to/images --output_db_path=/path/to/myimages.imgdb
```

ARCore can load images and build database real-time or load pre-build database, we recommend you use pre-build database because let app open faster. 

## Step 2: Prepare 3D models
You can use any tools to draw 3D models and export to ARCore supports format such as obj, fbx, or gltf. In this project, we drew our logo in Adobe Illustrator because it can export SVG files by assets. Then we use Blender to import these svg files and convert them to 3D models. You can [google blender svg to 3d](https://www.google.com/search?q=blender+svg+to+3d) to find tutorials.

The most important thing is be aware of the sizes of models. ARCore will try to understand your environment and estimate the sizes of real world objects. Therefore, if you want to place ideal sized models, you have scale them right. There were how we did for making the sizes of 3D models right.

1. make the size of the image to 100 cm x 66 cm because the unit of ARCore is meter, so it let us calculate sizes more easier.

![logo size](/images/3-size-logo.png)

2. make the sizes of 3D models matched to the sizes of elements of the logo.

![word size](/images/4-size-word.png)

![model size](/images/5-size-model.png)

For example, our logo is 1 meter x 0.66 meter, but ARCore detects the image is 0.25 meter x 0.165 meter. So, we have to scale the models and views to 0.25, so they can be the same sizes of the elements in the image.

> In our case, when we exported SVG files from Adobe Illustrator and imported them in Blender. The size is 1.25x smaller, so we have to apply scale for 1.25 to make the sizes right.


## Step 3 : Create an Android app project
To run ARCore, it require the app's target SDK API higher than 24, so create a new project and select `API 24: Android 7.0 (Nougat)`. And add these lines to gradle files

``` gradle
// in ./build.gradle 
buildscript {
    dependencies {
        // add this plugin for generating sfa 
        classpath "com.google.ar.sceneform:plugin:1.4.0"
    }
}
```

``` gradle
// in ./app/build.gradle
android {
    defaultConfig {
        ndk {
            /*
             * Sceneform is available for the following ABIs: arm64-v8a, armv7a,
             * x86_64 and x86. This sample app enables arm64-v8a to run on
             * devices and x86 to run on the emulator. Your application should
             * list the ABIs most appropriate to minimize APK size (arm64-v8a recommended).
             */
            abiFilters 'arm64-v8a', 'x86'
        }
    }
}

dependencies {
    // add there dependencies
    implementation "com.google.ar.sceneform:core:1.4.0"
    implementation "com.google.ar.sceneform.ux:sceneform-ux:1.4.0"
}

 // add this plugin for generating sfa 
apply plugin: "com.google.ar.sceneform.plugin"
```

add these lines in AndroidManifest.xml

``` xml
<?xml version="1.0" encoding="utf-8"?>
<manifest>
    <!-- the app require camera permission -->
    <uses-permission android:name="android.permission.CAMERA" />

    <!-- This tag indicates that this application requires ARCore. This results in the application
        only being visible in the Google Play Store on devices that support ARCore. -->
    <uses-feature
        android:name="android.hardware.camera.ar"
        android:required="true" />

    <application>

        <!-- This tag indicates that this application requires ARCore. This results in the application
        only being visible in the Google Play Store on devices that support ARCore. -->
        <meta-data
            android:name="com.google.ar.core"
            android:value="required" />
    </application>

</manifest>
```

Then the app is ARCore ready now.

## Step 4: Import 3D Models into the project
You can use [Google Sceneform Tools](https://developers.google.com/ar/develop/java/sceneform/import-assets) to import the models or just simply type these lines in the end of `./app/build.gradle`. For example,

``` gradle
sceneform.asset('models/modelA.obj',
        'default',
        'models/modelA.sfa',
        'src/main/assets/modelA')

sceneform.asset('models/modelB.obj',
        'default',
        'models/modelB',
        'src/main/assets/modelB')
```

once your add there lines, the plug in loads the models and generate *.sfb(Sceneform binary) files in the assets folder.

## Step 5: Create an ArFragment and load image database
Create a fragment and extended from ArFragment
``` kotlin
class YourArFragment : ArFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        // Turn off the plane discovery since we're only looking for ArImages
        planeDiscoveryController.hide()
        planeDiscoveryController.setInstructionView(null)
        arSceneView.planeRenderer.isEnabled = false
        arSceneView.scene.setOnTouchListener(::onTouchEvent)
        arSceneView.scene.addOnUpdateListener(::onUpdateFrame)

        return view
    }

    override fun getSessionConfiguration(session: Session): Config {
        val config = super.getSessionConfiguration(session)
        config.focusMode = Config.FocusMode.AUTO // make camera auto focus

        // load pre-build image database
        config.augmentedImageDatabase = AugmentedImageDatabase.deserialize(session, context!!.resources.assets.open("your.imgdb"))

        // or you can build image database real time
        // val db = AugmentedImageDatabase(session)
        // db.addImage("key", BitmapFactory.decodeStream( context!!.resources.assets.open("your.png")))
        // config.augmentedImageDatabase = db

        return config
    }

    private fun onUpdateFrame(frameTime: FrameTime?){
        // we will add anchor here later
    }

    private fun onTouchEvent(hitTestResult: HitTestResult, motionEvent: MotionEvent){
        // we will add touch event for swipe here later
    }
}
```

Add this fragment in `activity_main.xml`
``` xml
<AnyLayout>
    <fragment android:name="your.package.name.YourArFragment"
        android:id="@+id/ar_fragment"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</AnyLayout>
```

## Step 6: Load 3D Models and Views
ARCore supports two types of renderables

- Model Renderable: the 3D model in sfb format
- View Renderable: regular Android Layout

``` kotlin
class YourArFragment : ArFragment() {
    companion object {
        val modelARendereable: CompletableFuture<ModelRenderable>  
            = ModelRenderable.builder().setSource(context, Uri.parse("modelA.sfb")).build()
        val viewARendereable: CompletableFuture<ViewRenderable> 
            = ViewRenderable.builder().setView(context, R.layout.view_a).build()
    }
}
```

Because loading models and views is asynchronous. so you have to .isDone to change the state before .get it or use .thenAccept to get value is it is loaded.

## Step 7: Handle AugmentedImages and add AnchorNodes

``` kotlin
class YourArFragment : ArFragment() {
    // to keep tracking which trackable that we have created AnchorNode with it or not.
    private val trackableMap = mutableMapOf<String, AnchorNode>()

    private fun onUpdateFrame(frameTime: FrameTime?){
        val frame = arSceneView.arFrame

        // If there is no frame or ARCore is not tracking yet, just return.
        if (frame == null || frame.camera.trackingState != TrackingState.TRACKING) {
            return
        }

        // get detected AugmentedImage
        frame.getUpdatedTrackables(AugmentedImage::class.java).forEach { image ->
            when (image.trackingState) {
                // if it is in tracking state and we didn't add AnchorNode, then add one
                TrackingState.TRACKING -> if (!trackableMap.contains(image.name)) {
                    createAnchorNode(image)
                }
                TrackingState.STOPPED -> {
                    // remove it
                    trackableMap.remove(image.name)
                }
                else -> {
                }
            }
        }
    }

    private fun createAnchorNode(image: AugmentedImage) {
        val an = when (image.name) {
            "objectA" -> createObjectA(image)
        }

        if (an != null){
            // add the AnchorNode to the scene
            arSceneView.scene.addChild(node)

            // keep the node
            trackableMap[image.name] = an
        }
    }

    val objectAWidth: Float // the real width of model A
    val objectAHeight: Float // the real height of model A

    private fun createObjectA(image: AugmentedImage) : AnchorNode {
        val anchorNode = AnchorNode()
                
        // make anchor in the center of the images
        anchorNode.anchor = image.createAnchor(image.centerPose)
        
        var arWidth = image.extentX // extentX is estimated width
        var arHeight = image.extentZ // extentZ is estimated height

        var scaledSize = objectAWidth / arWidth

        val modelA = Node()

        // scale to right size
        modelA.localScale = Vector3(scaledSize, scaledSize, scaledSize)

        // model is in the center of the image, change it if you want to put on other places
        var offsetToTop = -arHeight / 2
        var offsetToLeft = -arWidth / 2
        modelA.localPosition = Vector3(offsetToLeft, 0f, offsetToTop)

        // load the model
        modelARendereable.thenAccept {
            modelA.renderable = it
        }

        modelA.setParent(anchorNode)


        val viewA = Node()
        viewA.localScale = Vector3(scaledSize, scaledSize, scaledSize)
        // view is flat, so have to make it vertical.
        viewA.localRotation = Quaternion(Vector3(1f, 0f, 0f), -90f)

        // load the model
        viewARendereable.thenAccept {
            viewA.renderable = it
        }

        viewA.setParent(anchorNode)

        return anchorNode
    }
}
```

## Add animation
Adding animation can be the same as regular Android apps by using [ObjectAnimator](https://developer.android.com/guide/topics/graphics/prop-animation), For example, this code make a model horizontally 

``` kotlin
// 
val orientation1 = Quaternion.axisAngle(Vector3(0.0f, 0.0f, 1.0f), 0f)
val orientation2 = Quaternion.axisAngle(Vector3(0.0f, 0.0f, 1.0f), 120f)
val orientation3 = Quaternion.axisAngle(Vector3(0.0f, 0.0f, 1.0f), 240f)
val orientation4 = Quaternion.axisAngle(Vector3(0.0f, 0.0f, 1.0f), 360f)

val animation = ObjectAnimator()
animation.setObjectValues(orientation1, orientation2, orientation3, orientation4)
animation.propertyName = "localRotation"
animation.setEvaluator(QuaternionEvaluator())
animation.repeatCount = ObjectAnimator.INFINITE
animation.repeatMode = ObjectAnimator.RESTART
animation.interpolator = LinearInterpolator()
animation.setAutoCancel(true)
animation.duration = 1000
animation.target = modelA
animation.start()
```

## Add video
ARCore doesn't allow us using `VideoView` in ViewRenderable, but it can make video's surface as an texture of a material of a renderable. For example,

``` kotlin
val texture = ExternalTexture()
val videoPlayer = MediaPlayer.create(context, R.raw.video)
videoPlayer.setSurface(texture.surface)
videoPlayer.isLooping = true

// We use ARCore's Model and it has defined viewTexture parameter in its material
val videoRenderable = ModelRenderable.builder()
                                     .setSource(context, com.google.ar.sceneform.rendering.R.raw.sceneform_view_renderable)
                                     .build()
            

videoRenderable.thenAccept { renderable ->
    renderable.material.setExternalTexture("viewTexture", texture)

    modelA.renderable = renderable
    videoPlayer.start()
}
```

Enjoy ARCore, SWARM Developer Team