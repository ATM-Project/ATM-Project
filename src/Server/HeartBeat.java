/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MyPC
 */
public class HeartBeat{
    private Socket socket;
    private int timeSet;
    private Timer heartBeatTimer;
    private TimerTask heartBeatTask;
    
    public HeartBeat(Socket socket, int timeSet){
        this.socket = socket;
        this.timeSet = timeSet;
        this.start();
    }
    
    public void start(){
        heartBeatTimer = new Timer();  
        heartBeatTask = new TimerTask() {  
  
            @Override  
            public void run() {  
                try {
                    // TODO Auto-generated method stub
                        if (socket.getInputStream().available()==0){
                            throw new IOException();
                        }
                } catch (IOException ex) {
                    try {
                        socket.close();
                        System.out.println("HEART FATAL CONNECTION");
                    } catch (IOException ex1) {
                        Logger.getLogger(HeartBeat.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
            }  
        };  
        heartBeatTimer.schedule(heartBeatTask, 0, timeSet);  
    }
    
    public void stop(){
        this.heartBeatTimer.cancel();
    }
    
}
