/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.xml.archives;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author Domi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class link { 
    @XmlAttribute
    public String rel;
    @XmlAttribute
    public String title;
    @XmlAttribute
    public String href;
}
