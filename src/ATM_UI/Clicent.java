/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ATM_UI;

import Query.*;
import QueryS.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.Query;
import server_UI.login_ui;
import server_UI.servers_UI;

/**
 *
 * @author Administrator
 */
public class Clicent {
   public static ObjectOutputStream output;
   public static ObjectInputStream input;
   public static Socket Clicent =null;

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

  public  static void connectToSever(String ipaddress,int queue)throws IOException {
        System.out.println("Attempting connection");
        Clicent=new Socket(ipaddress,queue);
        System.out.println(Clicent.getInetAddress().getHostAddress());
        Clicent.setOOBInline(false);
    }
   public static void getStreams() throws IOException{
    output=new ObjectOutputStream(Clicent.getOutputStream());
    output.flush();
    input=new ObjectInputStream(Clicent.getInputStream());
    }
 public   static void closeConnection(){
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
 public static void SendData(Object b){
       try {
 //          if(b instanceof Query)
//           contactEditorUI.setEnable(false);
           output.writeObject(b);
           output.flush();
//       } catch(NullPointerException e){
//           showMessage.showmessage("未连接服务器");
       } catch (IOException ex) {
           Logger.getLogger(Clicent.class.getName()).log(Level.SEVERE, null, ex);
       }
}
public static Object ReceiveData(){
    Object obj=null;
       try {
           obj = input.readObject();
 //          if(obj instanceof Query)
 //          contactEditorUI.setEnable(true);
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



