package pt.isec.a21280348.bigmath

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pt.isec.a21280348.bigmath.databinding.ActivityScoreboardBinding

class ScoreboardActivity : AppCompatActivity() {
    data class ScoreData(val userName : String, val score : Int, val bitmap : String)

    lateinit var binding : ActivityScoreboardBinding

    val highScores = arrayListOf<ScoreData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //val score = intent.getIntExtra("score",5)

        for(i in 0..4){
            highScores.add(ScoreData( ("user_" + i.toString()) , i , "..."))
        }

        binding.scoreList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        binding.scoreList.adapter = RVAdapter(highScores)
    }


    class RVAdapter(val data : ArrayList<ScoreData>) : RecyclerView.Adapter<RVAdapter.ViewHolder>(){
        class ViewHolder(val view : View): RecyclerView.ViewHolder(view){
            var usernameTV : TextView = view.findViewById(R.id.tvname)
            var scoreTV : TextView = view.findViewById(R.id.tvscore)
            var avatarIMG : ImageView = view.findViewById(R.id.userAvatar)

            fun update(newData : ScoreData){
                usernameTV.text = newData.userName;
                scoreTV.text = "Score: " + newData.score;
                //avatarIMG.setImageResource( ... )
            }
        }



        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_score,parent,false)
            view.tag = " "+(nr++)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.update(data[position])
        }

        override fun getItemCount(): Int = data.size
    }

    companion object{
        var nr : Int = 0
    }
}