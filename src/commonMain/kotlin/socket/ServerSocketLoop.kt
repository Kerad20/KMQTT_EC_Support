package socket

import mqtt.Broker
import mqtt.ClientConnection
import socket.tls.TLSServerSocket

open class ServerSocketLoop(private val broker: Broker) {

    private val serverSocket = if (broker.tlsSettings == null) ServerSocket(broker) else TLSServerSocket(broker)

    fun run() {
        while (serverSocket.isRunning()) {
            serverSocket.select(500) { clientConnection, state ->
                try {
                    handleEvent(clientConnection, state)
                    return@select true
                } catch (e: SocketClosedException) {
                    clientConnection.closedGracefully()
                    return@select false
                } catch (e: IOException) {
                    clientConnection.closedWithException()
                    return@select false
                }
            }
            broker.cleanUpOperations()
        }
    }

    private fun handleEvent(clientConnection: ClientConnection, state: SocketState) {
        when (state) {
            SocketState.READ -> {
                clientConnection.client.read()?.let {
                    clientConnection.dataReceived(it)
                }
            }
            SocketState.WRITE -> {
                clientConnection.client.sendRemaining()
            }
        }
    }

    fun stop() {
        serverSocket.close()
    }

    enum class SocketState {
        READ,
        WRITE
    }
}