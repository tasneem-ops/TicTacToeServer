/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author s
 */
public class PlayerHandler extends Thread{
    Player playerData;
    DataInputStream dis;
    PrintStream ps;
    Socket socket;
    public static Vector<PlayerHandler> playersConnections;
    static {
        playersConnections = new Vector<PlayerHandler>();
    }
    
    public PlayerHandler(Socket socket){
        try {
            this.socket = socket;
            dis = new DataInputStream(socket.getInputStream());
            ps = new PrintStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        playersConnections.add(this);
        this.start();
    }
    @Override
    public void run() {
        while(true){
            String msg;
            try {
                msg = dis.readLine();
                
            } catch (IOException ex) {
                Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    void closeConnections(){
        try{
            socket.close();
            dis.close();
            ps.close();
            this.stop();
            playersConnections.remove(this);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}