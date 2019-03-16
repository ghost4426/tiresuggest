/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Gian Tran
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder
        = {
            "tire"
        })
@XmlRootElement(name = "tires")
public class TireList {

    @XmlElement(required = true)
    private List<Tire> tires;

    public List<Tire> getTires() {
        if(tires == null){
            tires = new ArrayList<>();
        }
        return tires;
    }

    public void setTires(List<Tire> tires) {
        this.tires = tires;
    }
    
    public int getSize(){
        return this.getSize();
    }
    
}
