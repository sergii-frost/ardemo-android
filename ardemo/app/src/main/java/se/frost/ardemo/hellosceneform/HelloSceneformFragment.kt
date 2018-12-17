package se.frost.ardemo.hellosceneform

import android.os.Bundle
import android.view.View
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import se.frost.ardemo.R

public class HelloSceneformFragment: ArFragment() {

    private var andyRenderable: ModelRenderable? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAndy()
        initOnTapListener()
    }

    private fun initAndy() {
        ModelRenderable.builder()
            .setSource(context, R.raw.andy)
            .build()
            .thenAccept { andyRenderable = it }
    }

    private fun initOnTapListener() {
        setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            andyRenderable.let {
                val anchor = hitResult.createAnchor()
                val anchorNode = AnchorNode(anchor)
                anchorNode.setParent(this.arSceneView.scene)

                val andy = TransformableNode(transformationSystem)
                andy.setParent(anchorNode)
                andy.renderable = it
                andy.select()
            }
        }
    }
}