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
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pt.isec.a21280348.bigmath.databinding.ActivityScoreboardBinding

class ScoreboardActivity : AppCompatActivity() {
    data class ScoreData(val userName: String, val score: Long, val imgBaseStr: String)

    lateinit var binding: ActivityScoreboardBinding

    var highScores = arrayListOf<ScoreData>()
    var highScoresTime = arrayListOf<ScoreData>()

    var myAdapter = RVAdapter(highScores)
    lateinit var recyclerView: RecyclerView

    var listeners = arrayListOf<ListenerRegistration>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPref =
            this.getSharedPreferences(getString(R.string.user_file_key), Context.MODE_PRIVATE)
                ?: return
        var insertedUser = false

        //read user data
        val playName = sharedPref.getString(getString(R.string.usernameIdent), "")
        val playImage = sharedPref.getString(getString(R.string.imageIdent), "")
        val playScore = intent.getIntExtra("score", -1).toLong()
        val playTotalTime = intent.getIntExtra("totalTime", -1).toLong()
        Log.i("TotalTimeScore", playTotalTime.toString())


        val db = Firebase.firestore
        val db_highscores = db.collection("PointHighScores")
        Log.i("Firebase", "Count -> " + 5)
        var username: String = ""
        var score: Long = 0
        var image: String = ""
        recyclerView = binding.scoreList
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        if (playScore.toInt() != -1) {
            Log.i("NEWWAY", "start of")
            db.collection("PointHighScores").get().addOnSuccessListener { documents ->
                var i = 0
                for (document in documents) {
                    Log.i("ZZZ", document.get("score").toString())
                    if (document.get("score").toString() == "-1") {
                        Log.i("INSERTNEW", "Entrei aqui")
                        if (insertedUser)
                            break
                        else {
                            val insertData = hashMapOf(
                                "image" to playImage,
                                "score" to playScore,
                                "username" to playName,
                            )
                            insertedUser = true
                            Log.i("INSERT NEW", insertData.toString())
                            //document.reference.set(insertData)
                            //highScores.add(ScoreData(playName!!, playScore, playImage!!))
                        }
                    }
                }

                var tempScores = highScores.toMutableList()
                tempScores.add(ScoreData(playName!!, playScore, playImage!!))


                val sortedScores =
                    tempScores.sortedWith(compareBy({ it.score }))
                        .reversed() as ArrayList<ScoreData>
                Log.i("SORTED", "ISTO:  " + highScores.size)
                for (sorted in sortedScores) {
                    Log.i("SORTED", sorted.score.toString())
                }
                Log.i("EUSIM",sortedScores.size.toString())
                if (sortedScores.size >= 6) {
                    sortedScores.removeAt(5)
                }

                //if was added some new:


                if (true) {
                    Log.i("ORDER", "Entrei!")
                    //highScores = sortedScores.toMutableList() as ArrayList<ScoreData>


                    db.collection("PointHighScores").get().addOnSuccessListener { docs ->
                        var i = 0
                        for (document in docs) {
                            val insertData = hashMapOf(
                                "image" to sortedScores[i].imgBaseStr,
                                "score" to sortedScores[i].score,
                                "username" to sortedScores[i].userName,
                            )
                            Log.i(
                                "DBSORTED",
                                i.toString() + " -> " + insertData.get("score").toString()
                            )
                            i++
                            document.reference.set(insertData)
                        }
                    }
                }

            }.addOnFailureListener {
                Log.i("NEWWAY", "DEAD")
            }



            insertedUser = false


            db.collection("TimeHighScore").get().addOnSuccessListener { documents ->
                var i = 0
                for (document in documents) {
                    highScoresTime.add(ScoreData(document.get("username")!!.toString(),document.get("time")!! as Long,document.get("image")!!.toString()))
                }

                var tempScores = highScoresTime.toMutableList()
                tempScores.add(ScoreData(playName!!, playTotalTime, playImage!!))


                val sortedScores =
                    tempScores.sortedWith(compareBy({ it.score }))
                        .reversed() as ArrayList<ScoreData>
                Log.i("SORTED", "ISTO:  " + highScores.size)
                for (sorted in sortedScores) {
                    Log.i("SORTED", sorted.score.toString())
                }
                Log.i("EUSIM",sortedScores.size.toString())
                if (sortedScores.size >= 6) {
                    sortedScores.removeAt(5)
                }

                //if was added some new:


                if (true) {
                    Log.i("ORDER", "Entrei!")
                    //highScores = sortedScores.toMutableList() as ArrayList<ScoreData>


                    db.collection("TimeHighScore").get().addOnSuccessListener { docs ->
                        var i = 0
                        for (document in docs) {
                            val insertData = hashMapOf(
                                "image" to sortedScores[i].imgBaseStr,
                                "time" to sortedScores[i].score,
                                "username" to sortedScores[i].userName,
                            )
                            Log.i(
                                "DBSORTED",
                                i.toString() + " -> " + insertData.get("time").toString()
                            )
                            i++
                            document.reference.set(insertData)
                        }
                    }
                }

            }.addOnFailureListener {
                Log.i("NEWWAY", "DEAD")
            }


            binding.tvLocalScore.text = "MY SCORE: $playScore"
        } else {
            binding.tvLocalScore.text = "TOP SCORES"
        }
        recyclerView.adapter = myAdapter
        //startObservers()
        Log.i("Firebase", "FINAL Count -> " + highScores.size)

        binding.btnTime.setOnClickListener {
            endObservers()
            highScores.clear()
            myAdapter.notifyItemRangeRemoved(0, 5)
            startObserversTime()
            myAdapter.notifyItemRangeInserted(0, 5)
            if(playScore.toInt() != -1)
                binding.tvLocalScore.text = "MY TIME: $playTotalTime"
            else
                binding.tvLocalScore.text = "TOP TIME"
        }

