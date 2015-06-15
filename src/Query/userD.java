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
public class userD implements java.io.Serializable{
    private String name, money, credit;
    private boolean locked;
    
    userD(){}
    userD(String name, String money, boolean locked){
        this.name = name;
        this.money = money;
        this.locked = locked;
    }
    
    public String getName(){
        return this.name;
    }
    public String getMoney(){
        return this.name;
    }
    public String getCredit(){
        return this.name;
    }
    public boolean getStatus(){
        return this.locked;
    }
    
}
