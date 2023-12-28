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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author s
 */
public class PlayerHandler extends Thread {

    ArrayList<Player> avaliablePlayerList;
    static PreparedStatement psAvaliableUsers;
    Player playerData;
    DataInputStream dis;
    PrintStream ps;
    Socket socket;
    public static Vector<PlayerHandler> playersConnections;

    static {
        playersConnections = new Vector<PlayerHandler>();
    }

    public PlayerHandler(Socket socket) {
        avaliablePlayerList = new ArrayList<Player>();
        try {
            this.socket = socket;
            dis = new DataInputStream(socket.getInputStream());
            ps = new PrintStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        playersConnections.add(this);
        System.out.println("Player Handler is Created");
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            String msgArray;
            try {
                msgArray = dis.readLine();
                Gson gson = new GsonBuilder().create();
                ArrayList<String> messages = gson.fromJson(msgArray, ArrayList.class);
                String msg = messages.get(0);
                switch (msg) {
                    case "login":
                        loginUser(messages.get(1));
                        break;
                    case "signup":
                        signUpUser(messages.get(1));
                        break;
                    case "request":
                        requestToPlay(messages);
                        break;
                    case "accept":
                        startGame(messages);
                        break;
                    case "getAvailableUsers":
                        getAvailableUsers();
                        break;
                    case "logout":
                        logout();
                        break;
                    case "refuse":
                        sendRefuseMessage(messages);
                        break;
                }

            } catch (IOException ex) {
                Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, ex);
                closeConnections();
            }
        }
    }

    void closeConnections() {
        try {
            socket.close();
            dis.close();
            ps.close();
            this.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loginUser(String loginData) {
        try {
            Player player = Login.checkLoginData(loginData);
            if (player != null) {
                ArrayList<String> response = new ArrayList<>();
                response.add("login");
                response.add("Success");
                Gson gson = new GsonBuilder().create();
                String playerJson = gson.toJson(player);
                response.add(playerJson);
                String responseJSon = gson.toJson(response);
                ps.println(responseJSon);
                playerData = new Player(player);
                System.out.println("Player: " + player.toString());
                System.out.println("PlayerData: " + playerData.toString());
            } else {
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

    private void signUpUser(String playerData) {
        try {
            int result = SignUp.signUpUser(playerData);
            if(result == 0){
                System.out.println("signupresponse server");
                ArrayList<String> response = new ArrayList<>();
                response.add("signup");
                response.add("Success");
                Gson gson = new GsonBuilder().create();
                String responseJSon = gson.toJson(response);
                ps.println(responseJSon);
            }
            else if(result == 1){
                System.out.println("signupresponse server");
                ArrayList<String> response = new ArrayList<>();
                response.add("signup");
                response.add("Duplicate Username");
                Gson gson = new GsonBuilder().create();
                String responseJSon = gson.toJson(response);
                ps.println(responseJSon);
            }
            else if(result == 2){
                System.out.println("signupresponse server");
                ArrayList<String> response = new ArrayList<>();
                response.add("signup");
                response.add("Duplicate Email");
                Gson gson = new GsonBuilder().create();
                String responseJSon = gson.toJson(response);
                ps.println(responseJSon);
            }
            
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
            ArrayList<String> response = new ArrayList<>();
            response.add("signup");
            response.add("Failure");
            Gson gson = new GsonBuilder().create();
            String responseJSon = gson.toJson(response);
            ps.println(responseJSon);
        }
    }

    private void requestToPlay(ArrayList<String> request) {
        String player2UserName = request.get(1);
        System.out.println("Size of Vector:" + playersConnections.size());
        playersConnections.forEach(player -> {
            if(player.playerData != null){
                if (player.playerData.getUserName().equals(player2UserName)) {
                    System.out.println("Found Player with username:" + player2UserName);
                    ArrayList<String> response = new ArrayList<>();
                    response.add("request");
                    response.add(this.playerData.getUserName());
                    Gson gson = new GsonBuilder().create();
                    String responseJSon = gson.toJson(response);
                    player.ps.println(responseJSon);
                }
            }
        });
    }

    private void startGame(ArrayList<String> request) {
        String player1UserName = request.get(1);
        playersConnections.forEach(player -> {
            if(player.playerData != null){
                if (player.playerData.getUserName().equals(player1UserName)) {
                sendStartGame(player, this);
                updateInDB(player, this);
                Game game = new Game(player, this);
            }
            }
        });
    }

    private void getAvailableUsers() {
        System.out.println("Getting Available Users");
        try {
            psAvaliableUsers = ServerConnection.con.prepareStatement("SELECT * FROM PLAYER WHERE AVAILABLE=TRUE");
            ResultSet rs = psAvaliableUsers.executeQuery();
            while (rs.next()) {
                Player p1 = new Player();
                p1.setUserName(rs.getString("username"));
                p1.setEmail(rs.getString("email"));
                p1.setPlayerImage(rs.getString("playerimage"));
                p1.setScore(rs.getLong("score"));
                avaliablePlayerList.add(p1);
               
            }
            System.out.println("Available users are" + avaliablePlayerList.toString());
           ArrayList<String> response = new ArrayList<>();
             Gson gson = new GsonBuilder().create();
                response.add("AvailableUsers");
                String playerJson = gson.toJson(avaliablePlayerList);
                response.add(playerJson);
                String responseJSon = gson.toJson(response);
                System.out.println("Gson Response " + responseJSon);
                ps.println(responseJSon);
        } catch (SQLException ex) {
            Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void sendRefuseMessage(ArrayList<String> request){
        String player1UserName = request.get(1);
        playersConnections.forEach(player -> {
            if(player.playerData != null){
                if (player.playerData.getUserName().equals(player1UserName)) {
                    ArrayList<String> response = new ArrayList<>();
                    response.add("refuse");
                    response.add(this.playerData.getUserName());
                    Gson gson = new GsonBuilder().create();
                    String responseJSon = gson.toJson(response);
                    player.ps.println(responseJSon);
                }
            }
        });
    }

    private void logout() {
        //TODO: put logout code here
        //Recommended: put most of logic in seperate class with static methods
    }
    private void sendStartGame(PlayerHandler player1, PlayerHandler player2){
        ArrayList<String> response1 = new ArrayList<>();
        ArrayList<String> response2 = new ArrayList<>();
        response1.add("startGame");
        response2.add("startGame");
        response1.add(player2.playerData.getUserName());
        response2.add(player1.playerData.getUserName());
        Gson gson = new GsonBuilder().create();
        String responseJSon1 = gson.toJson(response1);
        String responseJSon2 = gson.toJson(response2);
        player1.ps.println(responseJSon1);
        player2.ps.println(responseJSon2);
    }
    
    private void updateInDB(PlayerHandler player1, PlayerHandler player2){
        try {
            PreparedStatement pst1 = ServerConnection.con.prepareStatement("update PLAYER SET ISPLAYING = ? WHERE USERNAME = ?");
            PreparedStatement pst2 = ServerConnection.con.prepareStatement("update PLAYER SET ISPLAYING = ? WHERE USERNAME = ?");
            pst1.setBoolean(1, true);
            pst1.setString(2, player1.playerData.getUserName());
            pst2.setBoolean(1, true);
            pst2.setString(2, player2.playerData.getUserName());
            int res1 = pst1.executeUpdate();
            int res2 = pst2.executeUpdate();
            System.out.println("Columns Updated:  "+ res1 + ""+ res2);
        } catch (SQLException ex) {
            Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
