/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package hr.algebra.rss_gui.view;

import dal.IRepository;
import dal.RepositoryFactory;
import hr.algebra.factory.UrlConnectionFactory;
import hr.algebra.rss_gui.RSS_GUI;
import hr.algebra.rss_gui.view.model.BlogpostTableModel;
import hr.algebra.utilities.FileUtils;
import hr.algebra.utilities.MessageUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import model.repo.blogpost.Blogpost;
import model.repo.blogpost.Category;
import model.xml.archives.head;
import model.xml.archives.link;
import model.xml.blogposts.rss;
import org.xml.sax.InputSource;

/**
 *
 * @author Domi
 */
public class BlogpostSelectJPanel extends javax.swing.JPanel {
    public static final String imageExtension = "jpg";
    public static final String imageDirectory = "Images";

    /**
     * Creates new form DataSourceSelectJPanel
     */
    public BlogpostSelectJPanel() {
        initComponents();
        BlogpostTableModel model = new BlogpostTableModel(new ArrayList<Blogpost>());
        
        RSS_GUI mainForm = RSS_GUI.getSingleton();
        
        mainForm.SyncAsyncWorker.subscribeToTaskEnded_ThreadWarn(() -> {
            SwingUtilities.invokeLater(() ->{
                lblWorkerStatus.setText("");
            });
        });
        
        mainForm.SyncAsyncWorker.addTask(() -> {
            asyncLoadDataFromDB();
        });
        
        jTableBlogpostDisplay.setAutoCreateRowSorter(true);
        
        /*test code*/
        {
            /*IRepository testRepo = RepositoryFactory.getInstance();
            List<Blogpost> posts = null;
            try {
                posts = testRepo.selectBlogpostsNoCategory();
            } catch (Exception ex) {
                Logger.getLogger(BlogpostSelectJPanel.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (posts == null){
                int i = posts.size();
            }*/
        }
    }
    
