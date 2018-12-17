package se.frost.ardemo

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment: Fragment() {

    interface MainFragmentInteractionListener {

        fun onHelloSceneFormButtonClick()
        fun onButton2Click()
        fun onButton3Click()
        fun onButton4Click()
    }

    private var listener: MainFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as? MainFragmentInteractionListener
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun initButtons() {
        helloSceneformButton.setOnClickListener { listener?.onHelloSceneFormButtonClick() }
        arDemoButton2.setOnClickListener { listener?.onButton2Click() }
        arDemoButton3.setOnClickListener { listener?.onButton3Click() }
        arDemoButton4.setOnClickListener { listener?.onButton4Click() }
    }
}