package pt.isec.a21280348.bigmath

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import pt.isec.a21280348.bigmath.databinding.ActivityGameTableBinding
import pt.isec.a21280348.bigmath.utils.TableSupporter
import pt.isec.a21280348.bigmath.utils.TableSupporter.Companion.checkOperation
import kotlin.math.abs

const val TAG_DAREA = "DrawingAreaEvent"

class GameTable @JvmOverloads constructor(
    context      : Context,
    attrs        : AttributeSet? = null,
    defStyleAttr : Int = 0,
    defStyleRes  : Int = 0
) : View(context,attrs,defStyleAttr,defStyleRes),GestureDetector.OnGestureListener {

    lateinit var table : MutableList<Any>
    var score : Int = 0
    private var level : Int = 1
        set(value){
            field = value
            binding.levelView.text =  "Level: " +value.toString()
        }

    private lateinit var binding : ActivityGameTableBinding

    var isScrolling : Boolean = false

    constructor(context: Context, binding : ActivityGameTableBinding, table : MutableList<Any>,score : Int) : this(context) {
        this.binding = binding
        this.table = table
        this.score = score
    }


    private val gestureDetector : GestureDetector by lazy {
        GestureDetector(context, this)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event?.action == MotionEvent.ACTION_UP ) {
            isScrolling = false
        }

        if (gestureDetector.onTouchEvent(event!!))
            return true
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onDown(e: MotionEvent): Boolean {
        return true
    }



    override fun onShowPress(e: MotionEvent) {

    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {

        var cellWidht  = binding.cell1.right
        var cellHeight = binding.cell1.bottom

        val myDistanceX = abs((e1.x) - abs(e2.x))
        val myDistanceY = abs((e1.y) - abs(e2.y))



        if(!isScrolling){
            if((myDistanceY > cellHeight * 3) && (myDistanceX < cellWidht)){//colunas
                checkColumn(e1,e2)
                isScrolling = true
            }else if((myDistanceX > cellWidht*3) && (myDistanceY < cellHeight)){//linhas
                checkLine(e1,e2)
                isScrolling = true
            }
        }

        return true
    }

    override fun onLongPress(e: MotionEvent) {
        //Log.i(TAG_DAREA, "onLongPress: ")
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        Log.i(TAG_DAREA, "onFling: ")
        //checkDistance(e1,e2)
        return false
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    fun nextLevel(){
        table = TableSupporter.generateTable(level++)
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
                7 -> binding.cell8.text = it.next().toString()
                9 -> binding.cell10.text = it.next().toString()
                10 -> binding.cell11.text = it.next().toString()
                11 -> binding.cell12.text = it.next().toString()
                12 -> binding.cell13.text = it.next().toString()
                13 -> binding.cell14.text = it.next().toString()
                14 -> binding.cell15.text = it.next().toString()
                15 -> binding.cell16.text = it.next().toString()
                17 -> binding.cell18.text = it.next().toString()
                19 -> binding.cell20.text = it.next().toString()
                20 -> binding.cell21.text = it.next().toString()
                21 -> binding.cell22.text = it.next().toString()
                22 -> binding.cell23.text = it.next().toString()
                23 -> binding.cell24.text = it.next().toString()
                24 -> binding.cell25.text = it.next().toString()
            }
        }
    }

    fun checkColumn(e1: MotionEvent,e2: MotionEvent){
        var chosen = 0
        var validPlay = false

        var cellWidht  = binding.cell1.right

        Log.i("INFOEVENT","(" + e1.x + "," + e2.x +")" + " | " +"(" + e1.y + "," + e2.y +")")

        if(e1.x > 0 && e2.x < cellWidht) {
            Log.i("PLACE", "Coluna I")
            chosen = 4
            validPlay = true
        }else if(e1.x > cellWidht * 2 && e2.x < cellWidht * 3){
            Log.i("PLACE", "Coluna II")
            chosen = 5
            validPlay = true
        }else if(e1.x > cellWidht * 4 && e2.x < cellWidht * 5){
            Log.i("PLACE", "Coluna III")
            chosen = 5
            validPlay = true
        }
        if(validPlay)
            calculatePlay(chosen)
    }
    fun checkLine(e1: MotionEvent,e2: MotionEvent){
        var chosen = 0
        var validPlay = false

        var cellHeight = binding.cell1.bottom

        Log.i("INFOEVENT","(" + e1.x + "," + e2.x +")" + " | " +"(" + e1.y + "," + e2.y +")")

        if(e1.y > 0 && e2.y < cellHeight) {
            Log.i("PLACE", "Linha I")
            chosen = 1
            validPlay = true
        }else if(e1.y > cellHeight*2 && e2.y < cellHeight*3){
            Log.i("PLACE", "Linha II")
            chosen = 2
            validPlay = true
        }else if(e1.y > cellHeight * 4 && e2.y < cellHeight * 5){
            Log.i("PLACE", "Linha III")
            chosen = 3
            validPlay = true
        }

        if(validPlay)
            calculatePlay(chosen)
    }



    fun calculatePlay(play : Int) {
        checkPlay(play)
        nextLevel()
    }

    fun checkPlay(chosen : Int){
        //1 -> line   one;
        //4 -> column one,
        //etc...
        if(chosen == checkBigger()) {
            score += 2 * level
            binding.tvScore.setTextColor(resources.getColor(R.color.rightChoice))
            binding.tvScore.animate().setDuration(750).withEndAction { binding.tvScore.setTextColor(Color.BLACK) }.start()
        }
        else {
            binding.tvScore.setTextColor(resources.getColor(R.color.wrongChoice))
            binding.tvScore.animate().setDuration(750).withEndAction { binding.tvScore.setTextColor(Color.BLACK) }.start()
                score -= (level* 1.3).toInt();
                if(score < 0)
                    score = 0

        }

        binding.tvScore.text =  score.toString()
    }


    fun checkBigger() : Int{
        var bigger :Double = 0.0
        var biggerId: Int = 0
        var value: Double = 0.0
        value = checkOperation(table.get(0) as Int,table.get(1) as Char,table.get(2) as Int,table.get(3) as Char,table.get(4) as Int)
        if(value > bigger) {
            biggerId = 1
            bigger = value
        }

        value = checkOperation(table.get(8) as Int,table.get(9) as Char,table.get(10) as Int,table.get(11) as Char,table.get(12) as Int)
        if(value > bigger) {
            biggerId = 2
            bigger = value
        }

        value = checkOperation(table.get(16) as Int,table.get(17) as Char,table.get(18) as Int,table.get(19) as Char,table.get(20) as Int)
        if(value > bigger) {
            biggerId = 3
            bigger = value
        }

        value = checkOperation(table.get(0) as Int,table.get(5) as Char,table.get(8) as Int,table.get(13) as Char,table.get(16) as Int)
        if(value > bigger) {
            biggerId = 4
            bigger = value
        }

        value = checkOperation(table.get(2) as Int,table.get(6) as Char,table.get(10) as Int,table.get(14) as Char,table.get(18) as Int)
        if(value > bigger) {
            biggerId = 5
            bigger = value
        }

        value = checkOperation(table.get(4) as Int,table.get(7) as Char,table.get(12) as Int,table.get(15) as Char,table.get(20) as Int)
        if(value > bigger) {
            biggerId = 6
            bigger = value
        }

       return biggerId
    }

}













