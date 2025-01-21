/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hr.algebra.utilities.swing;

import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

/**
 *
 * @author Domi
 */
public class MiscUtils {
    
    private MiscUtils(){}
    
    // https://stackoverflow.com/questions/10119587/how-to-increase-the-slow-scroll-speed-on-a-jscrollpane
    public static void fixScrolling(JScrollPane scrollpane) {
        JLabel systemLabel = new JLabel();
        FontMetrics metrics = systemLabel.getFontMetrics(systemLabel.getFont());
        int lineHeight = metrics.getHeight();
        int charWidth = metrics.getMaxAdvance();

        JScrollBar systemVBar = new JScrollBar(JScrollBar.VERTICAL);
        JScrollBar systemHBar = new JScrollBar(JScrollBar.HORIZONTAL);
        int verticalIncrement = systemVBar.getUnitIncrement();
        int horizontalIncrement = systemHBar.getUnitIncrement();

        scrollpane.getVerticalScrollBar().setUnitIncrement(lineHeight * verticalIncrement);
        scrollpane.getHorizontalScrollBar().setUnitIncrement(charWidth * horizontalIncrement);
    }
    
    public static void assignSimpleStringListModelToJList(JList jList, List<String> stringList){
        jList.setModel(new javax.swing.AbstractListModel<String>() {
            List<String> strings = stringList;
            public int getSize() { return strings.size(); }
            public String getElementAt(int i) { return strings.get(i); }
        });
    }
    
    public static List<String> getStringListFromStringModel(ListModel<String> targetModel){
        ArrayList<String> strings = new ArrayList<>();
        
        for (var i = 0; i < targetModel.getSize(); i++){
            strings.add(targetModel.getElementAt(i));
        }
        
        return strings;
    }
}
