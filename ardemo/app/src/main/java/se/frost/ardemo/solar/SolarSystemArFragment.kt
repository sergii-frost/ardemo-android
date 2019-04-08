package se.frost.ardemo.solar

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.GestureDetector
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import se.frost.ardemo.R
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import com.google.ar.core.Anchor
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Vector3
import kotlinx.android.synthetic.main.solar_controls.*


// Astronomical units to meters ratio. Used for positioning the planets of the solar system.
private const val AU_TO_METERS = 0.5f

class SolarSystemArFragment: ArFragment() {

    private val solarSettings = SolarSettings()

    private lateinit var sunRenderable: ModelRenderable
    private lateinit var mercuryRenderable: ModelRenderable
    private lateinit var venusRenderable: ModelRenderable
    private lateinit var earthRenderable: ModelRenderable
    private lateinit var lunaRenderable: ModelRenderable
    private lateinit var marsRenderable: ModelRenderable
    private lateinit var jupiterRenderable: ModelRenderable
    private lateinit var saturnRenderable: ModelRenderable
    private lateinit var uranusRenderable: ModelRenderable
    private lateinit var neptuneRenderable: ModelRenderable
    private lateinit var solarControlsRenderable: ViewRenderable

    // True once scene is loaded
    private var hasFinishedLoading = false
    // True once the scene has been placed.
    private var hasPlacedSolarSystem = false

    //private var loadingMessageSnackbar: Snackbar? = null