/*
   if(((e1.x > 0 && e1.x < cellWidht) && (e2.x > cellWidht*4)) && (e1.y > 0 && e2.y < cellHeight) ) {
       Log.i(TAG_DAREA, "Primeira Linha!")
       chosen = 1
       valid = true
   }
   else if(((e1.x > 0 && e1.x < cellWidht) && (e2.x > cellWidht*4)) && (e1.y > cellHeight*2 && e2.y < cellHeight*3) ) {
       Log.i(TAG_DAREA, "Segunda Linha!")
       chosen = 2
       valid = true
   }
   else if(((e1.x > 0 && e1.x < cellWidht) && (e2.x > cellWidht*4)) && (e1.y > cellHeight*4 && e2.y < cellHeight*5) ) {
       Log.i(TAG_DAREA, "Terceira Linha!")
       chosen = 3
       valid = true
   }
   else if(((e1.y > 0 && e1.y < cellHeight) && (e2.y > cellHeight*4)) && (e1.x > 0 && e2.x < cellWidht) ) {
       Log.i(TAG_DAREA, "PRIMEIRA COLUNA!")
       chosen = 4
       valid = true
   }
   else if(((e1.y > 0 && e1.y < cellHeight) && (e2.y > cellHeight*4)) && (e1.x > cellWidht*2 && e2.x < cellWidht*3) ) {
       Log.i(TAG_DAREA, "Segunda COLUNA!")
       chosen = 5
       valid = true
   }

   else if(((e1.y > 0 && e1.y < cellHeight) && (e2.y > cellHeight*4)) && (e1.x > cellWidht*4 && e2.x < cellWidht*5) ) {
       Log.i(TAG_DAREA, "Terceira COLUNA!")
       chosen = 6
       valid = true
   }*/