package pt.isec.a21280348.bigmath.multiplayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pt.isec.a21280348.bigmath.GameTableActivity
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class GameDataViewModel : ViewModel() {

    //TODO: Auto-generated, check what data needs to be here
    var table :  MutableList<Any>  = mutableListOf(20)
    var _levelLive : MutableLiveData<Int> = MutableLiveData<Int>().apply { value = 1 }
    var _timeLeftLive : MutableLiveData<Int> = MutableLiveData<Int>().apply { value = GameTableActivity.GAMETIME }
    var score : Int = 0
    var phase : Int = 1
    lateinit var levelLive : LiveData<Int>
    lateinit var timeLeftLive : LiveData<Int>
}


class GameViewModel : ViewModel() {

    companion object {
        const val SERVER_PORT = 9001


    }


    private var serverSocket: ServerSocket? = null

    private var threadsJogadores: ArrayList<Thread> = ArrayList()

    private var connectionState: ConnectionState? = null

    //Array that will have the information of each player connected and playing
    private var playersGameData: ArrayList<GameDataViewModel> = ArrayList()


    fun startLobby() {
        if (serverSocket != null)
            return


        //Thread para aguardar novos jogadores
        thread {
            //Create a new ServerSocket to listen for client connections. This call blocks until a connection is accepted from a client
            serverSocket = ServerSocket(SERVER_PORT)


                connectionState = ConnectionState.AWAITING_PLAYERS
                while (connectionState == ConnectionState.AWAITING_PLAYERS) {
                    //Listens for a connection to be made to this socket and accepts it. The method blocks until a connection is made.
                    val clientSocket = serverSocket?.accept()
                    if (clientSocket != null) {
                        playersGameData.add(GameDataViewModel())
                        thread { handleClient(clientSocket,playersGameData.last()) }

                    }
                }



        }
    }

    private fun handleClient(clientSocket: Socket, GameData: GameDataViewModel) {

        //check if the socket is valid
        if (clientSocket.isClosed)
            return

        //TODO: Handle player's game
    }

}