package pt.isec.a21280348.bigmath.multiplayer

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pt.isec.a21280348.bigmath.MyViewModel
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class GameViewModel : ViewModel() {

    companion object {
        const val SERVER_PORT = 9001
    }

    private var serverSocket: ServerSocket? = null

    private var threadsJogadores: ArrayList<Thread> = ArrayList()

    private var socketsClients: ArrayList<Socket> = ArrayList()

    //public var connectionState: ConnectionState? = null

    private val _connectionState = MutableLiveData(ConnectionState.SETTING_PARAMETERS)
    val connectionState : LiveData<ConnectionState>
        get() = _connectionState

    //Array that will have the information of each player connected and playing
    //public var _playersGameData: ArrayList<MyViewModel> = ArrayList()

    public var _playersGameData: MutableLiveData<ArrayList<MyViewModel>> = MutableLiveData<ArrayList<MyViewModel>>().apply { value = ArrayList() }
    private var playersGameData : LiveData<ArrayList<MyViewModel>>?
        get() = _playersGameData
        set(value) {}

    //val _playersGameData : MutableList<MyViewModel>()

    //public var _playersGameData: MutableList<MyViewModel>

    private var clientSocket: Socket? = null

    private val socketI: InputStream?
        get() = clientSocket?.getInputStream()
    private val socketO: OutputStream?
        get() = clientSocket?.getOutputStream()

    //var players : MutableLiveData<ArrayList<MyViewModel>> = MutableLiveData<ArrayList<MyViewModel>>().apply { value = ArrayList() }

    /*
    private val _connectionState = MutableLiveData(ConnectionState.AWAITING_PLAYERS)
    val connectionState: LiveData<ConnectionState>
        get() = _connectionState;*/


    fun startLobby() {
        if (serverSocket != null)
            return

        var test : MyViewModel = MyViewModel()

        //playersGameData?.value?.add(test)

        //Log.e("TAG", _playersGameData.value.toString())

        Log.e("TAG","Waiting for clients1")

        //val clientsInfoTemp = _playersGameData

        //Thread para aguardar novos jogadores
        thread {
            //Create a new ServerSocket to listen for client connections. This call blocks until a connection is accepted from a client
            serverSocket = ServerSocket(SERVER_PORT)

            Log.e("TAG","Waiting for clients12")

                _connectionState.postValue(ConnectionState.AWAITING_PLAYERS)
                while (_connectionState.value != ConnectionState.AWAITING_PLAYERS) {
                    Log.e("TAG","Waiting for clients2")
                    //Listens for a connection to be made to this socket and accepts it. The method blocks until a connection is made.
                    val clientSocket = serverSocket?.accept()

                    Log.e("TAG","Client Connected")

                    if (clientSocket != null) {

/*
                        _playersGameData.postValue {
                            val newList = playersGameData.value ?: arrayListOf()
                            newList.add(MyViewModel())
                            newList
                        }*/
/*
                        _playersGameData.postValue {
                            val newList = playersGameData.value ?: arrayListOf()
                            newList.add(MyViewModel())
                            newList
                        }*/


                        _playersGameData.value?.add(MyViewModel())
                        _playersGameData.postValue(_playersGameData.value)
/*
                        playersGameData?.value?.add(MyViewModel())
                        _playersGameData.postValue(playersGameData?.value)*/
/*
                        _playersGameData.postValue {
                            val newList = _playersGameData.value ?: arrayListOf()
                            newList.add(MyViewModel())
                            newList
                        }

                        Log.e("TAG",_playersGameData.value.toString())*/

                        //_playersGameData.add(MyViewModel())

                        //playersGameData?.value?.add(MyViewModel())
                        //_playersGameData.postValue(playersGameData?.value?.add(MyViewModel()))
                        //_playersGameData.postValue(playersGameData?.value)
                        socketsClients.add(clientSocket)
                        Log.e("TAG",socketsClients.toString())
                        thread {
                            //playersGameData?.value?.let { handleClient(clientSocket, it.last()) }
                            _playersGameData.value?.let { handleClient(clientSocket, it.last()) }
                            //handleClient(clientSocket,MyViewModel())
                        }
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

    private fun handleClient(clientSocket: Socket, GameData: MyViewModel) {

        //check if the socket is valid
        if (clientSocket.isClosed)
            return
        //9024-9024/pt.isec.a21280348.bigmath W/Settings:

        //TODO: Handle player's game

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