    public final void asyncLoadDataFromDB(){
        SwingUtilities.invokeLater(() ->{
            lblWorkerStatus.setText("Loading data from database...");
        });

        IRepository repo = RepositoryFactory.getInstance();
        List<Blogpost> posts;
        try {
            posts = repo.selectBlogpostsNoCategory();

            SwingUtilities.invokeLater(() ->{
                BlogpostTableModel model = new BlogpostTableModel(posts);
                this.jTableBlogpostDisplay.setModel(model);
            });
        } catch (Exception ex) {
            Logger.getLogger(BlogpostSelectJPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public final void loadDataFromWeb(){
        RSS_GUI mainForm = RSS_GUI.getSingleton();
        
        mainForm.SyncAsyncWorker.cancelNonRunningTasks();
        
        if (!mainForm.SyncAsyncWorker.getAllTasksFinished()){
            MessageUtils.showInformationMessage("Notice", "The program is still processing, please hold.");
        }
        
        ArrayList<link> links = new ArrayList<>();
        
        mainForm.SyncAsyncWorker.addTask(() -> {
            SwingUtilities.invokeLater(() ->{
                lblWorkerStatus.setText("Clearing repository...");
            });
            
            IRepository repo = RepositoryFactory.getInstance();
            try {
                repo.deleteAllBlogpostData();
            } catch (Exception ex) {
                Logger.getLogger(BlogpostSelectJPanel.class.getName()).log(Level.SEVERE, null, ex);
                mainForm.SyncAsyncWorker.cancelNonRunningTasks();
            
                SwingUtilities.invokeLater(() ->{
                    MessageUtils.showErrorMessage("Alert", "Could not flush repository.");
                });
            }
        });
        
        mainForm.SyncAsyncWorker.addTask(() -> {
            SwingUtilities.invokeLater(() ->{
                this.asyncLoadDataFromDB(); // Imma be honest this is literally the wild west and the worker is optional, better safe than sorry
            });
        });
        
        mainForm.SyncAsyncWorker.addTask(() -> {
            this.asyncFetchAllPossibleFeeds(links);
        });
        
        mainForm.SyncAsyncWorker.addTask(() -> {
            this.asyncLoadProvidedFeeds(links);
        });
        
        mainForm.SyncAsyncWorker.addTask(() -> {
            SwingUtilities.invokeLater(() ->{
                this.asyncLoadDataFromDB();
            });
        });
    }
    
    private final void asyncFetchAllPossibleFeeds(List<link> links){
        SwingUtilities.invokeLater(() ->{
            lblWorkerStatus.setText("Fetching archives...");
        });
        
        try(InputStream iStream = UrlConnectionFactory.getHttpUrlConnection("https://fractalsoftworks.com/blog/").getInputStream();){
            // XML is malformed on arrival, so let's trim off some fat first
            BufferedReader bReader = new BufferedReader(new InputStreamReader(iStream));
            String allLines = "<head>";
            String currLine;
            
            while ((currLine = bReader.readLine()) != null){
                if (currLine.startsWith("\t<link rel='archives'")){
                    allLines += currLine;
                }
            }
            allLines += "</head>";
            
            // https://stackoverflow.com/questions/9909465/how-to-disable-dtd-fetching-using-jaxb2-0 (Graham Leggett's answer)
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            //Do unmarshall operation
            Source xmlSource = new SAXSource(spf.newSAXParser().getXMLReader(), new InputSource(new StringReader(allLines)));
            JAXBContext jc = JAXBContext.newInstance(head.class);
            Unmarshaller um = jc.createUnmarshaller();
            head head = (head)um.unmarshal(xmlSource);
            
            links.addAll(head.link);
        } catch(Exception ex){
            Logger.getLogger(BlogpostSelectJPanel.class.getName()).log(Level.SEVERE, null, ex);
            
            SwingUtilities.invokeLater(() ->{
                MessageUtils.showErrorMessage("Alert", "Could not fetch history.");
            });
        }
    }
    
    private final void asyncLoadProvidedFeeds(List<link> links){
        try{
            // https://stackoverflow.com/questions/9909465/how-to-disable-dtd-fetching-using-jaxb2-0 (Graham Leggett's answer)
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            
            IRepository repository = RepositoryFactory.getInstance();
            ArrayList<Blogpost> blogpostsArr = new ArrayList<>();

            var linksReversed = links.reversed();
            
            for (int i = 0; i < linksReversed.size(); i++){
                var link = linksReversed.get(i);
                
                int indexSend = i + 1;
                int sizeSend = linksReversed.size();
                SwingUtilities.invokeLater(() ->{
                    lblWorkerStatus.setText("Fetching " + link.title + " (" + indexSend + "/" + sizeSend + ")" + "...");
                });
                
                try(InputStream iStream = UrlConnectionFactory.getHttpUrlConnection(link.href + "feed").getInputStream();){
                    String allLines = new String(iStream.readAllBytes(), StandardCharsets.UTF_8);

                    //Do unmarshall operation
                    Source xmlSource = new SAXSource(spf.newSAXParser().getXMLReader(), new InputSource(new StringReader(allLines)));
                    JAXBContext jc = JAXBContext.newInstance(rss.class);
                    Unmarshaller um = jc.createUnmarshaller();
                    
                    rss rssData = (rss)um.unmarshal(xmlSource);
                    
                    for (var item : rssData.channel.item.reversed()){
                        Blogpost bp = new Blogpost();

                        bp.title = item.title;
                        bp.link = item.link;
                        bp.datePublished = OffsetDateTime.parse(item.pubDate, DateTimeFormatter.RFC_1123_DATE_TIME);
                        bp.description = item.description;
                        bp.encodedContent = item.contentEncoded;
                        
                        bp.categories = Optional.of(new ArrayList<>());
                        
                        for (var catName : item.category){
                            Category category = new Category();
                            category.name = catName;
                            bp.categories.get().add(category);
                        }
                        
                        Pattern pattern = Pattern.compile("(?<=src=\")(.*?)." + imageExtension + "(?=\")");

                        Matcher matcher = pattern.matcher(item.contentEncoded);
                        
                        String imageURL;
                        if (!matcher.find()){
                            imageURL = "https://fractalsoftworks.com/wp-content/uploads/2017/06/comsec_redacted.jpg";
                        }else{
                            imageURL = matcher.group(0);
                        }

                        UUID uuid = UUID.randomUUID();
                        
                        bp.imagePath = imageDirectory + File.separator + uuid.toString() + "." + imageExtension;
                        
                        FileUtils.copyFromUrl(imageURL, bp.imagePath);
                        
                        blogpostsArr.add(bp);
                    }
                }
            }
            
            repository.createBlogposts(blogpostsArr);
        } catch(Exception ex){
            Logger.getLogger(BlogpostSelectJPanel.class.getName()).log(Level.SEVERE, null, ex);
            
            SwingUtilities.invokeLater(() ->{
                MessageUtils.showErrorMessage("Alert", "Could not load provided feeds.");
            });
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jTableBlogpostDisplay = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jbtnConfirmSelection = new javax.swing.JButton();
        jbtnFetchNew = new javax.swing.JButton();
        lblWorkerStatus = new javax.swing.JLabel();

        jTableBlogpostDisplay.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTableBlogpostDisplay.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jTableBlogpostDisplay);

        jbtnConfirmSelection.setText("Confirm Selection");

        jbtnFetchNew.setText("Clear And Fetch From Web");
        jbtnFetchNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnFetchNewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblWorkerStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnFetchNew)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnConfirmSelection)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnConfirmSelection)
                    .addComponent(jbtnFetchNew)
                    .addComponent(lblWorkerStatus))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnFetchNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnFetchNewActionPerformed
        loadDataFromWeb();
    }//GEN-LAST:event_jbtnFetchNewActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableBlogpostDisplay;
    private javax.swing.JButton jbtnConfirmSelection;
    private javax.swing.JButton jbtnFetchNew;
    private javax.swing.JLabel lblWorkerStatus;
    // End of variables declaration//GEN-END:variables
}
