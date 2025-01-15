/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal.sql;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import java.io.InputStream;
import java.util.Properties;
import javax.sql.DataSource;

/**
 *
 * @author Domi
 */

// More or less the same as in the example
public class DataSourceSingleton {
    private static final String PATH = "/config/db.properties";

    private static final String SERVER_NAME = "SERVER_NAME";
    private static final String DATABASE_NAME = "DATABASE_NAME";
    private static final String USER = "USER"; 
    private static final String PASSWORD = "PASSWORD";
    
    private static final Properties PROPERTIES =  new Properties();

    static {
        try (InputStream is = DataSourceSingleton.class.getResourceAsStream(PATH)) {      
            PROPERTIES.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println(PROPERTIES);
    }
    
    private DataSourceSingleton() {}

    private static DataSource instance;

    public static DataSource getInstance() {
        if (instance == null) {
            instance = createInstance();
        }
        return instance;
    }
    private static DataSource createInstance() {
        SQLServerDataSource dataSource = new SQLServerDataSource();
        dataSource.setServerName(PROPERTIES.getProperty(SERVER_NAME));
        dataSource.setDatabaseName(PROPERTIES.getProperty(DATABASE_NAME));
        dataSource.setUser(PROPERTIES.getProperty(USER));
        dataSource.setPassword(PROPERTIES.getProperty(PASSWORD));
        dataSource.setTrustServerCertificate(true);
        dataSource.setEncrypt(true);
        return dataSource;
    }  
}
