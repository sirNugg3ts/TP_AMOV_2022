package pt.isec.a21280348.bigmath

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog

import androidx.core.view.get
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pt.isec.a21280348.bigmath.databinding.ActivityGameTableBinding
import pt.isec.a21280348.bigmath.multiplayer.ConnectionState
import kotlin.concurrent.thread

import androidx.activity.viewModels

import androidx.activity.addCallback



class MyViewModel : ViewModel(){

    companion object {
        const val SERVER_PORT = 9999
    }

    var table :  MutableList<Any>  = mutableListOf(20)
    var _levelLive : MutableLiveData<Int> = MutableLiveData<Int>().apply { value = 1 }
    var levelLive : LiveData<Int>
        get() = _levelLive
        set(value) {}

    var _timeLeftLive : MutableLiveData<Int> = MutableLiveData<Int>().apply { value = GameTableActivity.GAMETIME }
    var score : Int = 0
    var phase : Int = 1
    var totalGameTime : Int = 0
    lateinit var timeLeftLive : LiveData<Int>

    private val _connectionState = MutableLiveData(ConnectionState.AWAITING_PLAYERS)
    val connectionState: LiveData<ConnectionState>
        get() = _connectionState
/*
    private var socket: Socket? = null
    private val socketI : InputStream?
        get() = socket?.getInputStream()

    private val socketO : OutputStream?
        get() = socket?.getOutputStream()

    private var serverSocket: ServerSocket? = null

    private var threadComm: Thread? = null


    fun startServer() {
        if(serverSocket != null || socket != null ||
                _connectionState.value != ConnectionState.SETTING_PARAMETERS)
            return;

        _connectionState.postValue(ConnectionState.SERVER_CONNECTING)

        thread {
            serverSocket = ServerSocket(SERVER_PORT)
            serverSocket?.run {
                try {
                    val socketClient = serverSocket!!.accept()
                    startComm(socketClient)
                } catch (_: Exception){
                    _connectionState.postValue(ConnectionState.CONNECTION_ERROR)
                } finally {
                    serverSocket?.close()
                    serverSocket = null
                }
            }
        }
    }

    fun stopServer() {
        serverSocket?.close()
        _connectionState.postValue(ConnectionState.CONNECTION_ENDED)
        serverSocket = null
    }

    fun startClient(serverIP : String, serverPort: Int = SERVER_PORT) {
        if(socket != null || _connectionState.value != ConnectionState.SETTING_PARAMETERS)
            return

        thread {
            _connectionState.postValue(ConnectionState.CLIENT_CONNECTING)
            try {
                val newSocket = Socket()
                newSocket.connect(InetSocketAddress(serverIP,serverPort),5000)
                startComm(newSocket)

            } catch (_: Exception){
                _connectionState.postValue(ConnectionState.CONNECTION_ERROR)
                stopGame()
            }
        }
    }


    private fun startComm(newSocket: Socket) {
        if(threadComm != null)
            return

        socket = newSocket

        threadComm = thread {
            try {
                if(socketI == null)
                    return@thread

                _connectionState.postValue(ConnectionState.CONNECTION_ESTABLISHED)
                val bufI = socketI!!.bufferedReader()

            } catch (_: Exception){

            } finally { }
        }
    }

    fun stopGame() {
        try {
            //_state.postValue(State.GAME_OVER)
            _connectionState.postValue(ConnectionState.CONNECTION_ERROR)
            socket?.close()
            socket = null
            threadComm?.interrupt()
            threadComm = null
        } catch (_: Exception) { }
    }*/
}

class GameTableActivity : AppCompatActivity() {
    data class GameInfo(var currentScore : Int,var inTurn : Boolean)
    private val model: MyViewModel by viewModels()
    private lateinit var binding : ActivityGameTableBinding
    private var _levelLive : MutableLiveData<Int> = MutableLiveData<Int>().apply { value = 1 }
    private var _timeLeftLive : MutableLiveData<Int> = MutableLiveData<Int>().apply { value = GAMETIME }
    private lateinit var menuItem : MenuItem
    private var totalGameTime = 0
    private lateinit var timeThread : Thread
    var threadStop : Boolean = false


    private var levelLive : LiveData<Int>
        get() = _levelLive
        set(value) {}

    private var timeLeftLive : LiveData<Int>
        get() = _timeLeftLive
        set(value) {}

    lateinit var gameTable : GameTable
    var info : GameInfo = GameInfo(0,false)
    var paused : Boolean = false
    var firstObserved : Boolean = true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameTableBinding.inflate(layoutInflater)
        setContentView(binding.root)


