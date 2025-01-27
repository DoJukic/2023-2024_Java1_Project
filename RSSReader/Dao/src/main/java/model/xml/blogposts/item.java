/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.xml.blogposts;

import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Domi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class item {
    @XmlTransient
    public static DateTimeFormatter INBOUND_DATE_FORMATTER = DateTimeFormatter.RFC_1123_DATE_TIME;
    
    @XmlElement
    public String title;
    @XmlElement
    public String link;
    @XmlElement
    public String pubDate;
    @XmlElement
    public List<String> category;
    @XmlElement
    public String description;
    @XmlElement(name = "content:encoded")
    public String contentEncoded;
}
