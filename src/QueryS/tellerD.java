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
public class tellerD implements java.io.Serializable{
    private String atmNo, ipAdd, money, currentUsr;
    
    public tellerD(String no,String ip, String money, String usr){
        this.atmNo = no;
        this.ipAdd = ip;
        this.money = money;
        this.currentUsr = usr;
    }
    
    public String getIp(){
        return this.ipAdd;
    }
    public String getNo(){
        return this.atmNo;
    }
    public String getMoney(){
        return this.money;
    }
    public String getUser(){
        return this.currentUsr;
    }
}
