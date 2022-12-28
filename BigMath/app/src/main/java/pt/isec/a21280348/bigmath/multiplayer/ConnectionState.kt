package pt.isec.a21280348.bigmath.multiplayer

enum class ConnectionState {
    AWAITING_PLAYERS,
    CONNECTION_ESTABLISHED,
    SETTING_PARAMETERS,
    CONNECTION_ERROR,
    CLIENT_CONNECTING
}