package pt.isec.a21280348.bigmath

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import pt.isec.a21280348.bigmath.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSettings.setOnClickListener {
            val intent = Intent(this,SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.btnProfile.setOnClickListener{
            val intent = Intent(this,ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.btnSinglePlayer.setOnClickListener{
            Toast.makeText(this,"Not implemented yet",Toast.LENGTH_SHORT).show()
        }

        binding.btnMultiplayer.setOnClickListener{
            Toast.makeText(this,"Not implemented yet",Toast.LENGTH_SHORT).show()
        }

        binding.btnCredits.setOnClickListener{
            Toast.makeText(this,"Not implemented yet",Toast.LENGTH_SHORT).show()
        }

        binding.btnExit.setOnClickListener{
            finish()
        }


    }
}