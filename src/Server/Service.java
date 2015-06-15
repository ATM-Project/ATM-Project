/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

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
import java.net.SocketException;

import static security.Security.encrypt;
import static security.Security.decrypty;

/**
 *
 * @author MyPC
 */
public class Service implements Runnable{
    private Socket connection;
    private String ipAddr, serialNum;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    
    private Mongo mongo = null;
    private DB db = null;
    
    Service(Socket socket, String serialNum){
        this.connection = socket;
        this.serialNum = serialNum;
        this.ipAddr = socket.getInetAddress().getHostAddress();
        addClient();
        
        try{
            connection.setSoTimeout(30000);
            input = new ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
        }
        catch(IOException e){
            e.printStackTrace();
            this.DBShutdown();
            System.exit(1);
        }
    }
    
    private void addClient(){
        DBCollection client = db.getCollection("clients");
        DBObject obj = client.findOne(new BasicDBObject().append("ipAddr", this.ipAddr));
        if (obj == null){
            BasicDBObject newObj = new BasicDBObject().append("ipAddr", this.ipAddr).append("SerialNum", this.serialNum).
                                        append("money", encrypt("100000"));
            client.insert(newObj);
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
        System.exit(1);
    }
    
    private void dealQuery(Object order){
        if (order instanceof login){
            check_login((login) order);
        }
        else if(order instanceof register){
            check_register((register) order);
        }
        else if(order instanceof query){
             check_query((query) order);      
        }
        else if(order instanceof queryS){
             check_queryS((queryS) order);
        }
        else{
            
        }
    }
    
    private void check_login(login order){
        String name = order.getName();
        String pwd = encrypt(order.getPwd());
        BasicDBObject user = new BasicDBObject().append("name", name);
        DBCollection users = db.getCollection("users");
        DBObject obj = users.findOne(user);
            try {
                if (obj == null){
                    output.writeObject(new login(false, "NO_SUCH_USER"));   output.flush();
                }
                else if((boolean)obj.get("checked")){
                    output.writeObject(new login(false, "USER_CHECKED"));   output.flush();
                }
                else if ((boolean)obj.get("locked")){
                    output.writeObject(new login(false, "USER_LOCKED"));   output.flush();
                }
                else if (!pwd.equals(obj.get("pwd").toString())){
                    users.update(user, new BasicDBObject().append("$inc", new BasicDBObject().append("wrongTimes", 1)));
                    if ((int)obj.get("wrongTimes") >= 2 ){
                        output.writeObject(new login(false, "WRONG_PASSWORD_FIANL"));   output.flush();
                    }
                    else{
                        output.writeObject(new login(false, "WRONG_PASSWORD"));   output.flush();
                    }
                }
                else{
                    BasicDBObject setting = new BasicDBObject().append("checked", true);
                    users.update(user, new BasicDBObject().append("$set", setting));
                    output.writeObject(new login(true, obj.get("money").toString()));   output.flush();
                }
            } catch (IOException ex) {
                Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    private void check_register(register order){
        
    }
    
    
    private void check_query(query order){
        
    }
    private void check_queryS(queryS order){
        
    }

    @Override
    public void run() {
        this.DBConnect();
        
        try{
            while(true){
                connection.sendUrgentData(0xFF);
                Object tmp = input.readObject();
                dealQuery(tmp);
            }
        }
        catch(IOException e){
            e.printStackTrace();
            this.DBShutdown(); 
            System.exit(1);
        }catch (ClassNotFoundException ex) {
            //send back: ERROR
            //Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
