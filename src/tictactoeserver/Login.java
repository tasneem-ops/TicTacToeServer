/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author s
 */
public class Login {
    
    public static Player checkLoginData(String loginData) throws SQLException, NoSuchAlgorithmException{
        Gson gson = new GsonBuilder().create();
        Player data = gson.fromJson(loginData, Player.class);
        PreparedStatement pst = ServerConnection.con.prepareStatement("select * from player where email = ?");
        pst.setString(1, data.getEmail());
        ResultSet rs = pst.executeQuery();
        if(rs.next()){
            byte[] salt = rs.getBytes("salt");
            String hashedSavedPassword = rs.getString("password");
            String hashedInputPassword = hashPassword(data.getPassword(), salt);
            System.out.println("Login Salt is:  " + salt.toString());
            System.out.println("Login saved SHA-1 is:  " + hashedSavedPassword);
            System.out.println("Login generated SHA-1 is:  " + hashedInputPassword);
            if(hashedSavedPassword.equals(hashedInputPassword)){
                Player player = getDataFromResultSet(rs);
                return player;
            }
        }
        else{
            return null;
        }
        return null;
    }
    
    private static Player getDataFromResultSet(ResultSet rs) throws SQLException {
        String userName = rs.getString("userName");
        String email = rs.getString("email");
        boolean available = rs.getBoolean("available");
        String playerImage = rs.getString("playerImage");
        long score = rs.getLong("score");
        boolean isPlaying = rs.getBoolean("isPlaying");
        
        return new Player(userName, email, available, playerImage, isPlaying, score);
    }
    
    private static byte[] generateSalt(){
        byte[] bytes = new byte[5];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);
        return bytes;
    }
    
    private static String hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException{
        MessageDigest msgDigest = MessageDigest.getInstance("SHA-1");
        msgDigest.reset();
        msgDigest.update(salt);
        byte[] hash = msgDigest.digest(password.getBytes());
        String sha1 = DatatypeConverter.printHexBinary(hash);
        return sha1;
    } 
    
}
