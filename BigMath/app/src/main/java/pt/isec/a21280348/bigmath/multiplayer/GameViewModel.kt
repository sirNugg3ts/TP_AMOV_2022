package pt.isec.a21280348.bigmath.multiplayer

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONArray
import org.json.JSONObject
import pt.isec.a21280348.bigmath.MyViewModel
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.StandardCharsets
import kotlin.concurrent.thread

class GameData{
    var table : List<Any> = listOf(20)
    var levelLive : Int = 1
    var timeLeftLive : Int = 0
    var score : Int = 0
    var phase : Int = 1
}



class GameViewModel : ViewModel() {

    companion object {
        const val SERVER_PORT = 9001
    }

    private var serverSocket: ServerSocket? = null

    private var socketsClients: ArrayList<Socket> = ArrayList()

    private val _gameState = MutableLiveData(GameState.WAITING_FOR_PLAYERS)

    private val _connectionState = MutableLiveData(ConnectionState.SETTING_PARAMETERS)
    val connectionState : LiveData<ConnectionState>
        get() = _connectionState


    private var clientSocket: Socket? = null

    private val socketI: InputStream?
        get() = clientSocket?.getInputStream()
    private val socketO: OutputStream?
        get() = clientSocket?.getOutputStream()


    fun startLobby() {
        if (serverSocket != null)
            return

        Log.e("TAG","Waiting for clients 1")


        //Thread para aguardar novos jogadores
        thread {
            //Create a new ServerSocket to listen for client connections. This call blocks until a connection is accepted from a client
            serverSocket = ServerSocket(SERVER_PORT)

            Log.e("TAG","Waiting for clients 2")

                _connectionState.postValue(ConnectionState.AWAITING_PLAYERS)
                while (_connectionState.value == ConnectionState.AWAITING_PLAYERS) {
                    Log.e("TAG","Waiting for clients 3")
                    //Listens for a connection to be made to this socket and accepts it. The method blocks until a connection is made.
                    try{
                        val clientSocket = serverSocket!!.accept()
                        Log.e("TAG","Client Connected!")

                        if (clientSocket != null) {

                            socketsClients.add(clientSocket)
                            Log.e("TAG",socketsClients.toString())
                            thread {
                                handleClient(clientSocket)
                            }
                        }
                    }catch (_: Exception){
                        _connectionState.postValue(ConnectionState.CONNECTION_ERROR)
                    }
                }
        }
    }

    fun startClient(serverIP: String, serverPort: Int = SERVER_PORT){
        if(clientSocket != null ){
            return
        }

        thread {
            _connectionState.postValue(ConnectionState.CLIENT_CONNECTING)
            try {
                val newSocket = Socket()
                newSocket.connect(InetSocketAddress(serverIP,serverPort), 5000)
                Log.i("TAG","Client Connected")
            } catch (_: Exception ) {
                _connectionState.postValue(ConnectionState.CONNECTION_ERROR)
                stopGame()
            }
        }
    }

    private fun handleClient(clientSocket: Socket) {

        //check if the socket is valid
        if (clientSocket.isClosed)
            throw Exception("Socket is closed")
        //9024-9024/pt.isec.a21280348.bigmath W/Settings:

        var _clientConnectionState : MutableLiveData<ConnectionState> = MutableLiveData(ConnectionState.CONNECTION_ESTABLISHED)

        //TODO: Handle player's game
        var gameData : GameData = GameData()
        thread {
            try{
                if (socketI == null)
                    return@thread

                val InputStreamReader = InputStreamReader(socketI, StandardCharsets.UTF_8)
                val OutputStreamWriter = OutputStreamWriter(socketO,StandardCharsets.UTF_8)

                //Wait for gameState to not be WAITING_FOR_PLAYERS
                //TODO: Instead of thread sleep, use a wait/notify mechanism

                while (_gameState.value == GameState.WAITING_FOR_PLAYERS) {
                    Thread.sleep(100)
                }

                //TODO: wait for server to create table and put it in gameData

                if (_gameState.value == GameState.WAITING_FOR_ANSWER) {
                    //Game is on, send the question
                    val jsonObject : JSONObject = JSONObject()
                    //Add gameData to jsonObject
                    jsonObject.put("table",JSONArray(gameData.table))
                    jsonObject.put("levelLive",gameData.levelLive)
                    jsonObject.put("timeLeftLive",gameData.timeLeftLive)
                    jsonObject.put("score",gameData.score)
                    jsonObject.put("phase",gameData.phase)

                    //Send jsonObject to client
                    OutputStreamWriter.write(jsonObject.toString())

                    //Wait for answer
                    val answer = InputStreamReader.read()

                    //decode answer as jsonObject
                    val jsonObjectAnswer : JSONObject = JSONObject(answer.toString())

                    //TODO: Check if answer is correct
                    //TODO: Update gameData
                    //TODO: Update _gameState
                    //TODO: Send new question if client is still playing


                }

            }catch (_: Exception){
               // _connectionState.postValue(ConnectionState.CONNECTION_ERROR)
            }finally {
                //Terminar jogo
            }
        }


    }

    fun stopGame(){
        try {
            _connectionState.postValue(ConnectionState.CONNECTION_ERROR)
            clientSocket?.close()
            clientSocket = null

        } catch (_: Exception) {
            Log.i("GameViewModel","Exception Stop Game")
        }
    }
}