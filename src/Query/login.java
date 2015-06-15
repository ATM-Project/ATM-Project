/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Query;

import java.util.ArrayList;

/**
 *
 * @author MyPC
 */
public class login implements java.io.Serializable{
    private String name, pwd;
    
    private boolean status;
    private String money, credit;
    private ArrayList mBox;
    
    public login(boolean status, String money){
        this.status = status;
        this.money = money;
    }
    
    public login(String nm,String pwd){
        this.name = nm;
        this.pwd = pwd;
    }//Client send for Checkin
    
    public String getName(){
        return this.name;
    }
    public String getPwd(){
        return this.pwd;
    }
    public String getMoney(){
        return this.money;
    }
    public String getCredit(){
        return this.credit;
    }
    public boolean getStatus(){
        return this.status;
    }
    public ArrayList getMsg(){
        return this.mBox;
    }

}
