/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hr.algebra.rss_gui.view.model;

import hr.algebra.rss_gui.RSS_GUI;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.repo.blogpost.Blogpost;

/**
 *
 * @author Domi
 */
public class BlogpostTableModel extends AbstractTableModel{
    
    private static final String[] COLUMN_NAMES = {
        "Id", 
        "Title", 
        "Link", 
        "Published date",
        "Description",
        "ImagePath"
    };
    
    public Blogpost getTheActualThingyPls(int i){
        return blogposts.get(i);
    }
    
    private List<Blogpost> blogposts;

    public BlogpostTableModel(List<Blogpost> blogposts) {
        this.blogposts = blogposts;
    }

    public void setblogposts(List<Blogpost> blogposts) {
        this.blogposts = blogposts;
        fireTableDataChanged();
    }
    
    @Override
    public int getRowCount() {
        return blogposts.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch(columnIndex) {
            case 0:
                return Integer.class;
        }
        return super.getColumnClass(columnIndex); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }
    
    

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return blogposts.get(rowIndex).id;
            case 1:
                return blogposts.get(rowIndex).title;
            case 2:
                return blogposts.get(rowIndex).link;
            case 3:
                return blogposts.get(rowIndex).datePublished.toLocalDate().toString() + ", " + blogposts.get(rowIndex).datePublished.toLocalTime().toString();
            case 4:
                return blogposts.get(rowIndex).description;
            case 5:
                return blogposts.get(rowIndex).imagePath;
            default:
                throw new RuntimeException("No such column");
        }
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }
}
