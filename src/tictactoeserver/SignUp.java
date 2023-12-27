/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;
import org.apache.derby.jdbc.ClientDriver;
import static tictactoeserver.ServerConnection.con;

/**
 *
 * @author Dr.Wlaa
 */
public class SignUp {

    private static byte[] salt;
    public static void signUpUser(String playerData) throws NoSuchAlgorithmException, SQLException{
        Gson gson = new GsonBuilder().create();
        Player player = gson.fromJson(playerData, Player.class);
        System.out.println(player.toString());
        player.setPassword(getPassword(player.getPassword()));
        player.setSalt(salt);
        insertToDatabase(player);
    }

    private static String getPassword(String password) throws NoSuchAlgorithmException {
        String hashedPassword = null;
            String algorithm = "SHA-1";
            salt = createSalt();
             hashedPassword = hashPassword(password, algorithm, salt);
            

        return hashedPassword;
    }

    private static int insertToDatabase(Player player) throws SQLException {
        int resultSet = -1;
        
            PreparedStatement statement = con.prepareStatement(
                    "INSERT INTO Player (USERNAME, EMAIL, PASSWORD, PLAYERSCORE, AVAILABLE, PLAYERIMAGE, "
                    + "SALT, isPlaying) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, player.getUserName());
            statement.setString(2, player.getEmail());
            statement.setString(3, player.getPassword());
            statement.setInt(4, 0);
            statement.setBoolean(5, true);
            statement.setString(6, null);
            statement.setBytes(7, player.getSalt());
            statement.setBoolean(8, false);
            resultSet = statement.executeUpdate();
               return resultSet;
    }

    private static String hashPassword(String data, String algorithm, byte[] salt) throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.reset();
        digest.update(salt);
        byte[] hash = digest.digest(data.getBytes());
        String sha1 = DatatypeConverter.printHexBinary(hash);
        return sha1;

    }

    private static byte[] createSalt() {
        byte[] bytes = new byte[5];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);
        return bytes;
    }

}
