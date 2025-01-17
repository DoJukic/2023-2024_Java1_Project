/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.xml.blogposts;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Domi
 */
@XmlRootElement(name="rss")
public class rss {
    public channel channel;
}
