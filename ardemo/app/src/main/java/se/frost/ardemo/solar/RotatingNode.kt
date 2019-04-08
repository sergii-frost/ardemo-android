package se.frost.ardemo.solar

import android.animation.ObjectAnimator
import android.view.animation.LinearInterpolator
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.QuaternionEvaluator
import com.google.ar.sceneform.math.Vector3

class RotatingNode(private val settings: SolarSettings, private val isOrbit: Boolean) : Node() {

    private var orbitAnimation: ObjectAnimator? = null

    var lastSpeedMultiplier = 1.0f
    var degreesPerSecond = 90.0f

    override fun onUpdate(p0: FrameTime?) {
        super.onUpdate(p0)

        //Orbit animation hasn't been set up.
        if (orbitAnimation == null) {
            return
        }

        //Check if we need to change the speed of rotation
        val speedMultiplier = getSpeedMultiplier()

        //Nothing has changed, continue
        if (lastSpeedMultiplier == speedMultiplier) {
            return
        }

        if (speedMultiplier == 0.0f) {
            orbitAnimation?.pause()
        } else {
            orbitAnimation?.apply {
                resume()
                val savedAnimatedFraction = animatedFraction
                duration = getAnimationDuration()
                setCurrentFraction(savedAnimatedFraction)
            }
            lastSpeedMultiplier = speedMultiplier
        }
    }

    override fun onActivate() {
        super.onActivate()
        startAnimation()
    }

    override fun onDeactivate() {
        super.onDeactivate()
        stopAnimation()
    }

    private fun startAnimation() {
        if (orbitAnimation != null) {
            return
        }
        orbitAnimation = createAnimator()
        orbitAnimation?.let {
            it.target = this
            it.duration = getAnimationDuration()
            it.start()
        }
    }

    private fun stopAnimation() {
        orbitAnimation?.cancel()
        orbitAnimation = null
    }

    private fun getSpeedMultiplier(): Float {
        return if (isOrbit) settings.orbitSpeedMultiplier else settings.rotationSpeedMultiplier
    }

    private fun getAnimationDuration(): Long {
        return (1000 * 360 / (degreesPerSecond * getSpeedMultiplier())).toLong()
    }

    companion object {
        /** Returns an ObjectAnimator that makes this node rotate. */
        private fun createAnimator(): ObjectAnimator {
            // Node's setLocalRotation method accepts Quaternions as parameters.
            // First, set up orientations that will animate a circle.
            val orientation1 = Quaternion.axisAngle(Vector3(0f, 1f, 0f), 0f)
            val orientation2 = Quaternion.axisAngle(Vector3(0f, 1f, 0f), 120f)
            val orientation3 = Quaternion.axisAngle(Vector3(0f, 1f, 0f), 240f)
            val orientation4 = Quaternion.axisAngle(Vector3(0f, 1f, 0f), 360f)

            return ObjectAnimator().apply {
                setObjectValues(orientation1, orientation2, orientation3, orientation4)
                // Next, give it the localRotation property.
                propertyName = "localOrientation"
                // Use Sceneform's QuaternionEvaluator.
                setEvaluator(QuaternionEvaluator())

                //  Allow orbitAnimation to repeat forever
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.RESTART
                interpolator = LinearInterpolator()
                setAutoCancel(true)
            }
        }
    }
}