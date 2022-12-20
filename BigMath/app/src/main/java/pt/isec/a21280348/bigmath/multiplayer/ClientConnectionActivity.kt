package pt.isec.a21280348.bigmath.multiplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pt.isec.a21280348.bigmath.databinding.ActivityClientConnectionBinding

class ClientConnectionActivity : AppCompatActivity() {

    private lateinit var binding : ActivityClientConnectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientConnectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}