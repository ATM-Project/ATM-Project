/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Query;

import java.util.Date;
import java.text.SimpleDateFormat;
/**
 *
 * @author MyPC
 */
public class tradeD implements java.io.Serializable{
    //"yyyy-MM-dd HH:mm:ss"
    //public static final SimpleDateFormat dFormat = new SimpleDateFormat("yyyyMMdd");
    private String atmNo, ipAddr;
    private String name, type, delta, remains;
    private String tDate;
    
    public tradeD(String no, String ipAddr, String name, String type, String delta, String remains, String date){
        this.atmNo = no;    this.ipAddr = ipAddr;   
        this.name = name; this.type = type;   this.delta = delta;   this.remains = remains;
        this.tDate = date;  
    }
    
    public String getDate(){
        return this.tDate;
    }
    public String getNo(){
        return this.atmNo;
    }
    public String getName(){
        return this.name;
    }
    public String getMethod(){
        return this.type;
    }
    public String getDelta(){
        return this.delta;
    }
    public String getRemain(){
        return this.remains;
    }
    
}
