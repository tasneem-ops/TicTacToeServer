/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

/**
 *
 * @author s
 */
public class Player {
    private String userName;
    private String password;
    private String email;
    private boolean available;
    private String playerImage;
    private byte[] salt;
    private boolean isPlaying;
    private long score;
   
    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getPlayerImage() {
        return playerImage;
    }

    public boolean isIsPlaying() {
        return isPlaying;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setPlayerImage(String playerImage) {
        this.playerImage = playerImage;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }
  
    public Player(String userName, String password, String email, boolean available, String playerImage) {
        
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.available = available;
        this.playerImage = playerImage;
    }

    public Player(String userName, String email, boolean available, String playerImage, boolean isPlaying, long score) {
        this.userName = userName;
        this.email = email;
        this.available = available;
        this.playerImage = playerImage;
        this.isPlaying = isPlaying;
        this.score = score;
        this.password = null;
        this.salt = null;
    }
    
    

    public Player() {
    }

    @Override
    public String toString() {
        return "Player{" + "userName=" + userName + ", password=" + password + ", email=" + email + ", available=" + available + ", playerImage=" + playerImage + ", salt=" + salt + '}';
    }

    public String getPassword() {
        return password;
    }

    public byte[] getSalt() {
        return salt;
    }
    public Player(Player player) {
        this.userName = player.userName;
        this.available = player.available;
        this.email = player.email;
        this.isPlaying = player.isPlaying;
        this.score = player.score;
        this.playerImage = player.playerImage;
        this.password = player.password;
        this.salt = player.salt;
    }
   
   
}
