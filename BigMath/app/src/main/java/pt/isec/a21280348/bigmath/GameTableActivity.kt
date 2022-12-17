package pt.isec.a21280348.bigmath

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import pt.isec.a21280348.bigmath.databinding.ActivityGameTableBinding
import pt.isec.a21280348.bigmath.utils.TableSupporter.Companion.generateTable

class GameTableActivity : AppCompatActivity() {
    data class GameInfo(var currentScore : Int,var currentTime : Int,var inTurn : Boolean,var level :Int)
    private lateinit var binding : ActivityGameTableBinding

    lateinit var gameTable : GameTable
    var info : GameInfo = GameInfo(0, GAMETIME,false,1)
    var table : MutableList<Any> = mutableListOf(20)
    var paused : Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameTableBinding.inflate(layoutInflater)
        setContentView(binding.root)


        gameTable = GameTable(this)
        gameTable.setActivityBinding(binding)
        gameTable.setGameInfo(info)
        binding.gameTableId.addView(gameTable)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Math Game"

        val r = Thread{
            while(info.currentTime > 0) {
                if(info.inTurn){
                    var pausetime : Int = 5
                    binding.btnPause.setImageResource(R.drawable.ic_baseline_pause_42)
                    while(pausetime > 0){
                        if(!paused){
                            pausetime -=1
                            binding.levelView.text = "Next level in " + pausetime + " seconds!"
                        }
                        Thread.sleep(1000)
                    }
                    binding.levelView.text = "Level: " + info.level.toString()
                    binding.btnPause.setImageResource(R.drawable.ic_outline_empty_origin_42)
                    info.inTurn = false
                }
                else if (!paused) {
                    binding.timeCounter.post{
                        binding.timeCounter.text =(info.currentTime ).toString()
                    }
                    info.currentTime -= 1
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
    }


    fun confirmQuit(){

    }


    companion object{
        val GAMETIME : Int = 60
    }

}