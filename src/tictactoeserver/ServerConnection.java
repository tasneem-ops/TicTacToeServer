/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;
import org.apache.derby.jdbc.ClientDriver;

/**
 *
 * @author s
 */
public class ServerConnection extends Thread{
    ServerSocket serverSocket; 
    Socket socket;
    public static Connection con;
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
    
    public void startConnectionDB(){
        try {
            DriverManager.registerDriver(new ClientDriver());
            con = DriverManager.getConnection("jdbc:derby://localhost:1527/DBforTicTacToe", "a", "a");
        } catch (SQLException ex) {
            Logger.getLogger(ServerScreenBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}