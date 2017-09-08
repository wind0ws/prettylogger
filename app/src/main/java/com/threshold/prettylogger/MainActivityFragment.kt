package com.threshold.prettylogger

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.threshold.logger.PrettyLogger
import com.threshold.logger.debug
import com.threshold.logger.tag
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * A placeholder fragment containing a simple view.
 */
class MainActivityFragment : Fragment(), PrettyLogger,View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button.setOnClickListener(this)
        testLog()
    }

    private fun testLog() {
        tag("OnceOnlyTag").info { "MainActivityFragment onViewCreated" }
        tag(loggerTag).debug("loggerTag is a filed of PrettyLogger interface")
    }

    override fun onClick(view: View?) {
        when (view) {
            button -> debug { "Clicked the button" }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        debug("MainActivityFragment onDestroyView")
    }
}
