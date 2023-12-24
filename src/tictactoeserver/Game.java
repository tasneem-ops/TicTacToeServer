/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

import java.net.Socket;

/**
 *
 * @author ghon
 */
import com.google.gson.*;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game extends Thread{
    private static final int BOX00=1;
    private static final int BOX01=2;
    private static final int BOX02=3;
    private static final int BOX10=4;
    private static final int BOX11=5;
    private static final int BOX12=6;
    private static final int BOX20=7;
    private static final int BOX21=8;
    private static final int BOX22=9;
    private static final int WIN=10;
    private static final int DRAW=11;
    private static final int LOSE=12;
        final int col0 = 2;
        final int col1 = 3;
        final int col2 = 4;
        final int row0 = 5;
        final int row1 = 6;
        final int row2 = 7;
        final int diagonalLeft = 0;
        final int diagonalRight = 1;
            int movesCount = 0; // sum of moves 

            int[] winnerData;
            protected final boolean[][] boxEnabled; // array that hold enablle or disable to labels

    private boolean isX = true;
    protected int playerCases[][];
    int player1ScoreCount = 0;
    int player2ScoreCount = 0;
    int i;
    int j;


    private PlayerHandler playerX;
    private PlayerHandler playerO;
    ArrayList<Integer> filledBoxes;
    Gson gson;
    
    private Game(){
                       boxEnabled = new boolean[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boxEnabled[i][j] = true;
            }
        }
                winnerData = new int[2];
                playerCases = new int[2][8];
        filledBoxes=new ArrayList<Integer>();
    }
    public Game(PlayerHandler playerX, PlayerHandler playerO){
        this();
        this.playerX=playerX;
        this.playerO=playerO;
        playerX.suspend();
        playerO.suspend();
        gson = new GsonBuilder().create();
       // playerX.ps.println(gson.toJson(playerO.playerData));
       // playerO.ps.println(gson.toJson(playerX.playerData));
        playerX.ps.println(gson.toJson(new Move('x', 0)));
        playerO.ps.println(gson.toJson(new Move('o', 0)));
        this.start();
        
    }
    @Override 
    public void run(){
        while(true){
            try {
                String msg = playerX.dis.readLine();
                System.out.println("after read line");
                Move move = gson.fromJson(msg, Move.class);
                
                if(takeMove('x',move)==2){
                    playerX.resume();
                    playerO.resume();
                    break;
                }
                msg = playerO.dis.readLine();
                move = gson.fromJson(msg, Move.class);
                if(takeMove('o',move)==2){
                    playerX.resume();
                    playerO.resume();
                    break;
                }
            } catch (IOException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public int takeMove(char playerSign,Move move){
        if(playerSign=='x'){
            converter(move.getBox());
            if(!boxEnabled[i][j]){
                sendMove(playerX.ps,new Move(playerSign,99));
                return 0;
            }
            else{
                //sendMove(playerX.ps,move);

                sendMove(playerO.ps,move);
                                System.out.println("After X Player Send <> Move");

                converter(move.getBox());
                updateCases(i, j);
                movesCount++;

            }
        }
        if(playerSign=='o'){
       converter(move.getBox());
            if(!boxEnabled[i][j]){
                sendMove(playerO.ps,new Move(playerSign,99));
                return 0;
            }
            else{
                //sendMove(playerO.ps,move);
                sendMove(playerX.ps,move);
                System.out.println("After O player Send <>");
                 converter(move.getBox());
                updateCases(i, j);
                movesCount++;

            }
        }
         if (movesCount >= 5) {
                winnerData = checkWinner();
                if (winnerData[0] == -1) {
                    if (movesCount == 9) {
                sendMove(playerX.ps,new Move('d',11));
                sendMove(playerO.ps,new Move('d',11));
                return 2;
                    } else {
                        isX = !isX;
                        boxEnabled[i][j] = false;
                    }
                } else if (winnerData[0] == 0) {
                sendMove(playerX.ps,new Move('w',10));
                sendMove(playerO.ps,new Move('l',12));
                return 2;
                } else if (winnerData[0] == 1) {
                    sendMove(playerX.ps,new Move('l',12));
                    sendMove(playerO.ps,new Move('w',10));
                return 2;
                }

            } else {
                isX = !isX;
                boxEnabled[i][j] = false;
            }
         return 1;
    }
    private void sendMove(PrintStream ps,Move move){
          Gson gson = new GsonBuilder().create();
          ps.println(gson.toJson(move));
    }
    void updateCases(int finalI, int finalJ) {
        int x = isX ? 0 : 1;
        if (finalI == finalJ) {
            playerCases[x][diagonalLeft]++;
        }
        if (finalI + finalJ == 2) {
            playerCases[x][diagonalRight]++;
        }
        if (finalI == 0) {
            playerCases[x][row0]++;
        }
        if (finalI == 1) {
            playerCases[x][row1]++;
        }
        if (finalI == 2) {
            playerCases[x][row2]++;
        }
        if (finalJ == 0) {
            playerCases[x][col0]++;
        }
        if (finalJ == 1) {
            playerCases[x][col1]++;
        }
        if (finalJ == 2) {
            playerCases[x][col2]++;
        }
    }
    int[] checkWinner() {
        int winner[] = {-1, 0};
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 8; j++) {
                if (playerCases[i][j] == 3) {
                    winner[0] = i;
                    winner[1] = j;
                    return winner;
                }
            }
        }
        return winner;
    }
    void disableLabels() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boxEnabled[i][j] = false;
            }
        }
    }
    void converter(int x){
        switch(x){
            case 1:i=0; j=0; break;
            case 2:i=0; j=1; break;
            case 3:i=0; j=2; break;
            case 4:i=1; j=0; break;
            case 5:i=1; j=1; break;
            case 6:i=1; j=2; break;
            case 7:i=2; j=0; break;
            case 8:i=2; j=1; break;
            case 9:i=2; j=2; break;

        }
    }
}