    private val gestureDetector by lazy {
        GestureDetector(
            context,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    onSingleTap(e)
                    return true
                }

                override fun onDown(e: MotionEvent): Boolean {
                    return true
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupModels()
        setupScene()
    }

    private fun setupModels() {
        // Build all the planet models.
        val sunStage = ModelRenderable.builder().setSource(context, Uri.parse("Sol.sfb")).build()
        val mercuryStage = ModelRenderable.builder().setSource(context, Uri.parse("Mercury.sfb")).build()
        val venusStage = ModelRenderable.builder().setSource(context, Uri.parse("Venus.sfb")).build()
        val earthStage = ModelRenderable.builder().setSource(context, Uri.parse("Earth.sfb")).build()
        val lunaStage = ModelRenderable.builder().setSource(context, Uri.parse("Luna.sfb")).build()
        val marsStage = ModelRenderable.builder().setSource(context, Uri.parse("Mars.sfb")).build()
        val jupiterStage = ModelRenderable.builder().setSource(context, Uri.parse("Jupiter.sfb")).build()
        val saturnStage = ModelRenderable.builder().setSource(context, Uri.parse("Saturn.sfb")).build()
        val uranusStage = ModelRenderable.builder().setSource(context, Uri.parse("Uranus.sfb")).build()
        val neptuneStage = ModelRenderable.builder().setSource(context, Uri.parse("Neptune.sfb")).build()

        // Build a renderable from a 2D View.
        val solarControlsStage = ViewRenderable.builder().setView(context, R.layout.solar_controls).build()

        CompletableFuture.allOf(
            sunStage,
            mercuryStage,
            venusStage,
            earthStage,
            lunaStage,
            marsStage,
            jupiterStage,
            saturnStage,
            uranusStage,
            neptuneStage,
            solarControlsStage
        ).handle { notUsed, throwable ->
            // When you build a Renderable, Sceneform loads its resources in the background while
            // returning a CompletableFuture. Call handle(), thenAccept(), or check isDone()
            // before calling get().

            if (throwable != null) {
                //show error, interrrupt
            }

            try {
                sunRenderable = sunStage.get()
                mercuryRenderable = mercuryStage.get()
                venusRenderable = venusStage.get()
                earthRenderable = earthStage.get()
                lunaRenderable = lunaStage.get()
                marsRenderable = marsStage.get()
                jupiterRenderable = jupiterStage.get()
                saturnRenderable = saturnStage.get()
                uranusRenderable = uranusStage.get()
                neptuneRenderable = neptuneStage.get()
                solarControlsRenderable = solarControlsStage.get()

                //Everything finished loading successfully.
                hasFinishedLoading = true
            } catch (ex: InterruptedException) {
                //show error
            } catch (ex: ExecutionException) {
                //show error
            }
        }
    }

    private fun setupScene() {
        arSceneView.scene.apply {
            setOnTouchListener { hitTestResult, motionEvent ->
                // If the solar system hasn't been placed yet, detect a tap and then check to see if
                // the tap occurred on an ARCore plane to place the solar system.
                if (!hasPlacedSolarSystem) {
                    return@setOnTouchListener gestureDetector.onTouchEvent(motionEvent)
                }

                // Otherwise return false so that the touch event can propagate to the scene.
                return@setOnTouchListener false
            }

            addOnUpdateListener {
                val frame = arSceneView.arFrame ?: return@addOnUpdateListener
                if (frame.camera.trackingState != TrackingState.TRACKING) {
                    return@addOnUpdateListener
                }

                frame.getUpdatedTrackables(Plane::class.java).forEach { plane ->
                    if (plane.trackingState == TrackingState.TRACKING) {
                        //hideLoadingMessage()
                    }
                }
            }
        }
    }

    private fun onSingleTap(motionEvent: MotionEvent?) {
        if (!hasFinishedLoading) {
            return
        }

        val frame = arSceneView.arFrame ?: return
        if (!hasPlacedSolarSystem && tryPlaceSolarSystem(motionEvent, frame)) {
            hasPlacedSolarSystem = true
        }
    }

    private fun tryPlaceSolarSystem(motionEvent: MotionEvent?, frame: Frame): Boolean {
        if (motionEvent != null && frame.camera.trackingState == TrackingState.TRACKING) {
            frame.hitTest(motionEvent).forEach { hit ->
                if ((hit.trackable as? Plane)?.isPoseInPolygon(hit.hitPose) == true) {
                    //Create an anchor
                    AnchorNode(hit.createAnchor()).apply {
                        setParent(arSceneView.scene)
                        addChild(createSolarSystem())
                    }
                    return true
                }
            }
        }

        return false
    }

    private fun createSolarSystem(): Node {
        val base = Node()
        val sun = createSun(base)
        context?.let {
            createPlanet(it, "Mercury", sun, 0.4f, 47f, mercuryRenderable, 0.019f)
            createPlanet(it, "Venus", sun, 0.7f, 35f, venusRenderable, 0.0475f)
            val earth = createPlanet(it, "Earth", sun, 1.0f, 29f, earthRenderable, 0.05f)
            createPlanet(it, "Moon", earth, 0.15f, 100f, lunaRenderable, 0.018f)
            createPlanet(it,"Mars", sun, 1.5f, 24f, marsRenderable, 0.0265f)
            createPlanet(it,"Jupiter", sun, 2.2f, 13f, jupiterRenderable, 0.16f)
            createPlanet(it,"Saturn", sun, 3.5f, 9f, saturnRenderable, 0.1325f)
            createPlanet(it,"Uranus", sun, 5.2f, 7f, uranusRenderable, 0.1f)
            createPlanet(it, "Neptune", sun, 6.1f, 5f, neptuneRenderable, 0.074f)
        }

        return base
    }

    private fun createSun(base: Node): Node {
        val sun = Node().apply {
            setParent(base)
            localPosition = Vector3(0f, .5f, 0f)
        }

        val sunVisual = Node().apply {
            setParent(sun)
            renderable = sunRenderable
            localScale = Vector3(.5f, .5f, .5f)
        }

        val solarControls = Node().apply {
            setParent(sun)
            renderable = solarControlsRenderable
            localPosition = Vector3(0f, .25f, 0f)
        }

        val solarControlsView = solarControlsRenderable.view
        (solarControlsView.findViewById(R.id.orbitSpeedBar) as? SeekBar)?.apply {
            setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    seekBar?.let {
                        val ratio = progress.toFloat() / it.max.toFloat()
                        solarSettings.orbitSpeedMultiplier = ratio * 10.0f
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            })
        }

        (solarControlsView.findViewById(R.id.rotationSpeedBar) as? SeekBar)?.apply {
            progress = (solarSettings.rotationSpeedMultiplier * 10.0f).toInt()
            setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    seekBar?.let {
                        val ratio = progress.toFloat() / it.max.toFloat()
                        solarSettings.rotationSpeedMultiplier = ratio * 10.0f
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }

        sunVisual.setOnTapListener { _, _ ->
            solarControls.isEnabled = !solarControls.isEnabled
        }

        return sun
    }

    private fun createPlanet(
        context: Context,
        name: String,
        parent: Node,
        auFromParent: Float,
        orbitDegreesPerSecond: Float,
        renderable: ModelRenderable,
        planetScale: Float
    ): Node {
        // Orbit is a rotating node with no renderable positioned at the sun.
        // The planet is positioned relative to the orbit so that it appears to rotate around the sun.
        // This is done instead of making the sun rotate so each planet can orbit at its own speed.
        val orbit = RotatingNode(solarSettings, true).apply {
            this.degreesPerSecond = orbitDegreesPerSecond
            setParent(parent)
        }

        // Create the planet and position it relative to the sun.
        return Planet(context, name, planetScale, renderable, solarSettings).apply {
            setParent(orbit)
            localPosition = Vector3(auFromParent * AU_TO_METERS, .0f, .0f)
        }
    }
}