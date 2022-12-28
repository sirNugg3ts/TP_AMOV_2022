package pt.isec.a21280348.bigmath

import android.content.ClipData.Item
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.get
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pt.isec.a21280348.bigmath.databinding.ActivityGameTableBinding
import pt.isec.a21280348.bigmath.utils.TableSupporter.Companion.generateTable
import kotlin.concurrent.thread


class MyViewModel : ViewModel(){
    var table :  MutableList<Any>  = mutableListOf(20)
    var _levelLive : MutableLiveData<Int> = MutableLiveData<Int>().apply { value = 1 }
    var _timeLeftLive : MutableLiveData<Int> = MutableLiveData<Int>().apply { value = GameTableActivity.GAMETIME }
    var score : Int = 0
    var phase : Int = 1
    lateinit var levelLive : LiveData<Int>
    lateinit var timeLeftLive : LiveData<Int>
}

class GameTableActivity : AppCompatActivity() {
    data class GameInfo(var currentScore : Int,var inTurn : Boolean)
    val model : MyViewModel by viewModels()
    private lateinit var binding : ActivityGameTableBinding
    private var _levelLive : MutableLiveData<Int> = MutableLiveData<Int>().apply { value = 1 }
    private var _timeLeftLive : MutableLiveData<Int> = MutableLiveData<Int>().apply { value = GAMETIME }
    private lateinit var menuItem : MenuItem
    private lateinit var timeThread : Thread
    var threadStop : Boolean = false


    private var levelLive : LiveData<Int>
        get() = _levelLive
        set(value) {}

    private var timeLeftLive : LiveData<Int>
        get() = _timeLeftLive
        set(value) {}

    lateinit var gameTable : GameTable
    var info : GameInfo = GameInfo(0,false)
    var paused : Boolean = false
    var firstObserved : Boolean = true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameTableBinding.inflate(layoutInflater)
        setContentView(binding.root)


        gameTable = GameTable(this)
        gameTable.setActivityBinding(binding)
        gameTable.setGameInfo(info)
        gameTable.setLiveData(_levelLive,_timeLeftLive)
        binding.gameTableId.addView(gameTable)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Math Game"


        binding.btnPause.setOnClickListener {
            if (info.inTurn) {
                paused = !paused
                if (paused) {
                    if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                        binding.btnPause.setImageResource(R.drawable.ic_baseline_play_arrow_42)
                    else
                        binding.btnPause.setImageResource(R.drawable.ic_baseline_play_arrow_14)
                } else {
                    if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                        binding.btnPause.setImageResource(R.drawable.ic_baseline_pause_42)
                    else
                        binding.btnPause.setImageResource(R.drawable.ic_baseline_pause_14   )

                }
            }
        }

        timeThread = Thread{
            Log.i("THREAD","Thread iniciado")
            while(_timeLeftLive.value!! > 0) {
                Log.i("THREAD",_timeLeftLive.value!!.toString())
                if(info.inTurn){
                }
                else if (!paused) {
                    _timeLeftLive.postValue( (_timeLeftLive.value!! - 1))
                }
                Thread.sleep(1000)
                if(threadStop)
                    break
            }
            if(!threadStop) {
                val intent = Intent(this, ScoreboardActivity::class.java)
                intent.putExtra("score", gameTable.getFinalScore())
                startActivity(intent)
            }
        }


        timeThread.start()

    }

    override fun onStart() {
        super.onStart()
        Log.i("Start","onStart")

        gameTable.gameStart()
        registLevelObserver()
        registTimeObserver()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater : MenuInflater = menuInflater
        Log.i("MENU","MENU CRIADO")
        inflater.inflate(R.menu.table_menu,menu)
        menuItem = menu[0]
        menuItem.title = "Level: " + _levelLive.value.toString()
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i("SAVE","WILL SAVE")
        model.table = gameTable.getGameTable()
        model._levelLive = _levelLive
        model._timeLeftLive = _timeLeftLive
        model.timeLeftLive = timeLeftLive
        model.levelLive = levelLive
        model.score = gameTable.getFinalScore()
        model.phase = gameTable.getPhase()
        threadStop = true

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.i("RESTORE","WILL RESTORE")
        Log.i("RESTORE",model.table.toString())
        //menuItem.title = "Level: " + _levelLive.value.toString()
        _levelLive = model._levelLive
        _timeLeftLive = model._timeLeftLive
        timeLeftLive = model.timeLeftLive
        levelLive = model.levelLive
        info = GameInfo(model.score,false)
        gameTable.restoreState(false,model.table,info,model.phase,_levelLive,_timeLeftLive)

        firstObserved = true
        registLevelObserver()
        timeLeftLive.observe(this){
            Log.i("TIME",_timeLeftLive.value.toString())
            binding.timeCounter.text = _timeLeftLive.value.toString()
        }
    }

    private fun registTimeObserver(){
        timeLeftLive.observe(this){
            Log.i("TIME",_timeLeftLive.value.toString())
            binding.timeCounter.text = _timeLeftLive.value.toString()
        }
    }

    private fun registLevelObserver(){
        levelLive.observe(this){
            if(firstObserved) {
                firstObserved = false
            }
            else {
                tableReset()
                binding.levelView.text = "Next level in " + 5 + " seconds!"
                binding.timeCounter.text = ""
                _timeLeftLive.value = 60 - (5 * (_levelLive.value!!-1))
                thread {
                    var pausetime = 5
                    runOnUiThread {
                        menuItem.title = "Level: " + _levelLive.value.toString()
                    }
                    if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        runOnUiThread {
                            binding.btnPause.setImageResource(R.drawable.ic_baseline_pause_42)
                        }
                    } else{
                        runOnUiThread {
                            binding.btnPause.setImageResource(R.drawable.ic_baseline_pause_14)
                        }
                    }
                    while(pausetime > 0){
                        if(!paused){
                            pausetime -=1
                            runOnUiThread {
                                binding.levelView.text = "Next level in " + pausetime + " seconds!"
                            }
                        }
                        Thread.sleep(1000)
                    }
                    runOnUiThread {
                        binding.levelView.text = ""
                        binding.levelPhase.text = ""
                    }
                    if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        runOnUiThread {
                            binding.btnPause.setImageResource(R.drawable.ic_outline_empty_origin_42)
                        }
                    }
                    else {
                        runOnUiThread {
                            binding.btnPause.setImageResource(R.drawable.ic_outline_empty_origin_14)
                        }
                    }
                    info.inTurn = false
                    gameTable.gameStart()
                }
            }
        }

    }


    private fun tableReset(){
        binding.cell1.text = ""
        binding.cell2.text = ""
        binding.cell3.text = ""
        binding.cell4.text = ""
        binding.cell5.text = ""
        binding.cell6.text = ""
        binding.cell7.text = ""
        binding.cell8.text = ""
        binding.cell9.text = ""
        binding.cell10.text = ""
        binding.cell11.text = ""
        binding.cell12.text = ""
        binding.cell13.text = ""
        binding.cell14.text = ""
        binding.cell15.text = ""
        binding.cell16.text = ""
        binding.cell17.text = ""
        binding.cell18.text = ""
        binding.cell19.text = ""
        binding.cell20.text = ""
        binding.cell21.text = ""
        binding.cell22.text = ""
        binding.cell23.text = ""
        binding.cell24.text = ""
        binding.cell25.text = ""
    }

    companion object{
        val GAMETIME : Int = 4
    }

}