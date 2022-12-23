package pt.isec.a21280348.bigmath

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import pt.isec.a21280348.bigmath.databinding.ActivityCreditsBinding
import java.util.concurrent.TimeUnit

class CreditsActivity : AppCompatActivity() {
 private lateinit var binding : ActivityCreditsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreditsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //addDataToFT()
        updateDataToFT()

    }

    fun addDataToFT(){
        //add a document to a collection in firestore
        val db = Firebase.firestore
        Log.i("Firebase",db.toString())

        val gameData1 = hashMapOf(
            "username" to "Leonardo Sousa",
            "score" to 27,
            "image" to "leo.png",
        )
        val gameData2 = hashMapOf(
            "username" to "Leonardo Sousa",
            "score" to 25,
            "image" to "leo.png",
        )

        db.collection("PointHighScores").document("PointsScore_1").set(gameData1)
        db.collection("PointHighScores").document("PointsScore_2").set(gameData2)
            /*.set(gameData).addOnSuccessListener {
                Log.i("Firebase","score added!")
            }.addOnFailureListener {
                Log.i("Firebase","score fail to add!")
            }*/
    }

    fun updateDataToFT() {
        val db = Firebase.firestore
        val coll = db.collection("PointHighScores")
        val countQuery = coll.count()
        var size : Long = 0

        countQuery.get(AggregateSource.SERVER).addOnCompleteListener{ task ->
            if(task.isSuccessful) {
                size = task.result.count
                Log.i("Firebase", "Count -> " + size)
                dataShow(size)
                return@addOnCompleteListener
            }else{
                //---
            }
        }
    }

    fun dataShow(size : Long){
        val db = Firebase.firestore

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
            }
        }
    }
}