package pt.isec.a21280348.bigmath.multiplayer

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import pt.isec.a21280348.bigmath.LobbyActivity
import pt.isec.a21280348.bigmath.R
import pt.isec.a21280348.bigmath.databinding.ActivityClientConnectionBinding

class ClientConnectionActivity : AppCompatActivity() {

    private lateinit var binding : ActivityClientConnectionBinding
    private val model: GameViewModel by viewModels ()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientConnectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnClientConnect.setOnClickListener{
            var strIp = binding.tfIPAdressByClient.text.toString()

            if(strIp.isEmpty() || !Patterns.IP_ADDRESS.matcher(strIp).matches()){
                Toast.makeText(this@ClientConnectionActivity, R.string.error_address , Toast.LENGTH_LONG)
                    .show()
            } else {
                /*
                * TODO: Redirect client to lobby as client mode
                *       Add this client to lobby list of clients
                * */
                model.startClient(strIp)
                startActivity(LobbyActivity.getClientModeIntent(this))
            }
        }

        binding.btnClientConnectEmulator.setOnClickListener {
            Toast.makeText(this@ClientConnectionActivity, "Not implemented yet" , Toast.LENGTH_LONG)
                .show()
            /*
                * TODO: Redirect client to lobby as client mode
                *       Add this client to lobby list of clients
                *
                *
                * model.startClient("10.0.2.2", SERVER_PORT-1)
                    // Configure port redirect on the Server Emulator:
                    // telnet localhost <5554|5556|5558|...>
                    // auth <key>
                    // redir add tcp:9998:9999
                * */
        }
    }



}