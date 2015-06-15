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
public class query implements java.io.Serializable{
    public enum method{DEPOSIT, WITHDRAW, TRANSFORM, UPDATE, LOOKUP};
    public method type;
    
    private boolean status;
    private String msg;
    
    private String amount;
    private String target;
    private ArrayList<tradeD> detail = new ArrayList<tradeD>();
    
    public query(method type){
        this.type = method.LOOKUP;
    }
    
    public query(method type, String num){
        this.type = type;//DEPOSIT OR WITHDRAW
        this.amount = num;
    }
    
    public query(method type, String num, String target){
        this.type = method.TRANSFORM;
        this.amount = num;
        this.target = target;
    }
    
    
    public query(boolean status, String msg){
        this.status = status;
        this.msg = msg;
    }
    
    public query(boolean status,String msg, ArrayList<tradeD> obj){
        this.msg = msg;
        this.status = status;
        this.detail = obj;
    }
    
    
    public boolean getStatus(){
        return this.status;
    }
    public String getMsg(){
        return this.msg;
    }
    public String getAmount(){
        return this.amount;
    }
    public String getTarget(){
        return this.target;
    }
    public ArrayList<tradeD> getDetail(){
        return this.detail;
    }
}
