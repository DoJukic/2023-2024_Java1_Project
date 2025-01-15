/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.repo.blogpost;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Domi
 */
public class Blogpost {
    public static final DateTimeFormatter DATE_OFFSET_FORMATTER
            = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    
    public int id;
    public String title;
    public String link;
    public OffsetDateTime datePublished;
    public String description;
    public String encodedContent;
    public String imagePath;
    public Optional<List<Category>> categories = Optional.empty();
}
