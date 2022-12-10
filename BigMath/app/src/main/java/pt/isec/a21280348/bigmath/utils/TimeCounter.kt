package pt.isec.a21280348.bigmath.utils

import android.app.Activity
import android.util.Log
import android.widget.TextView

class TimeCounter(tv : TextView): Thread() {
    val myTv : TextView = tv
    var currentTime : Int = TIME
    var inPause : Boolean = false
    override fun run() {
        while(currentTime > 0) {
            if (!inPause) {
                updateTV()
                currentTime -= 1000
            }
            sleep(1000)
        }
    }

    private fun updateTV(){
        myTv?.post{
            myTv.text =(currentTime/1000).toString()
        }
    }

    public fun pause(){
        inPause = true
    }

    public fun unPause(){
        inPause= false
    }


    companion object{
        val TIME = 60000
    }
}