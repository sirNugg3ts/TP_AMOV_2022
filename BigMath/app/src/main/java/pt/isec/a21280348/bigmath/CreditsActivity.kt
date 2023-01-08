package pt.isec.a21280348.bigmath

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pt.isec.a21280348.bigmath.databinding.ActivityCreditsBinding

class CreditsActivity : AppCompatActivity() {
 private lateinit var binding : ActivityCreditsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreditsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.credits)
    }
}