        binding.btnScore.setOnClickListener {
            endObservers()
            highScores.clear()
            myAdapter.notifyItemRangeRemoved(0, 5)
            startObserversScore()
            myAdapter.notifyItemRangeInserted(0, 5)
            if(playScore.toInt() != -1)
                binding.tvLocalScore.text = "MY SCORE: $playScore"
            else
                binding.tvLocalScore.text = "TOP SCORES"
        }

    }

    override fun onStop() {
        endObservers()
        super.onStop()
    }

    override fun onStart() {
        startObserversScore()
        super.onStart()

    }


    class RVAdapter(val data: ArrayList<ScoreData>) : RecyclerView.Adapter<RVAdapter.ViewHolder>() {
        class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            var usernameTV: TextView = view.findViewById(R.id.tvname)
            var scoreTV: TextView = view.findViewById(R.id.tvscore)
            var avatarIMG: ImageView = view.findViewById(R.id.userAvatar)

            fun update(newData: ScoreData) {
                if (newData.score.toInt() == -1) {
                    usernameTV.text = "";
                    scoreTV.text = "";
                    val decoded64 = Base64.decode(newData.imgBaseStr, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decoded64, 0, decoded64.size)
                    avatarIMG.setImageBitmap(bitmap)
                } else {
                    usernameTV.text = newData.userName;
                    scoreTV.text = "Score: " + newData.score;
                    val decoded64 = Base64.decode(newData.imgBaseStr, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decoded64, 0, decoded64.size)
                    avatarIMG.setImageBitmap(bitmap)
                }
            }
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_score, parent, false)
            view.tag = " " + (nr++)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.update(data[position])
        }

        override fun getItemCount(): Int = data.size
    }


    fun startObserversScore() {
        for (i in 0..4) {
            highScores.add(i, ScoreData("", -1, ""))
            startObserverSc(i)
        }
    }

    fun startObserversTime() {
        for (i in 0..4) {
            highScores.add(i, ScoreData("", -1, ""))
            startObserverTm(i)
        }
    }

    fun endObservers() {
        for (i in 0..4) {
            listeners[i].remove()
        }
        listeners.clear()
    }

    fun fillFSScore(index: Int) {
        val templateData = hashMapOf(
            "username" to "",
            "score" to -1,
            "image" to "",
        )

        val db = Firebase.firestore
        val doc = "PointsScore_$index"
        Log.i("Filling", doc)
        db.collection("PointHighScores").document(doc).set(templateData)
    }

    fun fillFSTime(index: Int) {
        val templateData = hashMapOf(
            "username" to "",
            "time" to -1,
            "image" to "",
        )

        val db = Firebase.firestore
        val doc = "TimeScore_$index"
        Log.i("Filling", doc)
        db.collection("TimeHighScore").document(doc).set(templateData, SetOptions.merge())
    }


    fun startObserverSc(index: Int) {
        Log.i("aiai", "PointsScore_" + index.toString())
        val db = Firebase.firestore
        listeners.add(
            db.collection("PointHighScores").document("PointsScore_" + index.toString())
                .addSnapshotListener { doc, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }
                    if (doc != null && doc.exists()) {
                        var username = doc.getString("username")
                        var score = doc.getLong("score")
                        var image = doc.getString("image")
                        Log.i("TESTE", highScores.size.toString())
                        if (username == null || score == null || image == null)
                            fillFSScore(index)
                        else
                            highScores[index] = ScoreData(username, score, image)
                        Log.i(
                            "Listener",
                            "Mudei " + highScores.get(index).userName + " " + highScores.get(index).score
                        )
                        Log.i("Listener", "Mudei " + myAdapter.itemCount)
                        myAdapter.notifyItemChanged(index)
                    }
                })
    }

    fun startObserverTm(index: Int) {
        Log.i("aiai", "TimeScore_" + index.toString())
        val db = Firebase.firestore
        listeners.add(
            db.collection("TimeHighScore").document("TimeScore_" + index.toString())
                .addSnapshotListener { doc, e ->
                    if (e != null)
                        return@addSnapshotListener
                    if (doc != null && doc.exists()) {
                        var username = doc.getString("username")
                        var time = doc.getLong("time")
                        var image = doc.getString("image")
                        Log.i("TESTE", highScores.size.toString())
                        if (username == null || time == null || image == null)
                            fillFSTime(index)
                        else
                            highScores[index] = ScoreData(username, time, image)
                        Log.i(
                            "Listener",
                            "Mudei " + highScores.get(index).userName + " " + highScores.get(index).score
                        )
                        Log.i("Listener", "Mudei " + myAdapter.itemCount)
                        myAdapter.notifyItemChanged(index)
                    } else {
                        Log.i("TIMEFILL", "FILL")
                        fillFSTime(index)
                    }
                })
    }


    fun updateDataToFT(): ArrayList<ScoreData> {
        val db = Firebase.firestore
        val coll = db.collection("PointHighScores")
        val countQuery = coll.count()
        var size: Long = 0
        var scoreList = arrayListOf<ScoreData>()

        countQuery.get(AggregateSource.SERVER).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                size = task.result.count
                Log.i("Firebase", "Count -> " + size)
                return@addOnCompleteListener
            } else {
                //...
            }
        }
        scoreList = dataShow(size)
        return scoreList
    }

    fun dataShow(size: Long): ArrayList<ScoreData> {
        val db = Firebase.firestore
        var scoreList = arrayListOf<ScoreData>()
        Log.i("Firebase", "Count -> " + size)
        for (i in 1..size) {
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
                scoreList.add(ScoreData(username, score, image))
            }
        }
        return scoreList
    }


    companion object {
        var nr: Int = 0
    }
}