package se.frost.ardemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.ar.sceneform.ux.ArFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initArFragment()
    }

    override fun onResume() {
        super.onResume()
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            CameraPermissionHelper.requestCameraPermission(this)
            return
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(this, R.string.camera_permission, Toast.LENGTH_LONG).show()
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(this)
            }
            finish()
        }
    }

    private fun initArFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.contentFrame, ArFragment())
            .commit()
    }
}
