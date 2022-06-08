package com.example.pigeneratorhandler

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_time_counter.*

class FragmentTimeCounter : Fragment() {

    private var messageListener: SendMessageListener? = null
    private var countColor = 0
    private var seconds = 0
    private var handler: Handler? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_time_counter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTimer(getString(R.string.start_time))


        buttonStart?.setOnClickListener {
            messageListener?.onMessageDetails(getString(R.string.message_start))
            runTimer()
            buttonStart.isClickable = false
            buttonPause.isClickable = true
            buttonRestart.isClickable = true
        }
        buttonPause?.setOnClickListener {
            messageListener?.onMessageDetails(getString(R.string.message_pause))
            buttonStart.isClickable = true
            buttonPause.isClickable = false
            buttonRestart.isClickable = true
            handler?.removeCallbacksAndMessages(null)
        }
        buttonRestart?.setOnClickListener {
            messageListener?.onMessageDetails(getString(R.string.message_restart))
            seconds = 0
            buttonPause.performClick()
            buttonStart.performClick()
            buttonStart.isClickable = true
            buttonPause.isClickable = true
            buttonRestart.isClickable = false
        }


        runTimer()
        buttonStart.performClick()
    }


    private fun setTimer(time: String) {
        textViewTimer?.text = time
    }

    private fun runTimer() {
        handler = Handler(Looper.getMainLooper())
        handler?.post(object : Runnable {
            override fun run() {
                val secondsTimer = seconds % 60
                val minutesTimer = seconds / 60

                if (countColor == 20) {
                    backgroundCount?.setBackgroundColor(
                        Color.argb(
                            255, (0..255).random(), (0..255).random(), (0..255).random()
                        )
                    )
                    countColor = 0
                } else {
                    countColor++
                }
                val time = String.format(getString(R.string.time_format), minutesTimer, secondsTimer)

                setTimer(time)

                seconds++
                handler?.postDelayed(this, 1000)
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SendMessageListener) {
            messageListener = context
        } else {
            throw RuntimeException("$context must implement listener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        messageListener = null
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(SECONDS_KEY, seconds)
        outState.putInt(COUNT_KEY, countColor)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        seconds = savedInstanceState?.getInt(SECONDS_KEY) ?: 0
        countColor = savedInstanceState?.getInt(COUNT_KEY) ?: 0
        super.onViewStateRestored(savedInstanceState)
    }

    interface SendMessageListener {
        fun onMessageDetails(message: String)
    }

    companion object{
        const val SECONDS_KEY = "seconds"
        const val COUNT_KEY = "count"
    }
}