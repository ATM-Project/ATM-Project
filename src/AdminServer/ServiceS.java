/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AdminServer;

import java.net.UnknownHostException;
import com.mongodb.DBObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import Query.*;
import Query.query.method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import static security.Security.encrypt;

/**
 *
 * @author MyPC
 */
public class ServiceS implements Runnable{
    private Socket connection;
    private String ipAddr, serialNum;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    
    private Mongo mongo = null;
    private DB db = null;
    
    private SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss z"); 
    
    private BasicDBObject currentUser = null;
    private static BasicDBObject myClient;
    
        ServiceS(Socket socket, String serialNum){
        //System.out.println(socket);
        this.connection = socket;
        this.serialNum = serialNum;      
        this.ipAddr = socket.getInetAddress().getHostAddress();
        myClient = new BasicDBObject().append("ipAddr", ipAddr);
        this.DBConnect();
        this.addClient();
        System.out.println(this.serialNum+" on.");
        try{
            //connection.setSoTimeout(30000);
            input = new ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
        }
        catch(IOException e){
            //e.printStackTrace();
            this.DBShutdown();
        }
    }
        
    private void addClient(){
        try{
            DBCollection client = db.getCollection("clients");
            DBObject obj = client.findOne(new BasicDBObject().append("ipAddr", this.ipAddr));
            if (obj == null){
                BasicDBObject newObj = new BasicDBObject().append("ipAddr", this.ipAddr).append("SerialNum", this.serialNum).
                                        append("money", encrypt("100000")).append("currentUser", "null");
                client.insert(newObj);
            }
            else{
                this.serialNum = obj.get("SerialNum").toString();
            }
        }
        catch(java.lang.NullPointerException e){
            e.printStackTrace();
        }
    }
    
    private void DBConnect(){
        try{
            mongo = new Mongo(new ServerAddress("",27017));
            db = mongo.getDB("bank");
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
            }catch (MongoException e) {
            e.printStackTrace();
            }
    }
    
    private void DBShutdown(){
        mongo.close();
        try {
            this.connection.close();
            System.out.println("Client "+this.serialNum+" down.");
        } catch (IOException ex) {
            System.out.println(this.serialNum+" close ERROR.");
            //Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    
    @Override
    public void run(){
        
    }
}
