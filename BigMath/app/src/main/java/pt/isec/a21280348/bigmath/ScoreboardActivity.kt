package pt.isec.a21280348.bigmath

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pt.isec.a21280348.bigmath.databinding.ActivityScoreboardBinding

class ScoreboardActivity : AppCompatActivity() {
    lateinit var binding : ActivityScoreboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val score = intent.getIntExtra("score",5)
        binding.scoreNumber.text = score.toString()
    }
}