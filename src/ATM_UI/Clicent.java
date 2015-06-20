/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ATM_UI;

import Query.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.Query;

/**
 *
 * @author Administrator
 */
public class Clicent {
   public static ObjectOutputStream output;
   public static ObjectInputStream input;
   private static Socket Clicent;

public void runClicent(){
try{
connectToSever("s",1);
}catch(EOFException e){
System.out.println("Clicent terminated connection");
}catch(IOException e){
    e.printStackTrace();
}
finally{
    closeConnection();
}
}

   static void connectToSever(String ipaddress,int queue)throws IOException {
        System.out.println("Attempting connection");
        Clicent=new Socket(ipaddress,queue);
        System.out.println(Clicent.getInetAddress().getHostAddress());
    }
    static void getStreams() throws IOException{
    output=new ObjectOutputStream(Clicent.getOutputStream());
    output.flush();
    input=new ObjectInputStream(Clicent.getInputStream());
    }
    static void closeConnection(){
        try{
        output.close();
        input.close();
        Clicent.close();
        }catch(IOException e){
        e.printStackTrace();}
    }
    static void SendData(String message){
        try{
        output.writeObject(message);
        output.flush();
        }catch(IOException e){
        System.err.println("Error writing object");}
    }
static void SendData(Object b){
       try {
           output.writeObject(b);
           output.flush();
       }catch(SocketException e){
       } catch (IOException ex) {
           Logger.getLogger(Clicent.class.getName()).log(Level.SEVERE, null, ex);
       }
}
static Object ReceiveData(){
    Object obj=null;
       try {
           obj = input.readObject();
       }catch(EOFException e){
       showMessage.showmessage("与服务器断开连接");
       } catch(SocketException e){
           showMessage.showmessage("与服务器断开连接");
       } catch (IOException ex) {
           Logger.getLogger(Clicent.class.getName()).log(Level.SEVERE, null, ex);
       } catch (ClassNotFoundException ex) {
           Logger.getLogger(Clicent.class.getName()).log(Level.SEVERE, null, ex);
       }
       return obj;
}
    
    
    
static String recieveData(){
    String a=null;
       try {
           a= (String)input.readObject();
       }catch(SocketException e){
       showMessage.showmessage("服务器断开连接");
       }catch(EOFException e){
           showMessage.showmessage("服务器断开连接");
       }catch (IOException ex) {
           Logger.getLogger(Clicent.class.getName()).log(Level.SEVERE, null, ex);
       } catch (ClassNotFoundException ex) {
           Logger.getLogger(Clicent.class.getName()).log(Level.SEVERE, null, ex);
       }
       return a;
}
}



