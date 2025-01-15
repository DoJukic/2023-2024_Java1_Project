/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.xml.archives;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Domi
 */
@XmlRootElement(name="head")
public class head { 
    public List<link> link;
}
