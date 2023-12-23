/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
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
            String msgArray;
            try {
                msgArray = dis.readLine();
                Gson gson = new GsonBuilder().create();
                ArrayList<String> messages = gson.fromJson(msgArray, ArrayList.class);
                String msg = messages.get(0);
                switch(msg){
                    case "login":
                        loginUser(messages.get(1));
                        break;
                    case "signup":
                        signUpUser();
                        break;
                    case "request":
                        requestToPlay(messages);
                        break;
                    case "accept":
                        startGame(messages);
                        break;
                }
                
            } catch (IOException ex) {
                Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, ex);
                closeConnections();
            }
        }
    }
    void closeConnections(){
        try{
            socket.close();
            dis.close();
            ps.close();
            this.stop();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    private void loginUser(String loginData){
        try {
            Player player = Login.checkLoginData(loginData);
            if(player != null){
                ArrayList<String> response = new ArrayList<>();
                response.add("login");
                response.add("Success");
                Gson gson = new GsonBuilder().create();
                String playerJson = gson.toJson(player);
                response.add(playerJson);
                String responseJSon = gson.toJson(response);
                ps.println(responseJSon);
            }
            else{
                ArrayList<String> response = new ArrayList<>();
                response.add("login");
                response.add("Failure");
                Gson gson = new GsonBuilder().create();
                String responseJSon = gson.toJson(response);
                ps.println(responseJSon);
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void signUpUser(){
        //TODO: put signup code here
        //Recommended: put most of logic in seperate class with static methods
    }
    
    private void requestToPlay(ArrayList<String> request){
        String player2UserName = request.get(1);
        playersConnections.forEach(player ->{
            if(player.playerData.getUserName().equals(player2UserName)){
                ArrayList<String> response = new ArrayList<>();
                response.add("request");
                response.add(this.playerData.getUserName());
                Gson gson = new GsonBuilder().create();
                String responseJSon = gson.toJson(response);
                player.ps.println(responseJSon);
            }  
        });
    }
    
    private void startGame(ArrayList<String> request){
        String player1UserName = request.get(1);
        playersConnections.forEach(player ->{
            if(player.playerData.getUserName().equals(player1UserName)){
                ArrayList<String> response1 = new ArrayList<>();
                ArrayList<String> response2 = new ArrayList<>();
                response1.add("startGame");
                response2.add("startGame");
                response1.add(this.playerData.getUserName());
                response2.add(player.playerData.getUserName());
                Gson gson = new GsonBuilder().create();
                String responseJSon1 = gson.toJson(response1);
                String responseJSon2 = gson.toJson(response2);
                player.ps.println(responseJSon1);
                this.ps.println(responseJSon2);
                
                //GameSession game = new GameSession(player, this);
            }  
        });
    }
}