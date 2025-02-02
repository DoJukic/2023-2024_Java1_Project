/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package hr.algebra.rss_gui.view;

import hr.algebra.rss_gui.RSS_GUI;
import hr.algebra.utilities.swing.MessageUtils;
import hr.algebra.utilities.swing.MiscUtils;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import model.repo.blogpost.Blogpost;
import model.repo.blogpost.Category;

/**
 *
 * @author Domi
 */
public class DataEditJPanel extends javax.swing.JPanel {
    RSS_GUI parentForm;
    
    int currentBlogpostID = -1;
    
    boolean isAdmin = false;
    boolean isNew = false;
    boolean sysIsBusy = false;
    
    public DataEditJPanel(RSS_GUI parentForm) {
        initComponents();
        
        this.parentForm = parentForm;
        
        parentForm.SyncAsyncWorker.subscribeToTaskStarted_ThreadWarn(() -> {
            SwingUtilities.invokeLater(() -> {
                setSystemBusyStatus(true);
            });
        });
        
        parentForm.SyncAsyncWorker.subscribeToAllTasksEnded_ThreadWarn(() -> {
            SwingUtilities.invokeLater(() -> {
                setSystemBusyStatus(false);
            });
        });
        
        MiscUtils.fixScrolling(jScrollPane1);
    }
    
    public void configIsAdmin(boolean isAdmin){
        this.isAdmin = isAdmin;
        refresh();
    }
    
    private void configIsNew(boolean isNew){
        this.isNew = isNew;
        refresh();
    }
    
    private void refresh(){
        pnlCategoriesView.setVisible(!isAdmin);
        PnlCategoriesEdit.setVisible(isAdmin);
        
        tfImagePath.setEditable(isAdmin);
        tfTitle.setEditable(isAdmin);
        tfLink.setEditable(isAdmin);
        tfDate.setEditable(isAdmin);
        
        epDescription.setEditable(isAdmin);
        epContent.setEditable(isAdmin);
        
        btnSave.setEnabled(isAdmin ? (!sysIsBusy ? !isNew : false) : false);
        btnDelete.setEnabled(isAdmin ? (!sysIsBusy ? !isNew : false) : false);
        btnCreateNew.setEnabled(isAdmin ? !sysIsBusy : false);
    }
    
    public void loadDisplay(Optional<Blogpost> optBlogpost, List<Category> categories){
        Blogpost blogpost;
        
        if (optBlogpost.isPresent()){
            blogpost = optBlogpost.get();
            configIsNew(false);
        }else{
            blogpost = new Blogpost();
            blogpost.id = -1;
            configIsNew(true);
        }
        
        currentBlogpostID = blogpost.id;
        
        lblImage.setText("IMAGE_ERROR");
        lblImage.setIcon(null);
        
        // https://java.tutorialink.com/resize-a-picture-to-fit-a-jlabel/
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(blogpost.imagePath));
            
            Image scaledImage = img.getScaledInstance(lblImage.getMinimumSize().width, lblImage.getMinimumSize().height, Image.SCALE_SMOOTH);

