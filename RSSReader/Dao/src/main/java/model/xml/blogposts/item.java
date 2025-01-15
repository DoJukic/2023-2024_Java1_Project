/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.xml.blogposts;

import java.time.OffsetDateTime;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Domi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class item {
    @XmlElement
    public String title;
    @XmlElement
    public String link;
    @XmlElement
    public OffsetDateTime pubDate;
    @XmlElement
    public List<String> category;
    @XmlElement
    public String description;
    @XmlElement(name = "content:encoded")
    public String contentEncoded;
}
