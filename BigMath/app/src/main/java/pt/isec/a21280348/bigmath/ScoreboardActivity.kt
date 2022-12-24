package pt.isec.a21280348.bigmath

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import pt.isec.a21280348.bigmath.databinding.ActivityScoreboardBinding
import java.util.Objects

class ScoreboardActivity : AppCompatActivity() {
    data class ScoreData(val userName : String, val score : Long, val bitmap : String)

    lateinit var binding : ActivityScoreboardBinding

    var highScores = arrayListOf<ScoreData>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //val score = intent.getIntExtra("score",5)



        val db = Firebase.firestore
        val db_highscores = db.collection("PointHighScores")
        Log.i("Firebase", "Count -> " + 5)
        var username : String = ""
        var score : Long= 0
        var image : String= ""

        binding.scoreList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        val myAdapter = RVAdapter(highScores)
        binding.scoreList.adapter = myAdapter
        for (i in 5 downTo 1 ) {
            val v = db_highscores.document("PointsScore_"+i.toString())
            v.get(Source.SERVER).addOnSuccessListener {
                val exists = it.exists()
                if(!exists)
                    return@addOnSuccessListener
                if(it.get("username") == null)
                    return@addOnSuccessListener
                username = it.getString("username") ?: "user"
                score = it.getLong("score") ?: 0
                image = it.getString("image") ?: "user"
                //v.update("username",newStr)
                Log.i("Firebase", username)
                Log.i("Firebase", score.toString())
                Log.i("Firebase", image)

                highScores.add(ScoreData(username,score,image))
                myAdapter.notifyItemInserted(5-i)
            }
        }

        Log.i("Firebase", "FINAL Count -> " + highScores.size)




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
























    fun updateDataToFT() : ArrayList<ScoreData>{
        val db = Firebase.firestore
        val coll = db.collection("PointHighScores")
        val countQuery = coll.count()
        var size : Long = 0
        var scoreList = arrayListOf<ScoreData>()

        countQuery.get(AggregateSource.SERVER).addOnCompleteListener{ task ->
            if(task.isSuccessful) {
                size = task.result.count
                Log.i("Firebase", "Count -> " + size)
                return@addOnCompleteListener
            }else{
                //...
            }
        }
        scoreList = dataShow(size)
        return scoreList
    }

    fun dataShow(size : Long): ArrayList<ScoreData> {
        val db = Firebase.firestore
        var scoreList = arrayListOf<ScoreData>()
        Log.i("Firebase", "Count -> " + size)
        for (i in 1..size ) {
            val v = db.collection("PointHighScores").document("PointsScore_1")
            v.get(Source.SERVER).addOnSuccessListener {
                val exists = it.exists()
                if (!exists) //if dont exists
                    return@addOnSuccessListener
                val username = it.getString("username") ?: "user"
                val score = it.getLong("score") ?: 0
                val image = it.getString("image") ?: "user"
                //v.update("username",newStr)
                Log.i("Firebase", username)
                Log.i("Firebase", score.toString())
                Log.i("Firebase", image)
                scoreList.add(ScoreData(username,score,image))
            }
        }
        return scoreList
    }


    companion object{
        var nr : Int = 0
    }
}