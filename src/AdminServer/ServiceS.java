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

import QueryS.*;
import QueryS.queryS.methodS;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author MyPC
 */
public class ServiceS implements Runnable{
    private HeartBeat abS;
    private Socket connection;
    private String ipAddr, serialNum;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    
    private Mongo mongo = null;
    private DB db = null;
    private DBCollection admins=null, users=null, records=null, clients=null;
    
    private SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss z"); 
    
    private BasicDBObject currentUser = null;
    
    ServiceS(Socket socket, String serialNum){
        //this.abS = new HeartBeat(socket, 2000);
        //System.out.println(socket);
        this.connection = socket;
        this.serialNum = serialNum;      
        this.ipAddr = socket.getInetAddress().getHostAddress();
        this.DBConnect();
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
    
    private void DBConnect(){
        try{
            mongo = new Mongo(new ServerAddress("",27017));
            db = mongo.getDB("bank");
            admins = db.getCollection("admins");
            users = db.getCollection("users");
            clients = db.getCollection("clients");
            records = db.getCollection("records");
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
            }catch (MongoException e) {
            e.printStackTrace();
            }
    }
    
    private void DBShutdown(){
        //this.abS.stop();
        check_logout();
        mongo.close();
        try {  
            this.connection.close();
            System.out.println("Link "+this.serialNum+" down.");
        } catch (IOException ex) {
            System.out.println(this.serialNum+" close ERROR.");
            //Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void dealQuery(Object order){
        if (order instanceof LoginS){
            check_login((LoginS) order);//DONE
        }
        else if(order instanceof queryS){
             check_query((queryS) order);//DONE
        }
        else if(order instanceof LogoutS){
            check_logout();//DONE
        }
        else {
            check_error();//DONE
        }
    }
    
    private ArrayList getData(DBCollection collection, methodS type, String name){
        ArrayList data = new ArrayList<>();
        DBCursor cursor;
        
        
        if (type == methodS.ATM){
            cursor = collection.find();
            while(cursor.hasNext()){
                DBObject obj = cursor.next();
                String no = obj.get("SerialNum").toString();    String ip = obj.get("ipAddr").toString();
                String money = obj.get("money").toString();     String usr = obj.get("currentUser").toString();
                data.add(new tellerD(no, ip, money, usr));
            }
        }
        else if (type == methodS.USER){
            cursor = collection.find(new BasicDBObject("name", name));
            while(cursor.hasNext()){
                DBObject obj = cursor.next();
                String usr = obj.get("name").toString();    boolean ip = (boolean)obj.get("locked");
                data.add(new userD(usr, ip));
            }
        }
        else if (type == methodS.TRADE){
            cursor = collection.find(new BasicDBObject("ipAddr", name));
            while(cursor.hasNext()){
                DBObject obj = cursor.next();
                String no = obj.get("serial").toString();    String ip = obj.get("ipAddr").toString();
                String method = obj.get("type").toString();     String usr = obj.get("name").toString();
                String delta = obj.get("delta").toString();     String remains = obj.get("remains").toString();
                String date = obj.get("time").toString();       
                data.add(new tradeD(no, ip, usr, method, delta, remains, date));
            }
        }
        return data;
    }
    
    private void check_query(queryS order){
        try{
            if (order.type == methodS.UNLOCK){
                BasicDBObject user = new BasicDBObject().append("name",order.getName());
                DBObject obj = users.findOne(user);
                if (obj==null||(!(boolean)obj.get("locked"))) {
                    output.writeObject(new queryS(false, "WTF!")); output.flush();
                }
                else{
                    users.update(user, new BasicDBObject().append("$set", new BasicDBObject().append("locked", false)));
                    output.writeObject(new queryS(true, "UNLOCKED")); output.flush();
                }
            }
            
            else if (order.type == methodS.RESET){
                DBObject admin = admins.findOne(currentUser);
                String pwd = admin.get("pwd").toString();
                if (order.getPwd0().equals(pwd)&&(order.getPwd1()!=null)){
                    admins.update(currentUser, new BasicDBObject().append("$set", new BasicDBObject().append("pwd", order.getPwd1())));
                    output.writeObject(new queryS(true, "Success")); output.flush();
                }
                else{
                    output.writeObject(new queryS(false, "wrongPwd")); output.flush();
                }
            }
            
            
            else if (order.type == methodS.UPDATE){
                
            }
            
            
            else{
                ArrayList tmp = new ArrayList<>();
                if (order.type == methodS.ATM){
                    tmp = (ArrayList<tellerD>)getData(clients ,order.type, order.getName());
                }
                else if (order.type == methodS.USER){
                    tmp = (ArrayList<userD>)getData(users ,order.type, order.getName());
                }
                else if (order.type == methodS.TRADE){
                    tmp = (ArrayList<tradeD>)getData(records ,order.type, order.getName());
                }
                
                if (tmp.isEmpty()){
                    output.writeObject(new queryS(false, order.type, null)); output.flush();
                }
                else{
                    output.writeObject(new queryS(true, order.type, tmp)); output.flush();
                }
                
            }
        }
        catch(IOException ex){
            Logger.getLogger(ServiceS.class.getName()).log(Level.SEVERE, null, ex);
            this.DBShutdown();
        }
    }
    
    private void check_login(LoginS order){
        String name = order.getName();
        String pwd = order.getPwd();
        BasicDBObject user = new BasicDBObject().append("name", name);
        DBObject obj = admins.findOne(user);
        
            try {
                if (obj == null){
                    output.writeObject(new LoginS(false, "NO_SUCH_USER"));   output.flush();
                }
                else if((boolean)obj.get("checked")){
                    output.writeObject(new LoginS(false, "USER_CHECKED"));   output.flush();
                }
                else if ((boolean)obj.get("locked")){
                    output.writeObject(new LoginS(false, "USER_LOCKED"));   output.flush();
                }
                else if (!pwd.equals(obj.get("pwd").toString())){
                    output.writeObject(new LoginS(false, "WRONG_PASSWORD"));   output.flush();      
                }
                else{                   
                    BasicDBObject setting = new BasicDBObject().append("checked", true)
                                                    .append("Last_Time", this.formatter.format(new Date()));
                    this.currentUser = user;
                    admins.update(user, new BasicDBObject().append("$set", setting));
                    
                    output.writeObject(new LoginS(true, obj.get("Last_Time").toString()));   output.flush();                
                    System.out.println(user.get("name")+" logs @"+this.serialNum);
                }
            } catch (IOException ex) {
                Logger.getLogger(ServiceS.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    private void check_logout(){
        if (currentUser != null){
            admins.update(currentUser, new BasicDBObject().append("$set", new BasicDBObject().append("checked", false)));
            admins.update(currentUser, new BasicDBObject().append("$set", new BasicDBObject().append("checked", false)));
            System.out.println(this.currentUser.get("name").toString()+" logged out @"+this.serialNum);
            this.currentUser = null;
        }
    }
    
    private void check_error(){
        try {
            output.writeObject(new String("WTF!")); output.flush();
            System.out.println("ERROR happened on "+this.serialNum);
        } catch (IOException ex) {
            Logger.getLogger(ServiceS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    @Override
    public void run(){
        try{
            while(true){
                Object tmp = input.readObject();
                dealQuery(tmp);
            }
        }
        catch (ClassNotFoundException ex) { 
            Logger.getLogger(ServiceS.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(java.net.SocketException e){
            //System.out.println(this.serialNum+" down.");
            this.DBShutdown(); 
        }
        catch(IOException e){
            //e.printStackTrace();
            this.DBShutdown(); 
        }
    }
}
