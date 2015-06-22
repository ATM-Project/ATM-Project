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
    public enum methodS{ATM, USER, TRADE, UPDATE, UNLOCK};
    public methodS type;
    
    private String id;
    private boolean status;
    private ArrayList arrList;
    
    queryS(methodS type){
        this.type = type;// userList or atmList
    }
    
    
    queryS(boolean status){
        this.status = status;
    }
    queryS(boolean status, methodS type, ArrayList arr){
        this.status = status;
        this.type = type;
        this.arrList = arr;
    }
    
}
