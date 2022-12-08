package pt.isec.a21280348.bigmath

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import pt.isec.a21280348.bigmath.databinding.ActivityGameTableBinding
import pt.isec.a21280348.bigmath.utils.TableSupporter.Companion.generateTable

class GameTableActivity : AppCompatActivity() {
    private lateinit var binding : ActivityGameTableBinding
    lateinit var gameTable : GameTable
    var score : Int = 0
    var table : MutableList<Any> = mutableListOf(20)
    private var level : Int = 0
        set(value){
            field = value
            binding.levelView.text = "Level: " + value
            binding.tvScore.text =  score.toString()
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameTableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startGame()

        gameTable = GameTable(this,binding,table,score)
        binding.gameTableId.addView(gameTable)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Math Game"

        binding.btnPause.setOnClickListener{
            Toast.makeText(this,"PAUSA",Toast.LENGTH_SHORT).show()
        }


    }


    fun startGame(){
        nextLevel()
    }





    fun nextLevel(){
        table = generateTable(level++)
        var it = table.iterator()
        Log.i("a",table.toString())
        for(i in 0..24){
            if(i == 6 || i == 8 || i ==16 || i ==18)
                continue
            when(i){
                0 -> binding.cell1.text = it.next().toString()
                1 -> binding.cell2.text = it.next().toString()
                2 -> binding.cell3.text = it.next().toString()
                3 -> binding.cell4.text = it.next().toString()
                4 -> binding.cell5.text = it.next().toString()
                5 -> binding.cell6.text = it.next().toString()
                //6 -> binding.cell7.text = it.next().toString()
                7 -> binding.cell8.text = it.next().toString()
                //8 -> binding.cell9.text = it.next().toString()
                9 -> binding.cell10.text = it.next().toString()
                10 -> binding.cell11.text = it.next().toString()
                11 -> binding.cell12.text = it.next().toString()
                12 -> binding.cell13.text = it.next().toString()
                13 -> binding.cell14.text = it.next().toString()
                14 -> binding.cell15.text = it.next().toString()
                15 -> binding.cell16.text = it.next().toString()
                //16 -> binding.cell17.text = it.next().toString()
                17 -> binding.cell18.text = it.next().toString()
                //18 -> binding.cell19.text = it.next().toString()
                19 -> binding.cell20.text = it.next().toString()
                20 -> binding.cell21.text = it.next().toString()
                21 -> binding.cell22.text = it.next().toString()
                22 -> binding.cell23.text = it.next().toString()
                23 -> binding.cell24.text = it.next().toString()
                24 -> binding.cell25.text = it.next().toString()
            }
        }
    }

}