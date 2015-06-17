/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Query;

/**
 *
 * @author MyPC
 */
public class register implements java.io.Serializable{
    private String name, pwd;
    private boolean status;
    private String msg;
    
    public register(String name, String pwd){
        this.name = name;
        this.pwd = pwd;
    }
    
    public register(boolean status, String msg){
        this.status = status;
        this.msg = msg;
    }
    
    public String getName(){
        return this.name;
    }
    public String getPwd(){
        return this.pwd;
    }
    public String getMsg(){
        return this.msg;
    }
    public boolean getStatus(){
        return this.status;
    }
    
    
}