            lblImage.setIcon(new ImageIcon(scaledImage));
            lblImage.setText("");
        } catch (IOException ex) {
            Logger.getLogger(DataEditJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        tfImagePath.setText(blogpost.imagePath);
        tfTitle.setText(blogpost.title);
        tfLink.setText(blogpost.link);
        tfDate.setText(blogpost.datePublished.format(Blogpost.DATE_OFFSET_FORMATTER));
        
        epDescription.setText(blogpost.description);
        epContent.setText(blogpost.encodedContent);
        
        ArrayList<String> allCategories = new ArrayList();
        for (var cat : categories)
            allCategories.add(cat.name);
        MiscUtils.assignSimpleStringListModelToJList(lsCategoriesAll, new ArrayList(allCategories));
        
        ArrayList<String> currentCategories = new ArrayList();
        if (blogpost.categories.isPresent())
            for (var cat : blogpost.categories.get())
                currentCategories.add(cat.name);
        
        MiscUtils.assignSimpleStringListModelToJList(lsCategoriesEdit, new ArrayList(currentCategories));
        MiscUtils.assignSimpleStringListModelToJList(lsCategoriesView, new ArrayList(currentCategories));
    }
    
    public void setSystemBusyStatus(boolean isBusy){
        this.sysIsBusy = isBusy;
        refresh();
    }
    
    public int getCurrentDisplayedBlogpostID(){
        return currentBlogpostID;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        lblImage = new javax.swing.JLabel();
        tfImagePath = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tfTitle = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tfLink = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tfDate = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        epDescription = new javax.swing.JEditorPane();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        epContent = new javax.swing.JEditorPane();
        pnlCategories = new javax.swing.JPanel();
        PnlCategoriesEdit = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        btnCategoryCreate = new javax.swing.JButton();
        btnCategoryAdd = new javax.swing.JButton();
        btnCategoryRemove = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        lsCategoriesAll = new javax.swing.JList<>();
        jScrollPane6 = new javax.swing.JScrollPane();
        lsCategoriesEdit = new javax.swing.JList<>();
        pnlCategoriesView = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        lsCategoriesView = new javax.swing.JList<>();
        btnSave = new javax.swing.JButton();
        btnCreateNew = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(600, 400));
        setPreferredSize(new java.awt.Dimension(600, 1000));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setMinimumSize(new java.awt.Dimension(16, 100));

        jPanel2.setMinimumSize(new java.awt.Dimension(0, 1000));
        jPanel2.setPreferredSize(new java.awt.Dimension(598, 900));

        jPanel3.setMinimumSize(new java.awt.Dimension(10, 100));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel4.setMinimumSize(new java.awt.Dimension(10, 100));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        lblImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImage.setText("IMAGE_HERE");
        lblImage.setMaximumSize(new java.awt.Dimension(511, 201));
        lblImage.setMinimumSize(new java.awt.Dimension(510, 200));

        tfImagePath.setEditable(false);
        tfImagePath.setText("IMAGE_PATH_HERE");

        jLabel2.setText("Title");

        tfTitle.setEditable(false);
        tfTitle.setText("TITLE_HERE");

        jLabel3.setText("Link");

        tfLink.setEditable(false);
        tfLink.setText("LINK_HERE");

        jLabel4.setText("Date Published");

        tfDate.setEditable(false);
        tfDate.setText("DATE_HERE");

        jLabel5.setText("Description");

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        epDescription.setEditable(false);
        epDescription.setText("DESC_HERE");
        epDescription.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane2.setViewportView(epDescription);

        jLabel6.setText("Content");

        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane3.setMinimumSize(new java.awt.Dimension(16, 60));

        epContent.setEditable(false);
        epContent.setText("CONTENT_HERE");
        epContent.setMinimumSize(new java.awt.Dimension(113, 60));
        epContent.setPreferredSize(new java.awt.Dimension(113, 60));
        jScrollPane3.setViewportView(epContent);

        pnlCategories.setMinimumSize(new java.awt.Dimension(100, 180));

        jLabel8.setText("Categories");
        jLabel8.setMaximumSize(new java.awt.Dimension(200, 16));
        jLabel8.setMinimumSize(new java.awt.Dimension(200, 16));
        jLabel8.setPreferredSize(new java.awt.Dimension(200, 16));

        jLabel9.setText("All Categories");
        jLabel9.setMaximumSize(new java.awt.Dimension(200, 16));
        jLabel9.setMinimumSize(new java.awt.Dimension(200, 16));
        jLabel9.setPreferredSize(new java.awt.Dimension(200, 16));

        btnCategoryCreate.setText("Create");
        btnCategoryCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCategoryCreateActionPerformed(evt);
            }
        });

        btnCategoryAdd.setText("Add");
        btnCategoryAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCategoryAddActionPerformed(evt);
            }
        });

        btnCategoryRemove.setText("Remove");
        btnCategoryRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCategoryRemoveActionPerformed(evt);
            }
        });

        jScrollPane7.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        lsCategoriesAll.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        lsCategoriesAll.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lsCategoriesAll.setDragEnabled(true);
        lsCategoriesAll.setMaximumSize(new java.awt.Dimension(199, 90));
        lsCategoriesAll.setMinimumSize(new java.awt.Dimension(199, 90));
        jScrollPane7.setViewportView(lsCategoriesAll);

        lsCategoriesEdit.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        lsCategoriesEdit.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lsCategoriesEdit.setDragEnabled(true);
        jScrollPane6.setViewportView(lsCategoriesEdit);

        javax.swing.GroupLayout PnlCategoriesEditLayout = new javax.swing.GroupLayout(PnlCategoriesEdit);
        PnlCategoriesEdit.setLayout(PnlCategoriesEditLayout);
        PnlCategoriesEditLayout.setHorizontalGroup(
            PnlCategoriesEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PnlCategoriesEditLayout.createSequentialGroup()
                .addGroup(PnlCategoriesEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                    .addComponent(jScrollPane6))
                .addGroup(PnlCategoriesEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PnlCategoriesEditLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE))
                    .addGroup(PnlCategoriesEditLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane7)))
                .addGap(18, 18, 18)
                .addGroup(PnlCategoriesEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PnlCategoriesEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnCategoryAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnCategoryRemove, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnCategoryCreate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        PnlCategoriesEditLayout.setVerticalGroup(
            PnlCategoriesEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PnlCategoriesEditLayout.createSequentialGroup()
                .addGroup(PnlCategoriesEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(PnlCategoriesEditLayout.createSequentialGroup()
                        .addComponent(btnCategoryCreate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCategoryAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCategoryRemove))
                    .addGroup(PnlCategoriesEditLayout.createSequentialGroup()
                        .addGroup(PnlCategoriesEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PnlCategoriesEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel7.setText("Categories");

        jScrollPane4.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        lsCategoriesView.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "A", "BC", "DEF", "ABCDEF" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        lsCategoriesView.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane4.setViewportView(lsCategoriesView);

        javax.swing.GroupLayout pnlCategoriesViewLayout = new javax.swing.GroupLayout(pnlCategoriesView);
        pnlCategoriesView.setLayout(pnlCategoriesViewLayout);
        pnlCategoriesViewLayout.setHorizontalGroup(
            pnlCategoriesViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane4)
        );
        pnlCategoriesViewLayout.setVerticalGroup(
            pnlCategoriesViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCategoriesViewLayout.createSequentialGroup()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlCategoriesLayout = new javax.swing.GroupLayout(pnlCategories);
        pnlCategories.setLayout(pnlCategoriesLayout);
        pnlCategoriesLayout.setHorizontalGroup(
            pnlCategoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PnlCategoriesEdit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlCategoriesView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlCategoriesLayout.setVerticalGroup(
            pnlCategoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCategoriesLayout.createSequentialGroup()
                .addComponent(PnlCategoriesEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlCategoriesView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(lblImage, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tfImagePath, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tfTitle, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tfLink, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tfDate, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlCategories, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfImagePath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlCategories, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jScrollPane1.setViewportView(jPanel2);

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnCreateNew.setText("Create New");
        btnCreateNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateNewActionPerformed(evt);
            }
        });

        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCreateNew)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDelete)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 965, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave)
                    .addComponent(btnCreateNew)
                    .addComponent(btnDelete))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (currentBlogpostID < 0){
            MessageUtils.showErrorMessage("Blogpost Does Not Exist", "cannot save a non-existent blogpost. Create a new one instead.");
            return;
        }
        
        if (!parentForm.SyncAsyncWorker.getAllTasksFinished()){
            MessageUtils.showErrorMessage("Program Busy", "An operation is currently running, please try again once it is complete.");
            return;
        }
        
        Blogpost blogpost = new Blogpost();
        blogpost.id = currentBlogpostID;
        blogpost.imagePath = tfImagePath.getText();
        blogpost.title = tfTitle.getText();
        blogpost.link = tfLink.getText();
        try{
            blogpost.datePublished = OffsetDateTime.parse(tfDate.getText(), Blogpost.DATE_OFFSET_FORMATTER);
        }catch(Exception e){
            MessageUtils.showErrorMessage("Bad Date", "You have provided an invalid date. There is no saving this.");
            return;
        }
        blogpost.description = epDescription.getText();
        blogpost.encodedContent = epContent.getText();
        
        // https://stackoverflow.com/questions/1816673/how-do-i-check-if-a-file-exists-in-java
        File f = Paths.get(blogpost.imagePath).toFile();
        if(!f.canRead())
            if (!MessageUtils.showConfirmDialog("Are you sure?", "It doesn't look like the image exists, are you sure you wish to proceed?"))
                return;
        
        var cats = new ArrayList<Category>();
        for(var i = 0; i < lsCategoriesEdit.getModel().getSize(); i++){
            Category cat = new Category();
            cat.name = lsCategoriesEdit.getModel().getElementAt(i);
            cats.add(cat);
        }
        blogpost.categories = Optional.of(cats);
        
        parentForm.dataEditPanelSaveInitiated(blogpost);
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCategoryAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCategoryAddActionPerformed
        String value = lsCategoriesAll.getSelectedValue();
        
        if (value == null){
            MessageUtils.showErrorMessage("Error", "Nothing selected in the \"all categories\" list!");
            return;
        }
        
        List<String> categoryNames = MiscUtils.getStringListFromStringModel(lsCategoriesEdit.getModel());
        if (!categoryNames.contains(value))
            categoryNames.add(value);
        
        MiscUtils.assignSimpleStringListModelToJList(lsCategoriesEdit, new ArrayList(categoryNames));
    }//GEN-LAST:event_btnCategoryAddActionPerformed

    private void btnCategoryRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCategoryRemoveActionPerformed
        String value = lsCategoriesEdit.getSelectedValue();
        
        if (value == null){
            MessageUtils.showErrorMessage("Error", "Nothing selected in the \"categories\" list!");
            return;
        }
        
        List<String> categoryNames = MiscUtils.getStringListFromStringModel(lsCategoriesEdit.getModel());
        categoryNames.remove(value);
        
        MiscUtils.assignSimpleStringListModelToJList(lsCategoriesEdit, categoryNames);
    }//GEN-LAST:event_btnCategoryRemoveActionPerformed

    private void btnCategoryCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCategoryCreateActionPerformed
        String catName = MessageUtils.showInputDialog("Please input the category's name.");
        
        if (catName == null)
            return;
        
        if (catName.strip() == ""){
            MessageUtils.showErrorMessage("Input Error", "Category cannot be blank.");
        }
        
        List<String> categoryEditNames = MiscUtils.getStringListFromStringModel(lsCategoriesEdit.getModel());
        if (!categoryEditNames.contains(catName))
            categoryEditNames.add(catName);
        MiscUtils.assignSimpleStringListModelToJList(lsCategoriesEdit, categoryEditNames);
        
        List<String> categoryAllNames = MiscUtils.getStringListFromStringModel(lsCategoriesAll.getModel());
        if (!categoryAllNames.contains(catName))
            categoryAllNames.add(catName);
        MiscUtils.assignSimpleStringListModelToJList(lsCategoriesAll, categoryAllNames);
    }//GEN-LAST:event_btnCategoryCreateActionPerformed

    private void btnCreateNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateNewActionPerformed
        if (!parentForm.SyncAsyncWorker.getAllTasksFinished()){
            MessageUtils.showErrorMessage("Program Busy", "An operation is currently running, please try again once it is complete.");
        }
        
        Blogpost blogpost = new Blogpost();
        blogpost.id = -1;
        blogpost.imagePath = tfImagePath.getText();
        blogpost.title = tfTitle.getText();
        blogpost.link = tfLink.getText();
        try{
            blogpost.datePublished = OffsetDateTime.parse(tfDate.getText(), Blogpost.DATE_OFFSET_FORMATTER);
        }catch(Exception e){
            MessageUtils.showErrorMessage("Bad Date", "You have provided an invalid date. There is no saving this.");
            return;
        }
        blogpost.description = epDescription.getText();
        blogpost.encodedContent = epContent.getText();
        
        // https://stackoverflow.com/questions/1816673/how-do-i-check-if-a-file-exists-in-java
        File f = Paths.get(blogpost.imagePath).toFile();
        if(!f.canRead())
            if (!MessageUtils.showConfirmDialog("Are you sure?", "It doesn't look like the image exists, are you sure you wish to proceed?"))
                return;
        
        var cats = new ArrayList<Category>();
        for(var i = 0; i < lsCategoriesEdit.getModel().getSize(); i++){
            Category cat = new Category();
            cat.name = lsCategoriesEdit.getModel().getElementAt(i);
            cats.add(cat);
        }
        blogpost.categories = Optional.of(cats);
        
        parentForm.dataEditPanelCreateNewInitiated(blogpost);
    }//GEN-LAST:event_btnCreateNewActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        if (currentBlogpostID < 0){
            MessageUtils.showErrorMessage("Blogpost Does Not Exist", "cannot save a non-existent blogpost. Create a new one instead.");
            return;
        }
        
        if (!parentForm.SyncAsyncWorker.getAllTasksFinished()){
            MessageUtils.showErrorMessage("Program Busy", "An operation is currently running, please try again once it is complete.");
            return;
        }
        
        parentForm.dataEditPanelDeleteInitiated(currentBlogpostID);
    }//GEN-LAST:event_btnDeleteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PnlCategoriesEdit;
    private javax.swing.JButton btnCategoryAdd;
    private javax.swing.JButton btnCategoryCreate;
    private javax.swing.JButton btnCategoryRemove;
    private javax.swing.JButton btnCreateNew;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnSave;
    private javax.swing.JEditorPane epContent;
    private javax.swing.JEditorPane epDescription;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JLabel lblImage;
    private javax.swing.JList<String> lsCategoriesAll;
    private javax.swing.JList<String> lsCategoriesEdit;
    private javax.swing.JList<String> lsCategoriesView;
    private javax.swing.JPanel pnlCategories;
    private javax.swing.JPanel pnlCategoriesView;
    private javax.swing.JTextField tfDate;
    private javax.swing.JTextField tfImagePath;
    private javax.swing.JTextField tfLink;
    private javax.swing.JTextField tfTitle;
    // End of variables declaration//GEN-END:variables
}
