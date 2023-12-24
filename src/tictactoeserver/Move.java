/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

/**
 *
 * @author ghon
 */
class Move {
    private char sign;
    private int move;
    public Move(){}
    public Move(char sign,int move){
        this.sign=sign;
        this.move=move;
    }
}
