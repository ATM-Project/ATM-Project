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
import Query.query.method;
import Query.queryS.methodS;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    
    private SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss z"); 
    
    private BasicDBObject currentUser = null;
    private static BasicDBObject myClient;
    
    Service(Socket socket, String serialNum){
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
        check_logout();
        mongo.close();
        try {
            //this.connection.getInputStream().close();
            //this.connection.getOutputStream().close();
            this.connection.close();
            System.out.println("Client "+this.serialNum+" down.");
        } catch (IOException ex) {
            System.out.println(this.serialNum+" close ERROR.");
            //Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void dealQuery(Object order){
        if (order instanceof login){
            check_login((login) order);//DONE
        }
        else if(order instanceof register){
            check_register((register) order);//DONE
        }
        else if(order instanceof query){
             check_query((query) order);//DONE
        }
        else if(order instanceof queryS){
             check_queryS((queryS) order);
        }
        else if(order instanceof Logout){
            check_logout();//DONE
        }
        else {
            check_error();//DONE
        }
    }
    
    private void check_login(login order){
        String name = order.getName();
        String pwd = order.getPwd();
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
                    if ((int)obj.get("wrongTimes") == 2){
                        output.writeObject(new login(false, "WRONG_PASSWORD_FIANL"));   output.flush();
                        users.update(user, new BasicDBObject().append("$set", new BasicDBObject("locked",true)));
                    }
                    else{
                        output.writeObject(new login(false, "WRONG_PASSWORD"));   output.flush();
                    }
                }
                else{
                    BasicDBObject setting = new BasicDBObject().append("checked", true);
                    this.currentUser = user;
                    users.update(user, new BasicDBObject().append("$set", setting));
                    
                    DBCollection clients = db.getCollection("clients");
                    clients.update(myClient, new BasicDBObject().append("$set", new BasicDBObject().append("currentUser", currentUser)));
                    
                    output.writeObject(new login(true, obj.get("money").toString()));   output.flush();
                    
                    System.out.println(user.get("name")+" logs in "+this.serialNum);
                }
            } catch (IOException ex) {
                Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    private void check_register(register order){
        String name = order.getName();
        String pwd = order.getPwd();
        BasicDBObject user = new BasicDBObject().append("name", name);
        DBCollection users = db.getCollection("users");
        DBObject obj = users.findOne(user);
        
        try{
            if (obj != null){
                output.writeObject(new register(false, "USER_EXSIT")); output.flush();
            }
            else{
                BasicDBObject tmp = new BasicDBObject().append("name",name).append("pwd", pwd).append("credit","").append("money", encrypt("0"))
                        .append("checked", false).append("locked", false).append("wrongTimes", 0);
                users.insert(tmp);
                output.writeObject(new register(true, ""));
            }
        }
        catch(IOException e){
            this.DBShutdown();
        }
    }
    
    private boolean checkNegative(double a, double b, double c){
        if (a<0||b<0||c<0) {
            try {
                output.writeObject(new query(false, "MONEY_NEGATIVE"));    output.flush();
                return false;
            } catch (IOException ex) {
                this.DBShutdown();
                Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }
    
    private void check_query(query order){
        
        
        try{
            query data = order; Date time = new Date();
            double money = 0;
            if (!data.getAmount().equals("null"))
                money = Double.parseDouble(new String(decrypty(data.getAmount())));
        
            DBCollection users = db.getCollection("users");
            DBCollection clients = db.getCollection("clients");
            DBCollection records = db.getCollection("records");
        
            BasicDBObject userQ = new BasicDBObject().append("name", currentUser.getString("name"));
            DBObject user = users.findOne(userQ);
            BasicDBObject clientQ = new BasicDBObject().append("ipAddr", ipAddr);
            DBObject client = clients.findOne(clientQ);
            
            //DEAL THE QUERY PART
            if (data.type == method.WITHDRAW){
                double user_money = Double.parseDouble(new String(decrypty(user.get("money").toString())));
                double client_money = Double.parseDouble(new String(decrypty(client.get("money").toString())));
                user_money -= money;    client_money -= money;
                
                if (checkNegative(user_money, client_money, money)){

                    String user_money_tmp = encrypt(user_money+""); String client_money_tmp = encrypt(client_money+"");
                    String user_tmp = "-"+money;
                
                    users.update(userQ, new BasicDBObject().append("$set", new BasicDBObject().append("money", user_money_tmp)));
                    records.insert(new BasicDBObject().append("ipAddr",this.ipAddr).append("serial", this.serialNum)
                                    .append("name", userQ.getString("name")).append("type", "WITHDRAW").append("delta", encrypt(user_tmp))
                                    .append("remains",user_money_tmp).append("time", formatter.format(time)));
                    clients.update(new BasicDBObject().append("ipAddr", ipAddr), 
                                    new BasicDBObject().append("$set", new BasicDBObject().append("money", client_money_tmp)));
                
                    output.writeObject(new query(true,"")); output.flush();
                }
            }
            
            
            else if (data.type == method.DEPOSIT){
                double user_money = Double.parseDouble(new String(decrypty(user.get("money").toString())));
                double client_money = Double.parseDouble(new String(decrypty(client.get("money").toString())));
                user_money += money;    client_money += money;
                if (checkNegative(user_money, client_money, money)){
                    String user_money_tmp = encrypt(user_money+""); String client_money_tmp = encrypt(client_money+"");
                    String user_tmp = "+"+money;
                    
                    users.update(userQ, new BasicDBObject().append("$set", new BasicDBObject().append("money", user_money_tmp)));
                    records.insert(new BasicDBObject().append("ipAddr",this.ipAddr).append("serial", this.serialNum)
                                    .append("name", userQ.getString("name")).append("type", "DEPOSIT").append("delta", encrypt(user_tmp))
                                    .append("remains",user_money_tmp).append("time", formatter.format(time)));
                    clients.update(new BasicDBObject().append("ipAddr", ipAddr), 
                                    new BasicDBObject().append("$set", new BasicDBObject().append("money", client_money_tmp)));
                
                    output.writeObject(new query(true,"")); output.flush();
                }
            }
            
            
            
            else if (data.type == method.TRANSFORM){
                BasicDBObject targetQ = new BasicDBObject().append("name", data.getTarget());
                DBObject target = users.findOne(targetQ);
                if (target == null){
                    output.writeObject(new query(false, "NO_SUCH_USER")); output.flush();
                }
                else if (target.get("name").toString().equals(currentUser.getString("name"))){
                    output.writeObject(new query(false, "NOT_ALLOWED")); output.flush();
                }
                else{
                    double user_money = Double.parseDouble(new String(decrypty(user.get("money").toString())));
                    double target_money = Double.parseDouble(new String(decrypty(target.get("money").toString())));
                    double client_money = Double.parseDouble(new String(decrypty(client.get("money").toString())));
                    target_money += money;  user_money -= money;
                    if (checkNegative(user_money, target_money, money)){
                        String target_money_tmp = encrypt(target_money+""); String user_money_tmp = encrypt(user_money+"");
                        String client_money_tmp = encrypt(client_money+"");
                        String target_tmp = "+"+money; String user_tmp = "-"+money;
                    
                        users.update(userQ, new BasicDBObject().append("$set", new BasicDBObject().append("money", user_money_tmp)));
                        users.update(targetQ, new BasicDBObject().append("$set", new BasicDBObject().append("money", target_money_tmp)));
                        records.insert(new BasicDBObject().append("ipAddr",this.ipAddr).append("serial", this.serialNum)
                                    .append("name", userQ.getString("name")).append("type", "TRANSFORM").append("delta", encrypt(user_tmp))
                                    .append("remains",user_money_tmp).append("time", formatter.format(time)));
                        records.insert(new BasicDBObject().append("ipAddr",this.ipAddr).append("serial", this.serialNum)
                                    .append("name", targetQ.getString("name")).append("type", "TRANSFORM").append("delta", encrypt(target_tmp))
                                    .append("remains",target_money_tmp).append("time", formatter.format(time)));
                        clients.update(new BasicDBObject().append("ipAddr", ipAddr), 
                                    new BasicDBObject().append("$set", new BasicDBObject().append("money", client_money_tmp)));
                    
                        output.writeObject(new query(true,"")); output.flush();
                    }
                }
            }
            
            
            else if (data.type == method.LOOKUP){
                DBCursor cursor = records.find(userQ);
                ArrayList<tradeD> details = new ArrayList<tradeD>();
                while(cursor.hasNext()){
                    DBObject tmp = cursor.next();
                    tradeD obj = new tradeD(tmp.get("serial").toString(), tmp.get("ipAddr").toString(), tmp.get("name").toString(),
                                     tmp.get("type").toString(), tmp.get("delta").toString(), tmp.get("remains").toString(),
                                     tmp.get("time").toString());
                    details.add(obj);
                }
                output.writeObject(new query(true, "", details));   output.flush();
            }
            
        }
        catch(java.lang.NullPointerException ex){
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
            this.DBShutdown();
        }
        catch (IOException ex) {
            this.DBShutdown();
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void check_queryS(queryS order){
        
    }
    
    private void check_logout(){
        if (currentUser != null){
            DBCollection users = db.getCollection("users");
            users.update(currentUser, new BasicDBObject().append("$set", new BasicDBObject().append("checked", false)));
            DBCollection clients = db.getCollection("clients");
            clients.update(myClient, new BasicDBObject().append("$set", new BasicDBObject().append("currentUser", null)));
            System.out.println(this.currentUser+" logged out @"+this.serialNum);
            this.currentUser = null;
        }
    }
    
    private void check_error(){
        try {
            output.writeObject(new String("WTF!")); output.flush();
            System.out.println("ERROR happened on "+this.serialNum);
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try{
            while(true){
                Object tmp = input.readObject();
                dealQuery(tmp);
            }
        }
        catch (ClassNotFoundException ex) { 
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
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
