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
public class userD implements java.io.Serializable{
    private String name;
    private boolean locked;
    
    public userD(String name, boolean status){
        this.name = name;
        this.locked = status;
    }
    
    public String getName(){
        return this.name;
    }
    public boolean getStatus(){
        return this.locked;
    }
    
}
