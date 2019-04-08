package se.frost.ardemo.solar

import android.content.Context
import android.view.MotionEvent
import android.widget.TextView
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import se.frost.ardemo.R

private const val INFO_CARD_Y_POS_COEFF = 0.55f

class Planet(
    private val context: Context,
    private val planetName: String,
    private val planetScale: Float,
    private val planetRenderable: ModelRenderable,
    private val settings: SolarSettings
) : Node(), Node.OnTapListener {

    private val infoCard by lazy {
        val card = Node()
        card.let {
            it.setParent(this)
            it.isEnabled = false
            it.localPosition = Vector3(0.0f, planetScale * INFO_CARD_Y_POS_COEFF, 0.0f)
        }

        ViewRenderable.builder()
            .setView(context, R.layout.planet_card_view)
            .build()
            .thenAccept {
                card.renderable = it
                (it.view as? TextView)?.text = planetName
            }
            .exceptionally { throwable ->
                throw AssertionError("Could not load planet card view", throwable)
            }

        card
    }

    private val planetVisual by lazy {
         RotatingNode(settings, false).let {
             it.setParent(this)
             it.renderable = planetRenderable
             it.localScale = Vector3(planetScale, planetScale, planetScale)
        }
    }

    init {
        setOnTapListener(this)
    }

    override fun onActivate() {
        super.onActivate()
        infoCard
        planetVisual
    }

    override fun onTap(p0: HitTestResult?, p1: MotionEvent?) {
        infoCard.isEnabled = !infoCard.isEnabled
    }

    override fun onUpdate(p0: FrameTime?) {
        super.onUpdate(p0)

        // Typically, getScene() will never return null because onUpdate() is only called when the node
        // is in the scene.
        // However, if onUpdate is called explicitly or if the node is removed from the scene on a
        // different thread during onUpdate, then getScene may be null.
        if (scene == null) {
            return
        }

        val cameraPosition = scene.camera.worldPosition
        val cardPosition = infoCard.worldPosition
        val direction = Vector3.subtract(cameraPosition, cardPosition)
        val lookRotation = Quaternion.lookRotation(direction, Vector3.up())
        infoCard.worldRotation = lookRotation
    }
}