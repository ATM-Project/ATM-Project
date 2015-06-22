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
    public enum method{DEPOSIT, WITHDRAW, TRANSFORM, LOOKUP, RESET};
    public method type;
    
    private boolean status;
    private String msg;
    private String pwd0, pwd1;
    
    private String amount;
    private String target;
    private ArrayList<tradeD> detail = new ArrayList<tradeD>();
    
    public query(method type){
        this.type = method.LOOKUP;
        this.amount = null;
    }
    
    public query(method type, String num){
        this.type = type;//DEPOSIT OR WITHDRAW OR UPDATE
        this.amount = num;
    }
    
    public query(method type, String num, String target){
        if (type == method.RESET){
            this.type = method.RESET;
            this.pwd0 = num;
            this.pwd1 = target;
        }
        else{
            this.type = method.TRANSFORM;
            this.amount = num;
            this.target = target;
        }
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
    public String getPwd0(){
        return this.pwd0;
    }
     public String getPwd1(){
        return this.pwd1;
    }
    public ArrayList<tradeD> getDetail(){
        return this.detail;
    }
}
