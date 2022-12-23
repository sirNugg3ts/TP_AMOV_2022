package pt.isec.a21280348.bigmath

import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Telephony.Sms
import android.telephony.SmsManager
import android.text.InputFilter
import android.text.InputType
import android.text.Spanned
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import pt.isec.a21280348.bigmath.databinding.ActivityLobbyBinding
import pt.isec.a21280348.bigmath.multiplayer.ConnectionState

class LobbyActivity : AppCompatActivity() {

    companion object {
        private const val SERVER_MODE = 0
        private const val CLIENT_MODE = 1

        private const val TAG = "LobbyAcivity"

        fun getServerModeIntent(context : Context) : Intent {
            return Intent(context,LobbyActivity::class.java).apply {
                putExtra("mode", SERVER_MODE)
            }
        }

        fun getClientModeIntent(context : Context) : Intent {
            return Intent(context,LobbyActivity::class.java).apply {
                putExtra("mode", CLIENT_MODE)
            }
        }
    }

    private lateinit var binding: ActivityLobbyBinding
    private val model : MyViewModel by viewModels()
    private var strIP : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(model.connectionState.value != ConnectionState.CONNECTION_ESTABLISHED){
            when(intent.getIntExtra("mode", SERVER_MODE)){
                SERVER_MODE -> startAsServer()
                CLIENT_MODE -> startAsClient()
            }
        }

/*
        binding.btnInvitePlayers.setOnClickListener {
            logi("clicked invite players button")
        }*/

        binding.btnInvitePlayers.setOnClickListener {

            logi("clicked invite players button")

            val edtBox = EditText(this).apply {
                maxLines = 1
                hint = "(+351)"
                inputType = InputType.TYPE_CLASS_PHONE
                filters = arrayOf(object : InputFilter {
                    override fun filter(
                        source: CharSequence?,
                        start: Int,
                        end: Int,
                        dest: Spanned?,
                        dstart: Int,
                        dend: Int
                    ): CharSequence? {
                        source?.run {
                            var ret = ""
                            forEach {
                                if (it.isDigit())
                                    ret += it
                            }
                            return ret
                        }
                        return null
                    }

                })
            }

            var dlg = AlertDialog.Builder(this)
                .setTitle("Invite a client")
                .setMessage("Insert a phone number to invite to play! ")
                .setPositiveButton("Send") { _: DialogInterface, _: Int ->
                    val strNumber = edtBox.text.toString()
                    if(strNumber.isEmpty() || !Patterns.PHONE.matcher(strNumber).matches()){
                        Toast.makeText(this@LobbyActivity, "Invalid phone number!", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        //val sentPI: PendingIntent = PendingIntent.getBroadcast(this, 0, Intent("SMS_SENT"), 0)
                        //SmsManager.getDefault().sendTextMessage(strNumber, null, "Join the Big Math multiplayer game: using this IP adress: " + strIP, sentPI, null)


                        try {
/*
                            val uri = Uri.parse("smsto:" + strNumber)
                            val intent = Intent(Intent.ACTION_SENDTO, uri)
                            intent.putExtra("Ola", "Here goes your message...")
                            startActivity(intent)*/

                            var smsManager:SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                this.getSystemService(SmsManager::class.java)
                            } else {
                                TODO("VERSION.SDK_INT < M")
                            }

                            smsManager.sendTextMessage(
                                strNumber,
                                null,
                                "Join the Big Math multiplayer game: using this IP adress:  $strIP",
                                null,
                                null
                            )

                            //sendSMS()

                            Toast.makeText(applicationContext, "Message Sent", Toast.LENGTH_LONG)
                                .show()

                        } catch (e: Exception){
                            Toast.makeText(applicationContext, "Please enter all the data.."+e.message.toString(), Toast.LENGTH_LONG)
                                .show()
                        }
                        /*
                        var obj = SmsManager.getDefault()
                        obj.sendTextMessage(strNumber,
                            null,
                            "Join the Big Math multiplayer game: using this IP adress: " + strIP,
                            null,
                            null
                        )*/
                    }
                }
                .setNegativeButton(R.string.cancel_btn) { _: DialogInterface, _: Int ->
                }
                .setCancelable(false)
                .setView(edtBox)
                .create()

            dlg.show()
        }

    }

    private fun sendSMS() {
        val uri = Uri.parse("smsto:" + strIP)
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra(("Join the Big Math multiplayer game: using this IP adress: $strIP"), "Here goes your message...")
        startActivity(intent)
    }

    private fun logi(message: String){
        Log.i(TAG,message)
    }

    private fun startAsServer() {
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val ip = wifiManager.connectionInfo.ipAddress
        val strIpAdress = String.format("%d.%d.%d.%d" ,
            ip and 0xff,
            (ip shr 8) and 0xff,
            (ip shr 16) and 0xff,
            (ip shr 24) and 0xff
        )

        strIP = strIpAdress

        binding.txtMessageLobbyIp.text = String.format(getString(R.string.msg_ip_address),strIpAdress)
        binding.btnInvitePlayers.visibility = View.VISIBLE
        binding.btnStartGame.visibility = View.VISIBLE
/*
        binding.btnInvitePlayers.setOnClickListener {

            val edtBox = EditText(this).apply {
                maxLines = 1
                hint = "(+351)"
                filters = arrayOf(object : InputFilter {
                    override fun filter(
                        source: CharSequence?,
                        start: Int,
                        end: Int,
                        dest: Spanned?,
                        dstart: Int,
                        dend: Int
                    ): CharSequence? {
                        source?.run {
                            var ret = ""
                            forEach {
                                if (it.isDigit() || it == '.')
                                    ret += it
                            }
                            return ret
                        }
                        return null
                    }

                })
            }

            var dlg = AlertDialog.Builder(this)
                .setTitle("Invite a client")
                .setMessage("Insert a phone number to invite to play!")
                .setPositiveButton("Send") { _: DialogInterface, _: Int ->
                    val strNumber = edtBox.text.toString()
                    if(strNumber.isEmpty() || !Patterns.PHONE.matcher(strNumber).matches()){
                        Toast.makeText(this@LobbyActivity, "Invalid phone number!", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        var obj = SmsManager.getDefault()
                        obj.sendTextMessage(strNumber,
                            null,
                            "Join the Big Math multiplayer game: using this IP adress: " + strIpAdress,
                            null,
                            null
                        )
                    }
                }
                .setNegativeButton(R.string.cancel_btn) { _: DialogInterface, _: Int ->
                    finish()
                }
                .setCancelable(false)
                .setView(edtBox)
                .create()

            dlg.show()
        }*/

    }

    private fun startAsClient() {
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val ip = wifiManager.connectionInfo.ipAddress
        val strIpAdress = String.format("%d.%d.%d.%d" ,
            ip and 0xff,
            (ip shr 8) and 0xff,
            (ip shr 16) and 0xff,
            (ip shr 24) and 0xff
        )

        binding.txtMessageLobbyIp.text = String.format("Waiting for server to start game...")
        binding.btnInvitePlayers.visibility = View.INVISIBLE
        binding.btnStartGame.visibility = View.INVISIBLE
    }
}