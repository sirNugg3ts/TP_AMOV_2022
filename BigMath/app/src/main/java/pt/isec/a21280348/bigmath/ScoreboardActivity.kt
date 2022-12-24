package pt.isec.a21280348.bigmath

import android.content.Context
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
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
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import pt.isec.a21280348.bigmath.databinding.ActivityScoreboardBinding
import java.util.Objects

class ScoreboardActivity : AppCompatActivity() {
    data class ScoreData(val userName : String, val score : Long, val imgBaseStr : String)

    lateinit var binding : ActivityScoreboardBinding

    var highScores = arrayListOf<ScoreData>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPref = this.getSharedPreferences(getString(R.string.user_file_key), Context.MODE_PRIVATE) ?: return
        var insertedUser = false

        //read user data
        val playName = sharedPref.getString(getString(R.string.usernameIdent), "")
        val playImage= sharedPref.getString(getString(R.string.imageIdent), "")
        val playScore = intent.getIntExtra("score",5).toLong()
        //Log.i("SCORE",playScore.toString())



        val db = Firebase.firestore
        val db_highscores = db.collection("PointHighScores")
        Log.i("Firebase", "Count -> " + 5)
        var username : String = ""
        var score : Long= 0
        var image : String= ""

        binding.scoreList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        val myAdapter = RVAdapter(highScores)
        binding.scoreList.adapter = myAdapter
        /*for (i in 1..5 ) {
            val v = db_highscores.document("PointsScore_"+i.toString())
            Log.i("Leaderboard", ("Lendo : PointsScore_"+i.toString()))
            Thread.sleep(300)
            v.get(Source.SERVER).addOnSuccessListener {
                val exists = it.exists()
                if(!exists)
                    return@addOnSuccessListener
                if(it.get("username") == null){
                    if(insertedUser)
                        return@addOnSuccessListener
                    else{
                        insertedUser = true
                    }
                }
                username = it.getString("username") ?: playName!!
                score = it.getLong("score") ?: playScore
                image = it.getString("image") ?: playImage!!
                //v.update("username",newStr)
                Log.i("Firebase", username)
                Log.i("Firebase", score.toString())
                Log.i("Firebase", image)

                Log.i("Leaderboard",score.toString())
                highScores.add(ScoreData(username,score,image))
                myAdapter.notifyItemInserted(i-1)
            }
        }*/
        Log.i("NEWWAY", "start of")
        db.collection("PointHighScores").get().addOnSuccessListener { documents->
            var i = 0
            for(document in documents){

                if(document.get("username") == null){
                    if(insertedUser)
                        return@addOnSuccessListener
                    else{
                        val insertData = hashMapOf(
                            "image" to playImage,
                            "score" to playScore,
                            "username" to playName,
                        )
                        insertedUser = true
                        document.reference.set(insertData)
                        highScores.add(ScoreData(playName!!, playScore, playImage!!))
                        myAdapter.notifyItemInserted(i++)
                    }
                }else {

                    username = document.getString("username") ?: playName!!
                    score = document.getLong("score") ?: playScore
                    image = document.getString("image") ?: playImage!!
                    //v.update("username",newStr)
                    Log.i("Firebase", username)
                    Log.i("Firebase", score.toString())
                    Log.i("Firebase", image)

                    Log.i("Leaderboard", score.toString())
                    highScores.add(ScoreData(username, score, image))
                    myAdapter.notifyItemInserted(i++)
                }

            }
        }.addOnFailureListener{
            Log.i("NEWWAY","DEAD")
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
                val decoded64 = Base64.decode(newData.imgBaseStr, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decoded64,0,decoded64.size)
                avatarIMG.setImageBitmap(bitmap)

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