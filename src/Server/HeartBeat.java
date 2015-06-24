/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.net.Socket;

/**
 *
 * @author MyPC
 */
public class HeartBeat implements Runnable{
    private Socket socket;
    
    public HeartBeat(Socket socket){
        this.socket = socket;
    }
    
    public void run(){
        while(!socket.isClosed()){
            
        }
    }
    
}
