/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package hr.algebra.rss_gui;

import dal.IRepository;
import dal.RepositoryFactory;
import hr.algebra.rss_gui.view.BlogpostSelectJPanel;
import hr.algebra.utilities.SynchronousAsynchronousWorker;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import model.repo.blogpost.Blogpost;
import model.repo.blogpost.Category;

/**
 *
 * @author Domi
 */
public class RSS_GUI extends javax.swing.JFrame {
    
    private static RSS_GUI current;
    
    public static final String dateFormat = "yyyy-mm-dd";
    
    public final SynchronousAsynchronousWorker SyncAsyncWorker = new SynchronousAsynchronousWorker();
    
    public static RSS_GUI getSingleton(){
        return current;
    }

    /**
     * Creates new form RSS_GUI
     */
    public RSS_GUI() {
        initComponents();
        
        current = this;
        
        jtpMain.addTab("Data Select", new BlogpostSelectJPanel());
        
        /*test code*/
        {
        /*IRepository testRepo = RepositoryFactory.getInstance();
        
        var dummyBlogpost = new Blogpost();
        
        dummyBlogpost.title = "TestTitle";
        dummyBlogpost.link = "TestLink";
        dummyBlogpost.datePublished = OffsetDateTime.parse("Fri, 20 Dec 2024 21:12:23 +0000", DateTimeFormatter.RFC_1123_DATE_TIME);
        
        dummyBlogpost.description = "TestDesc";
        dummyBlogpost.encodedContent = "TestContent";
        
        dummyBlogpost.imagePath = "TEST_PATH";
        
        dummyBlogpost.categories = new ArrayList<Category>();
        
        var cat1 = new Category();
        cat1.name = "CAT ONE";
        dummyBlogpost.categories.add(cat1);
        
        var cat2 = new Category();
        cat2.name = "MEOW MEOW";
        dummyBlogpost.categories.add(cat2);
        
        int id = 0;
        
        try {
            id = testRepo.createBlogpost(dummyBlogpost);
        } catch (Exception e) {
            var msg = e.getMessage();
        }
        
        cat2.name = "MEOW MEOW " + id;*/
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jtpMain = new javax.swing.JTabbedPane();
        jMenuBarMain = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(600, 400));

        jtpMain.setMinimumSize(new java.awt.Dimension(0, 0));

        jMenu1.setText("File");
        jMenuBarMain.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBarMain.add(jMenu2);

        setJMenuBar(jMenuBarMain);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jtpMain, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jtpMain, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(RSS_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RSS_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RSS_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RSS_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RSS_GUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBarMain;
    private javax.swing.JTabbedPane jtpMain;
    // End of variables declaration//GEN-END:variables
}
