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
import hr.algebra.utilities.FileUtils;
import hr.algebra.utilities.swing.MessageUtils;
import hr.algebra.utilities.SynchronousAsynchronousWorker;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.StringReader;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FileChooserUI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import model.repo.blogpost.Blogpost;
import model.repo.blogpost.Category;
import model.repo.user.Login;
import model.repo.user.UserInfo;
import model.xml.blogposts.blogposts;
import model.xml.blogposts.item;
import model.xml.blogposts.rss;
import org.xml.sax.InputSource;

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

    public void setBlogpostDisplay(Optional<Integer> optBlogpostID){
        if (optBlogpostID.isPresent()){
            int blogpostID = optBlogpostID.get();
            
            SyncAsyncWorker.addTask(() -> {
                IRepository repository = RepositoryFactory.getInstance();

                try {
                    List<Category> categories = repository.selectCategories();
                    Optional<Blogpost> blogpostFull = repository.selectBlogpost(blogpostID);

                    SwingUtilities.invokeLater(() ->{
                        if (blogpostFull.isEmpty()){
                            MessageUtils.showErrorMessage("Display Failed", "Blogpost no longer exists?");
                            return;
                        }

                        dataEditVisible = true;
                        dataEditPanel.loadDisplay(blogpostFull, categories);
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
            return;
        }
            
        SyncAsyncWorker.addTask(() -> {
            IRepository repository = RepositoryFactory.getInstance();

            try {
                List<Category> categories = repository.selectCategories();

                SwingUtilities.invokeLater(() ->{
                    dataEditVisible = true;
                    dataEditPanel.loadDisplay(Optional.empty(), categories);
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
        SyncAsyncWorker.addTask(() -> {
            IRepository repository = RepositoryFactory.getInstance();
            try {
                repository.updateBlogpost(blogpost);
            } catch (Exception ex) {
                Logger.getLogger(DataEditJPanel.class.getName()).log(Level.SEVERE, null, ex);
                
                SwingUtilities.invokeLater(() ->{
                    MessageUtils.showErrorMessage("Blogpost Edit Failed", "Database fault :(");
                });
            }
            
            SwingUtilities.invokeLater(() ->{
                // refresh!
                if (dataEditPanel.getCurrentDisplayedBlogpostID() == blogpost.id){
                    setBlogpostDisplay(Optional.of(blogpost.id));
                }
                dataSelectPanel.loadDataFromDB();
            });
        });
    }

    public void dataEditPanelCreateNewInitiated(Blogpost blogpost){
        SyncAsyncWorker.addTask(() -> {
            IRepository repository = RepositoryFactory.getInstance();
            try {
                blogpost.id = repository.createBlogpost(blogpost);
            } catch (Exception ex) {
                Logger.getLogger(DataEditJPanel.class.getName()).log(Level.SEVERE, null, ex);
                
                SwingUtilities.invokeLater(() ->{
                    MessageUtils.showErrorMessage("Blogpost Creation Failed", "Database fault :(");
                });
            }
            
            SwingUtilities.invokeLater(() ->{
                // refresh!
                setBlogpostDisplay(Optional.of(blogpost.id));
                dataSelectPanel.loadDataFromDB();
            });
        });
    }

    public void dataEditPanelDeleteInitiated(int blogpostID){
        SyncAsyncWorker.addTask(() -> {
            IRepository repository = RepositoryFactory.getInstance();
            try {
                repository.deleteBlogpost(blogpostID);
            } catch (Exception ex) {
                Logger.getLogger(RSS_GUI.class.getName()).log(Level.SEVERE, null, ex);
                
                SwingUtilities.invokeLater(() ->{
                    MessageUtils.showErrorMessage("Blogpost Deletion Failed", "Database fault :(");
                });
            }
            
            SwingUtilities.invokeLater(() ->{
                if (dataEditPanel.getCurrentDisplayedBlogpostID() == blogpostID){
                    dataEditVisible = false;
                    rebuildTabs();
                    jtpMain.setSelectedComponent(dataSelectPanel);
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
        jMenu1 = new javax.swing.JMenu();
        miExport = new javax.swing.JMenuItem();
        miStopBackgroundTasks = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Starsector Blog RSS Parser");
        setMinimumSize(new java.awt.Dimension(640, 420));
        setPreferredSize(new java.awt.Dimension(640, 420));

        jtpMain.setMinimumSize(new java.awt.Dimension(0, 0));

        menuThemes.setText("Select Theme");
        jMenuBarMain.add(menuThemes);

        jMenu1.setText("Miscellaneous");

        miExport.setText("Export to File");
        miExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miExportActionPerformed(evt);
            }
        });
        jMenu1.add(miExport);

        miStopBackgroundTasks.setText("Stop Background Tasks");
        miStopBackgroundTasks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miStopBackgroundTasksActionPerformed(evt);
            }
        });
        jMenu1.add(miStopBackgroundTasks);

        jMenuBarMain.add(jMenu1);

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

    private void miExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miExportActionPerformed
        if (!SyncAsyncWorker.getAllTasksFinished()){
            MessageUtils.showErrorMessage("Program Busy", "The program is currently busy, please try again later.");
            return;
        }
        
        final JFileChooser fc = new JFileChooser();
        
        fc.addChoosableFileFilter(new FileNameExtensionFilter("XML (.xml)", "xml"));
        fc.setFileFilter(fc.getChoosableFileFilters()[0]);
        
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file;
            try {
                file = new File(fc.getSelectedFile().getCanonicalPath() + "." + ((FileNameExtensionFilter) fc.getFileFilter()).getExtensions()[0]);
            } catch (Exception ex) {
                Logger.getLogger(RSS_GUI.class.getName()).log(Level.SEVERE, null, ex);
                
                MessageUtils.showErrorMessage("Saving to File Failed", "Unknown error. Sorry.");
                return;
            }
            
            SyncAsyncWorker.addTask(() -> {
                IRepository repository = RepositoryFactory.getInstance();

                try {
                    // This is horrible. I am sorry. So much data...
                    List<Blogpost> blogposts = repository.selectBlogpostsNoCategory();
                    blogposts blogpostsToBeMarshalled = new blogposts();
                    blogpostsToBeMarshalled.item = new ArrayList<>();

                    for (var blogpost : blogposts){
                        Blogpost blogpostFull = repository.selectBlogpost(blogpost.id).get();
                        item itemToAdd = new item();

                        itemToAdd.title = blogpostFull.title;
                        itemToAdd.link = blogpostFull.link;
                        itemToAdd.pubDate = blogpostFull.datePublished.format(item.INBOUND_DATE_FORMATTER);
                        itemToAdd.link = blogpostFull.link;

                        itemToAdd.category = new ArrayList<String>();
                        for (var cat : blogpostFull.categories.get()){
                            itemToAdd.category.add(cat.name);
                        }

                        itemToAdd.description = blogpostFull.description;
                        itemToAdd.contentEncoded = blogpostFull.encodedContent;

                        blogpostsToBeMarshalled.item.add(itemToAdd);
                    }

                    JAXBContext jc = JAXBContext.newInstance(blogposts.class);
                    Marshaller marshaller = jc.createMarshaller();

                    marshaller.marshal(blogpostsToBeMarshalled, file);

                } catch (Exception ex) {
                    Logger.getLogger(RSS_GUI.class.getName()).log(Level.SEVERE, null, ex);

                    SwingUtilities.invokeLater(() -> {
                        MessageUtils.showErrorMessage("Saving to File Failed", "Unknown error. Sorry.");
                    });
                }
            });
        }
    }//GEN-LAST:event_miExportActionPerformed

    private void miStopBackgroundTasksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miStopBackgroundTasksActionPerformed
        SyncAsyncWorker.cancelAllTasks();
    }//GEN-LAST:event_miStopBackgroundTasksActionPerformed

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
        
        LookAndFeelInfo[] lafs = {};
        UIManager.setInstalledLookAndFeels(lafInfoList.toArray(lafs));
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RSS_GUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBarMain;
    private javax.swing.JTabbedPane jtpMain;
    private javax.swing.JMenu menuThemes;
    private javax.swing.JMenuItem miExport;
    private javax.swing.JMenuItem miStopBackgroundTasks;
    // End of variables declaration//GEN-END:variables

}
