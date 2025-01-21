/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package hr.algebra.rss_gui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatPropertiesLaf;
import dal.IRepository;
import dal.RepositoryFactory;
import hr.algebra.rss_gui.view.BlogpostSelectJPanel;
import hr.algebra.rss_gui.view.DataEditJPanel;
import hr.algebra.rss_gui.view.DataViewJPanel;
import hr.algebra.rss_gui.view.LoginJPanel;
import hr.algebra.rss_gui.view.RegisterJPanel;
import hr.algebra.utilities.swing.MessageUtils;
import hr.algebra.utilities.SynchronousAsynchronousWorker;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import model.repo.blogpost.Blogpost;
import model.repo.blogpost.Category;
import model.repo.user.Login;
import model.repo.user.UserInfo;

/**
 *
 * @author Domi
 */
public class RSS_GUI extends javax.swing.JFrame {
    
    public static final String DATE_FORMAT = "yyyy-mm-dd";
    
    public final SynchronousAsynchronousWorker SyncAsyncWorker = new SynchronousAsynchronousWorker();
    
    LoginJPanel loginPanel = new LoginJPanel(this);
    RegisterJPanel registerPanel = new RegisterJPanel(this);
    private boolean registerVisible = false;
    BlogpostSelectJPanel dataSelectPanel = new BlogpostSelectJPanel(this);
    private boolean dataSelectVisible = false;
    DataEditJPanel dataEditPanel = new DataEditJPanel(this);
    private boolean dataEditVisible = false;

    /**
     * Creates new form RSS_GUI
     */
    public RSS_GUI() {
        initComponents();
        
        rebuildTabs();
        
        var me_myself = this;
        
        // https://stackoverflow.com/questions/9778621/how-to-make-a-jmenu-item-do-something-when-its-clicked
        for (LookAndFeelInfo lafInfo : UIManager.getInstalledLookAndFeels()){
            JMenuItem menuItem = new JMenuItem(new AbstractAction(lafInfo.getName()) {
                public void actionPerformed(ActionEvent e) {
                    try {
                        UIManager.setLookAndFeel(lafInfo.getClassName());
                        SwingUtilities.updateComponentTreeUI(me_myself);
                        SwingUtilities.updateComponentTreeUI(registerPanel);
                        SwingUtilities.updateComponentTreeUI(dataSelectPanel);
                        SwingUtilities.updateComponentTreeUI(dataEditPanel);
                    } catch( Exception ex ) {
                        System.err.println( "Failed to initialize LaF" );
                    }
                }
            });
            menuThemes.add(menuItem);
        }
    }
    
    public void rebuildTabs(){
        // https://stackoverflow.com/questions/1013479/after-calling-jtabbedpane-removeall-the-jtabbedpane-still-has-x-number-of-tab
        while (jtpMain.getTabCount() > 0)
            jtpMain.remove(0);
        
        jtpMain.addTab("Log In", loginPanel);
        if (registerVisible){
            jtpMain.addTab("Register", registerPanel);
        }
        
        if (dataSelectVisible){
            jtpMain.addTab("Data Select", dataSelectPanel);
            
            if (dataEditVisible){
                jtpMain.addTab("Data View", dataEditPanel);
            }
        }
    }
    
    public void loginLogInAttempt(Login login){
        loginPanel.configLoginButtonEnabled(false);
        
        SyncAsyncWorker.addTask(() -> {
            IRepository repository = RepositoryFactory.getInstance();
            try {
                Optional<UserInfo> loginResult = repository.tryLogin(login);
                
                if (loginResult.isPresent()){
                    SwingUtilities.invokeLater(() ->{
                        dataSelectVisible = true;
                        dataEditVisible = false;
                        
                        setAdminStatus(loginResult.get().admin);
                        
                        rebuildTabs();
                        jtpMain.setSelectedComponent(dataSelectPanel);
                    });
                }else{
                    SwingUtilities.invokeLater(() ->{
                        MessageUtils.showErrorMessage("Login Failed", "Wrong alias or password.");
                    });
                }
            } catch (Exception ex) {
                Logger.getLogger(RSS_GUI.class.getName()).log(Level.SEVERE, null, ex);
                
                SwingUtilities.invokeLater(() ->{
                    MessageUtils.showErrorMessage("Login Failed", "Database fault :(");
                });
            }
            
            SwingUtilities.invokeLater(() ->{
                loginPanel.configLoginButtonEnabled(true);
            });
        });
    }
    
    public void showRegister(){
        registerVisible = true;
        rebuildTabs();
        jtpMain.setSelectedComponent(registerPanel);
    }
    
    public void hideRegister(){
        registerVisible = false;
        rebuildTabs();
    }
    
