package pt.isec.a21280348.bigmath

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import pt.isec.a21280348.bigmath.databinding.ActivityMainBinding
import pt.isec.a21280348.bigmath.multiplayer.ClientConnectionActivity


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
            val intent = Intent(this,GameTableActivity::class.java)
            startActivity(intent)
        }

        binding.btnMultiplayer.setOnClickListener{
            //Toast.makeText(this,"Not implemented yet",Toast.LENGTH_SHORT).show()

            val dlg = AlertDialog.Builder(this)
                .setTitle(R.string.multiplyer_title)
                .setMessage(R.string.msg_multiplyer_initializer)
                .setNeutralButton(R.string.server_btn) { _: DialogInterface, _: Int ->
                    startActivity(LobbyActivity.getServerModeIntent(this))
                }

                .setNegativeButton(R.string.cancel_btn) {  _: DialogInterface, _: Int ->
                    closeContextMenu()
                }
                .setPositiveButton(R.string.client_btn) { _: DialogInterface, _: Int ->
                    val intent = Intent(this, ClientConnectionActivity::class.java)
                    startActivity(intent)
                }

                .setCancelable(false)
                .create()

            dlg.show()

        }

        binding.btnCredits.setOnClickListener{
            val intent = Intent(this,CreditsActivity::class.java)
            startActivity(intent)
        }

        binding.btnLeaderboard.setOnClickListener{
            val intent = Intent(this,ScoreboardActivity::class.java)
            startActivity(intent)
        }

        binding.btnExit.setOnClickListener{
            finish()
        }


    }
}