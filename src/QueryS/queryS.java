/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QueryS;

import java.util.ArrayList;

/**
 *
 * @author MyPC
 */
public class queryS implements java.io.Serializable{
    public enum methodS{ATM, USER, TRADE, UPDATE, UNLOCK, RESET};
    public methodS type;
    
    private String name, money;
    private String pwd0, pwd1;
    
    private boolean status;
    private String message;
    private ArrayList<tellerD> tellerList;
    private ArrayList<tradeD> tradeList;
    private ArrayList<userD> userList;
    
    public queryS(methodS type){
        this.type = type;
    }
    
    public queryS(methodS type, String t1, String t2){
        this.type = methodS.RESET;
        this.pwd0 = t1;
        this.pwd1 = t2;
    }
    public queryS(methodS type, String name){
        this.type = type;
        
        if (type == methodS.UPDATE)
            this.money = name;
        else 
            this.name = name;
    }
    
    public queryS(boolean status, String msg){
        this.status = status;
        this.message = msg;
    }
    
    public queryS(boolean status, methodS type, ArrayList arr){
        this.status = status;
        if (type == methodS.ATM){
            this.tellerList = arr;
        }
        else if (type == methodS.TRADE){
            this.tradeList = arr;
        }
        else if (type == methodS.USER) {
            this.userList = arr;
        }
    }
    
    public boolean getStatus(){
        return this.status;
    }
    public String getName(){
        return this.name;
    }
    public String getMoney(){
        return this.money;
    }
    public String getMsg(){
        return this.message;
    }
    public String getPwd0(){
        return this.pwd0;
    }
    public String getPwd1(){
        return this.pwd1;
    }
    public ArrayList<tellerD> getTeller(){
        return this.tellerList;
    }
    public ArrayList<tradeD> getTrade(){
        return this.tradeList;
    }
    public ArrayList<userD> getUser(){
        return this.userList;
    }
}
