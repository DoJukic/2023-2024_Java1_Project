/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Domi
 */
public class RepositoryFactory {
    private RepositoryFactory(){}
    
    private static IRepository instance;
    private static final String PATH = "/config/repository.properties";
    private static final String CLASS_NAME = "CLASS_NAME";
    private static final Properties PROPERTIES =  new Properties();
    
    static {
        try (InputStream is = RepositoryFactory.class.getResourceAsStream(PATH)) {   
            PROPERTIES.load(is);
            instance = (IRepository) 
                    Class.forName(PROPERTIES.getProperty(CLASS_NAME))
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (Exception ex) {
            Logger.getLogger(RepositoryFactory.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    public static IRepository getInstance() {
        return instance;
    }
}
