package pt.isec.a21280348.bigmath

import android.content.ClipData.Item
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.view.get
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pt.isec.a21280348.bigmath.databinding.ActivityGameTableBinding
import pt.isec.a21280348.bigmath.utils.TableSupporter.Companion.generateTable
import kotlin.concurrent.thread

class GameTableActivity : AppCompatActivity() {
    data class GameInfo(var currentScore : Int,var inTurn : Boolean)
    private lateinit var binding : ActivityGameTableBinding
    private val _levelLive : MutableLiveData<Int> = MutableLiveData<Int>().apply { value = 1 }
    private val _timeLeftLive : MutableLiveData<Int> = MutableLiveData<Int>().apply { value = GAMETIME }
    private lateinit var menuItem : MenuItem

    private val levelLive : LiveData<Int>
        get() = _levelLive

    private val timeLeftLive : LiveData<Int>
        get() = _timeLeftLive

    lateinit var gameTable : GameTable
    var info : GameInfo = GameInfo(0,false)
    var table : MutableList<Any> = mutableListOf(20)
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

        val r = Thread{

            while(_timeLeftLive.value!! > 0) {
                if(info.inTurn){
                }
                else if (!paused) {
                    _timeLeftLive.postValue( (_timeLeftLive.value!! - 1))
                }
                Thread.sleep(1000)
            }
            val intent = Intent(this,ScoreboardActivity::class.java)
            intent.putExtra("score",gameTable.getFinalScore())
            startActivity(intent)
        }



        binding.btnPause.setOnClickListener {
            if (info.inTurn) {
                paused = !paused
                if (paused) {
                    binding.btnPause.setImageResource(R.drawable.ic_baseline_play_arrow_42)
                } else {
                    binding.btnPause.setImageResource(R.drawable.ic_baseline_pause_42)

                }
            }
        }
        r.start()
        gameTable.gameStart()
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
                    binding.btnPause.setImageResource(R.drawable.ic_baseline_pause_42)
                    while(pausetime > 0){
                        if(!paused){
                            pausetime -=1
                            binding.levelView.text = "Next level in " + pausetime + " seconds!"
                        }
                        Thread.sleep(1000)
                    }
                    binding.levelView.text = ""
                    binding.levelPhase.text = ""
                    binding.btnPause.setImageResource(R.drawable.ic_outline_empty_origin_42)
                    info.inTurn = false
                    gameTable.gameStart()
                }
            }
        }


        timeLeftLive.observe(this){
            Log.i("TIME",_timeLeftLive.value.toString())
            binding.timeCounter.text = _timeLeftLive.value.toString()

        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.table_menu,menu)
        menuItem = menu[0]
        menuItem.title = "Level: " + _levelLive.value.toString()
        return true
    }


    fun confirmQuit(){

    }

    fun tableReset(){
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
        val GAMETIME : Int = 60
    }

}