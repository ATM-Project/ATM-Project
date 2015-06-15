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
    private String tDate;
    private String atmNo;
    private String method, amount, remains;
    
    tradeD(String date, String no, String method, String amount, String remains){
        this.tDate = date;  this.atmNo = no;
        this.method = method;   this.amount = amount;
        this.remains = remains;
    }
    
    public String getDate(){
        return this.tDate;
    }
    public String getNo(){
        return this.atmNo;
    }
    public String getMethod(){
        return this.method;
    }
    public String getAmount(){
        return this.amount;
    }
    public String getRemain(){
        return this.remains;
    }
    
}
