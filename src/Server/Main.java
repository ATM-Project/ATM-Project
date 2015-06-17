/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

//import java.util.concurrent.ThreadPoolExecutor;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Query.query;

/**
 *
 * @author MyPC
 */
public class Main{
    
    private static final int port = 4242;
    private static final int maxLink = 10;
    
    private static ArrayList<Socket> linkPool = new ArrayList<Socket>();
    
    private static boolean judge(Socket socket){
        String addr  = socket.getInetAddress().getHostAddress();
        for (Socket link : linkPool) {
            String tmp = link.getInetAddress().getHostAddress();
            if (tmp.equals(addr)&&(link.isConnected())) return false;
        }
        return true;
    }
    
    private static String zeroFill(int num){
        String tmp = String.valueOf(num);
        while(tmp.length() < 4){
            tmp = "0" + tmp;
        }
        return tmp;
    }
     
    public static void main(String[] args){
        ServerSocket server = null;
        ExecutorService threadPool = Executors.newFixedThreadPool(maxLink);
        int count = 0;
        
        try{
            server = new ServerSocket(port, maxLink);
        }
        catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Server Start.");
        while(true){
            try{
                Socket socket = server.accept();
                String ipAdd = socket.getInetAddress().getHostAddress();
                if (judge(socket)){
                    count++;
                    System.out.println("Link  "+zeroFill(count)+"  Start");
                    linkPool.add(socket);
                    Service tmp = new Service(server.accept(), zeroFill(count));
                    threadPool.execute(tmp);
                }
                else{
                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                    output.writeObject(new query(false, "RUNTIMEERROR"));   output.flush();
                    socket.close();   
                }
                
            }
            catch(IOException e){
                System.exit(1);
            }
        }
        
    }
    
}
