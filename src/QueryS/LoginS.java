/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QueryS;

/**
 *
 * @author MyPC
 */
public class LoginS implements java.io.Serializable{
    private String name, pwd;
    
    private boolean status;
    private String message;
    
    public LoginS(boolean status, String msg){
        this.status = status;
        this.message = msg;
    }
    
    public LoginS(String nm,String pwd){
        this.name = nm;
        this.pwd = pwd;
    }//Client send for Checkin
    
    public String getName(){
        return this.name;
    }
    public String getPwd(){
        return this.pwd;
    }
    public boolean getStatus(){
        return this.status;
    }
    public String getMsg(){
        return this.message;
    }
}
