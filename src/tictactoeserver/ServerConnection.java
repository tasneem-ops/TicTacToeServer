/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author s
 */
public class ServerConnection extends Thread{
    ServerSocket serverSocket;
    Socket socket;
    @Override
    public void run(){
        try {
            serverSocket = new ServerSocket(Constants.PORT);
            while(true){
                socket = serverSocket.accept();
                new PlayerHandler(socket);
            }
        }
        catch (IOException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}