    public void registerRegisterAttempt(Login login){
        registerPanel.configRegisterButtonEnabled(false);
        
        SyncAsyncWorker.addTask(() -> {
            IRepository repository = RepositoryFactory.getInstance();
            try {
                boolean loginResult = repository.tryRegister(login);
                
                if (loginResult){
                    SwingUtilities.invokeLater(() ->{
                        hideRegister();
                        MessageUtils.showInformationMessage("Register Success", "Please log in with your credentials.");
                    });
                }else{
                    SwingUtilities.invokeLater(() ->{
                        MessageUtils.showErrorMessage("Register Failed", "Alias already exists or is invalid.");
                    });
                }
            } catch (Exception ex) {
                Logger.getLogger(RSS_GUI.class.getName()).log(Level.SEVERE, null, ex);
                
                SwingUtilities.invokeLater(() ->{
                    MessageUtils.showErrorMessage("Register Failed", "Database fault :(");
                });
            }
            
            SwingUtilities.invokeLater(() ->{
                registerPanel.configRegisterButtonEnabled(true);
            });
        });
    }
    
    private void setAdminStatus(Boolean isAdmin) {
        dataSelectPanel.configIsAdmin(isAdmin);
        dataEditPanel.configIsAdmin(isAdmin);
    }

    public void blogpostSelectBlogpostSelectedView(int blogpostID){
        SyncAsyncWorker.addTask(() -> {
            IRepository repository = RepositoryFactory.getInstance();
            
            try {
                List<Category> categories = repository.selectCategories();
                Optional<Blogpost> blogpostFull = repository.selectBlogpost(blogpostID);
                
                SwingUtilities.invokeLater(() ->{
                    if (blogpostFull.isEmpty())
                        MessageUtils.showErrorMessage("Display Failed", "Blogpost no longer exists?");
                    
                    dataEditVisible = true;
                    dataEditPanel.loadDisplay(blogpostFull.get(), categories);
                    rebuildTabs();
                    jtpMain.setSelectedComponent(dataEditPanel);
                });
            } catch (Exception ex) {
                Logger.getLogger(RSS_GUI.class.getName()).log(Level.SEVERE, null, ex);
                
                SwingUtilities.invokeLater(() ->{
                    MessageUtils.showErrorMessage("Display Failed", "Database fault :(");
                });
            }
        });
    }

    public void dataEditPanelSaveInitiated(Blogpost blogpost){
        dataEditPanel.setSaveBusyStatus(true);
         
        SyncAsyncWorker.addTask(() -> {
            IRepository repository = RepositoryFactory.getInstance();
            try {
                repository.updateBlogpost(blogpost);
            } catch (Exception ex) {
                Logger.getLogger(DataEditJPanel.class.getName()).log(Level.SEVERE, null, ex);
                
                SwingUtilities.invokeLater(() ->{
                    MessageUtils.showErrorMessage("Login Failed", "Database fault :(");
                });
            }
            
            SwingUtilities.invokeLater(() ->{
                dataEditPanel.setSaveBusyStatus(false);
                
                // refresh!
                if (dataEditPanel.getCurrentDisplayedBlogpostID() == blogpost.id){
                    blogpostSelectBlogpostSelectedView(blogpost.id);
                }
                dataSelectPanel.loadDataFromDB();
            });
        });
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
        menuThemes = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Starsector Blog RSS Parser");
        setMinimumSize(new java.awt.Dimension(640, 420));
        setPreferredSize(new java.awt.Dimension(640, 420));

        jtpMain.setMinimumSize(new java.awt.Dimension(0, 0));

        menuThemes.setText("Select Theme");
        jMenuBarMain.add(menuThemes);

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
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) [disabled]">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        /*
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
        */
        //</editor-fold>
        
        try {
            UIManager.setLookAndFeel( new FlatLightLaf() );
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }
        
        List<LookAndFeelInfo> lafInfoList = new ArrayList<LookAndFeelInfo>(Arrays.asList(UIManager.getInstalledLookAndFeels()));
        
        LookAndFeelInfo lafInfo = new LookAndFeelInfo(FlatLightLaf.NAME, FlatLightLaf.class.getName());
        lafInfoList.add(lafInfo);
        lafInfo = new LookAndFeelInfo(FlatDarkLaf.NAME, FlatDarkLaf.class.getName());
        lafInfoList.add(lafInfo);
        
        LookAndFeelInfo[] shtifuck = {};
        UIManager.setInstalledLookAndFeels(lafInfoList.toArray(shtifuck));
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RSS_GUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar jMenuBarMain;
    private javax.swing.JTabbedPane jtpMain;
    private javax.swing.JMenu menuThemes;
    // End of variables declaration//GEN-END:variables

}
