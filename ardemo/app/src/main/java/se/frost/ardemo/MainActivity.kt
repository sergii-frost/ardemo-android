package se.frost.ardemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class MainActivity : AppCompatActivity(), MainFragment.MainFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initMainFragment()
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

    ////
    //// MainFragmentInteractionListener
    ////

    override fun onButton1Click() {
        showToast("1")
    }

    override fun onButton2Click() {
        showToast("2")
    }

    override fun onButton3Click() {
        showToast("3")
    }

    override fun onButton4Click() {
        showToast("4")
    }

    ////
    //// Private methods
    ////

    private fun initMainFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.contentFrame, MainFragment())
            .commit()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, "Button clicked: $message", Toast.LENGTH_SHORT).show()
    }
}