        gameTable = GameTable(this)
        gameTable.setActivityBinding(binding)
        gameTable.setGameInfo(info)
        gameTable.setLiveData(_levelLive,_timeLeftLive)
        binding.gameTableId.addView(gameTable)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.app_name)



        binding.btnPause.setOnClickListener {
            if (info.inTurn) {
                paused = !paused
                if (paused) {
                    if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                        binding.btnPause.setImageResource(R.drawable.ic_baseline_play_arrow_42)
                    else
                        binding.btnPause.setImageResource(R.drawable.ic_baseline_play_arrow_14)
                } else {
                    if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                        binding.btnPause.setImageResource(R.drawable.ic_baseline_pause_42)
                    else
                        binding.btnPause.setImageResource(R.drawable.ic_baseline_pause_14   )

                }
            }
        }

        timeThread = Thread{
            Log.i("THREAD","Thread iniciado")
            while(_timeLeftLive.value!! > 0) {
                Log.i("THREAD",_timeLeftLive.value!!.toString())
                if(info.inTurn){
                }
                else if (!paused) {
                    _timeLeftLive.postValue( (_timeLeftLive.value!! - 1))
                    totalGameTime++
                }
                Thread.sleep(1000)
                if(threadStop)
                    break
            }
            if(!threadStop) {
                val intent = Intent(this, ScoreboardActivity::class.java)
                intent.putExtra(scoreTAG, gameTable.getFinalScore())
                intent.putExtra(totalTimeTAG, totalGameTime)
                startActivity(intent)
            }
        }


        timeThread.start()


        onBackPressedDispatcher.addCallback(this ) {
            val builder = AlertDialog.Builder(this@GameTableActivity)
            builder.setTitle(getString(R.string.leave)).setMessage(getString(R.string.quitQuestion))

            builder.setPositiveButton(android.R.string.ok){ dialog,which ->
                finish()
                threadStop = true
            }
            builder.setNegativeButton(android.R.string.cancel){ dialog,which ->
            }
            builder.show()
        }
    }






    override fun onStart() {
        super.onStart()
        Log.i("Start","onStart")

        gameTable.gameStart()
        registLevelObserver()
        registTimeObserver()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater : MenuInflater = menuInflater
        Log.i("MENU","MENU CRIADO")
        inflater.inflate(R.menu.table_menu,menu)
        menuItem = menu[0]
        menuItem.title = getString(R.string.level)+ " " + _levelLive.value.toString()
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i("SAVE","WILL SAVE")
        model.table = gameTable.getGameTable()
        model._levelLive = _levelLive
        model._timeLeftLive = _timeLeftLive
        model.timeLeftLive = timeLeftLive
        model.levelLive = levelLive
        model.score = gameTable.getFinalScore()
        model.phase = gameTable.getPhase()
        model.totalGameTime = totalGameTime
        threadStop = true
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.i("RESTORE","WILL RESTORE")
        Log.i("RESTORE",model.table.toString())
        //menuItem.title = "Level: " + _levelLive.value.toString()
        _levelLive = model._levelLive
        _timeLeftLive = model._timeLeftLive
        timeLeftLive = model.timeLeftLive
        levelLive = model.levelLive
        totalGameTime = model.totalGameTime
        info = GameInfo(model.score,false)
        gameTable.restoreState(false,model.table,info,model.phase,_levelLive,_timeLeftLive)

        firstObserved = true
        registLevelObserver()
        registTimeObserver()
    }

    private fun registTimeObserver(){
        timeLeftLive.observe(this){
            Log.i("TIME",_timeLeftLive.value.toString())
            Log.i(totalTimeTAG,totalGameTime.toString() )
            binding.timeCounter.text = _timeLeftLive.value.toString()
        }
    }

    private fun registLevelObserver(){
        levelLive.observe(this){
            if(firstObserved) {
                firstObserved = false
            }
            else {
                tableReset()
                getString(R.string.user_file_key)
                binding.levelView.text = getString(R.string.nextLevel) + " " + 5 + " " +  getString(R.string.seconds) + "!"
                binding.timeCounter.text = ""
                if(60 - (5 * (_levelLive.value!!-1)) > 20)
                    _timeLeftLive.value = 60 - (5 * (_levelLive.value!!-1))
                else
                    _timeLeftLive.value = 20
                thread {
                    var pausetime = 5
                    runOnUiThread {
                        menuItem.title = getString(R.string.level)+ " "+ _levelLive.value.toString()
                    }
                    if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        runOnUiThread {
                            binding.btnPause.setImageResource(R.drawable.ic_baseline_pause_42)
                        }
                    } else{
                        runOnUiThread {
                            binding.btnPause.setImageResource(R.drawable.ic_baseline_pause_14)
                        }
                    }
                    while(pausetime > 0){
                        if(!paused){
                            pausetime -=1
                            runOnUiThread {
                                binding.levelView.text = getString(R.string.nextLevel) + " "+ pausetime+ " " + getString(R.string.seconds) + "!"
                            }
                        }
                        Thread.sleep(1000)
                    }
                    runOnUiThread {
                        binding.levelView.text = ""
                        binding.levelPhase.text = ""
                    }
                    if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        runOnUiThread {
                            binding.btnPause.setImageResource(R.drawable.ic_outline_empty_origin_42)
                        }
                    }
                    else {
                        runOnUiThread {
                            binding.btnPause.setImageResource(R.drawable.ic_outline_empty_origin_14)
                        }
                    }
                    info.inTurn = false
                    gameTable.gameStart()
                }
            }
        }

    }


    private fun tableReset(){
        binding.cell1.text = ""
        binding.cell2.text = ""
        binding.cell3.text = ""
        binding.cell4.text = ""
        binding.cell5.text = ""
        binding.cell6.text = ""
        binding.cell7.text = ""
        binding.cell8.text = ""
        binding.cell9.text = ""
        binding.cell10.text = ""
        binding.cell11.text = ""
        binding.cell12.text = ""
        binding.cell13.text = ""
        binding.cell14.text = ""
        binding.cell15.text = ""
        binding.cell16.text = ""
        binding.cell17.text = ""
        binding.cell18.text = ""
        binding.cell19.text = ""
        binding.cell20.text = ""
        binding.cell21.text = ""
        binding.cell22.text = ""
        binding.cell23.text = ""
        binding.cell24.text = ""
        binding.cell25.text = ""
    }

    companion object{
        val GAMETIME : Int = 60
        val scoreTAG = "score"
        val totalTimeTAG = "totalTime"
    }

}