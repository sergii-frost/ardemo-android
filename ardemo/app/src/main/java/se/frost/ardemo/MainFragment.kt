package se.frost.ardemo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
    }

    private fun initButtons() {
        arDemoButton1.setOnClickListener { showToast(arDemoButton1.text.toString()) }
        arDemoButton2.setOnClickListener { showToast(arDemoButton2.text.toString()) }
        arDemoButton3.setOnClickListener { showToast(arDemoButton3.text.toString()) }
        arDemoButton4.setOnClickListener { showToast(arDemoButton4.text.toString()) }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, "Button clicked: $message", Toast.LENGTH_SHORT).show()
    }
}