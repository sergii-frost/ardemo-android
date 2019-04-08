package se.frost.ardemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.Toast
import se.frost.ardemo.hellosceneform.HelloSceneformFragment
import se.frost.ardemo.solar.SolarSystemArFragment

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

    override fun onHelloSceneFormButtonClick() {
        addFragment(HelloSceneformFragment())
    }

    override fun onButton2Click() {
        addFragment(SolarSystemArFragment())
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
        replaceFragment(MainFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.contentFrame, fragment)
            .commit()
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.contentFrame, fragment)
            .addToBackStack(fragment.javaClass.name)
            .commit()
    }


    private fun showToast(message: String) {
        Toast.makeText(this, "Button clicked: $message", Toast.LENGTH_SHORT).show()
    }
}
