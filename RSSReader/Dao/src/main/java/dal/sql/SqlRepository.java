package dal.sql;

import dal.IRepository;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import model.repo.blogpost.Blogpost;
import model.repo.blogpost.Category;
import model.repo.user.Login;
import model.repo.user.UserInfo;

/**
 *
 * @author Domi
 */
public class SqlRepository implements IRepository {
    // Blogpost
    private static final String BLOGPOST_ID = "BlogpostID";
    private static final String ID_BLOGPOST = "IDBlogpost";
    private static final String TITLE = "Title";
    private static final String LINK = "Link";
    private static final String DATE_PUBLISHED_STRING = "DatePublishedStr";
    private static final String DESCRIPTION = "Description";
    private static final String ENCODED_CONTENT = "EncodedContent";
    private static final String IMAGE_PATH = "ImagePath";
            
    private static final String CATEGORY_NAME = "CategoryName";

    private static final String CREATE_BLOGPOST = "{ CALL createBlogpost (?,?,?,?,?,?,?) }";
    private static final String LINK_BLOGPOST_TO_CATEGORY = "{ CALL linkBlogpostToCategory (?,?) }";
    
    private static final String SELECT_BLOGPOST = "{ CALL selectBlogpost (?) }";
    private static final String SELECT_BLOGPOST_CATEGORIES = "{ CALL selectBlogpostCategories (?) }";
    private static final String SELECT_BLOGPOSTS = "{ CALL selectBlogposts }";
    private static final String SELECT_CATEGORIES = "{ CALL selectCategories }";
    
    private static final String DELETE_BLOGPOST = "{ CALL deleteBlogpost (?) }";
    private static final String DELETE_ALL_BLOGPOST_DATA = "{ CALL deleteAllBlogpostData }";
    
    private static final String UPDATE_BLOGPOST_FLUSH_CATEGORIES = "{ CALL updateBlogpostAndFlushCategories (?,?,?,?,?,?,?) }";
    
    // Login
    private static final String LOGIN_ALIAS = "Alias";
    private static final String LOGIN_PASSWORD_PLAIN = "PasswordPlain";
    private static final String LOGIN_EXISTS = "Exists";
    private static final String LOGIN_IS_ADMIN = "IsAdmin";
    private static final String LOGIN_SUCCESS = "Success";
    
    private static final String TRY_LOG_IN = "{ CALL tryLogIn (?,?,?,?) }";
    private static final String TRY_REGISTER = "{ CALL tryRegister (?,?,?) }";

    private int createBlogpostViaConnection(Connection con, Blogpost blogpost) throws Exception{
        int returnInt;
        
        try(CallableStatement stmt = con.prepareCall(CREATE_BLOGPOST)) {
            
            stmt.setString(TITLE, blogpost.title);
            stmt.setString(LINK, blogpost.link);
            stmt.setString(DATE_PUBLISHED_STRING, blogpost.datePublished
                    .format(Blogpost.DATE_OFFSET_FORMATTER));
            stmt.setString(DESCRIPTION, blogpost.description);
            stmt.setString(ENCODED_CONTENT, blogpost.encodedContent);

            stmt.setString(IMAGE_PATH, blogpost.imagePath);

            stmt.registerOutParameter(BLOGPOST_ID, Types.INTEGER);
            stmt.executeUpdate();

            returnInt = stmt.getInt(BLOGPOST_ID);
        }
        
        linkBlogpostToCategories(con, blogpost, returnInt);
        
        return returnInt;
    }
    
    private void linkBlogpostToCategories(Connection con, Blogpost blogpost, int blogpostID) throws Exception{
        if (blogpost.categories.isPresent() && !blogpost.categories.get().isEmpty()){
            try(CallableStatement stmt = con.prepareCall(LINK_BLOGPOST_TO_CATEGORY)) {
                for (var cat : blogpost.categories.get()){
                    stmt.setInt(BLOGPOST_ID, blogpostID);
                    stmt.setString(CATEGORY_NAME, cat.name);
                    stmt.executeUpdate();
                }
            }
        }
    }
    
    @Override
    public int createBlogpost(Blogpost blogpost) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try(Connection con = dataSource.getConnection()) {
            
            return createBlogpostViaConnection(con, blogpost);
        }
    }

    @Override
    public void createBlogposts(List<Blogpost> blogposts) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try(Connection con = dataSource.getConnection()) {
            
            for (var blogpost : blogposts){
                createBlogpostViaConnection(con, blogpost);
            }
        }
    }

    @Override
    public void updateBlogpost(Blogpost blogpost) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try(Connection con = dataSource.getConnection();) {
            try(CallableStatement stmt = con.prepareCall(UPDATE_BLOGPOST_FLUSH_CATEGORIES)) {
                
                stmt.setInt(BLOGPOST_ID, blogpost.id);
                stmt.setString(TITLE, blogpost.title);
                stmt.setString(LINK, blogpost.link);
                stmt.setString(DATE_PUBLISHED_STRING, blogpost.datePublished
                        .format(Blogpost.DATE_OFFSET_FORMATTER));
                stmt.setString(DESCRIPTION, blogpost.description);
                stmt.setString(ENCODED_CONTENT, blogpost.encodedContent);

                stmt.setString(IMAGE_PATH, blogpost.imagePath);
                
                stmt.executeUpdate();
            }
        
            linkBlogpostToCategories(con, blogpost, blogpost.id);
        }
    }

    @Override
    public void deleteBlogpost(int id) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try(Connection con = dataSource.getConnection();) {
            try(CallableStatement stmt = con.prepareCall(DELETE_BLOGPOST)) {
                stmt.setInt(BLOGPOST_ID, id);
                stmt.executeUpdate();
            }
        }
    }
    
    public void deleteAllBlogpostData() throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try(Connection con = dataSource.getConnection();) {
            try(CallableStatement stmt = con.prepareCall(DELETE_ALL_BLOGPOST_DATA)) {
                stmt.executeUpdate();
            }
        }
    }
    
    private Blogpost loadBlogpostFromResultSet(ResultSet rs) throws SQLException{
        var blogpost = new Blogpost();
        blogpost.id = rs.getInt(ID_BLOGPOST);
        blogpost.title = rs.getString(TITLE);
        blogpost.link = rs.getString(LINK);
        blogpost.datePublished = OffsetDateTime.parse(rs.getString(DATE_PUBLISHED_STRING).replaceFirst("[ ]", "T").replace(" ", ""), Blogpost.DATE_OFFSET_FORMATTER);
        blogpost.description = rs.getString(DESCRIPTION);
        blogpost.encodedContent = rs.getString(ENCODED_CONTENT);
        blogpost.imagePath = rs.getString(IMAGE_PATH);
        
        return blogpost;
    }

    @Override
    public Optional<Blogpost> selectBlogpost(int id) throws Exception {
        Optional<Blogpost> maybeBlogpost = Optional.empty();
        
        DataSource dataSource = DataSourceSingleton.getInstance();
        try(Connection con = dataSource.getConnection();) {
            try(CallableStatement stmt = con.prepareCall(SELECT_BLOGPOST)) {
                stmt.setInt(BLOGPOST_ID, id);
                try(ResultSet rs = stmt.executeQuery()){
                    if (rs.next()) {
                        var blogpost = loadBlogpostFromResultSet(rs);
                        maybeBlogpost = Optional.of(blogpost);
                    }
                }
            }
            
            if (maybeBlogpost.isPresent()){
                maybeBlogpost.get().categories = Optional.of(new ArrayList<>());
                
                try(CallableStatement stmt = con.prepareCall(SELECT_BLOGPOST_CATEGORIES)) {
                    stmt.setInt(BLOGPOST_ID, id);
                    try(ResultSet rs = stmt.executeQuery()){
                        while (rs.next()) {
                            Category cat = new Category();

                            cat.name = rs.getString(CATEGORY_NAME);
                            maybeBlogpost.get().categories.get().add(cat);
                        }
                    }
                }
            }
        }
        
        return maybeBlogpost;
    }

    @Override
    public List<Blogpost> selectBlogpostsNoCategory() throws Exception {
        List<Blogpost> blogposts = new ArrayList<>();
        
        DataSource dataSource = DataSourceSingleton.getInstance();
        try(Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_BLOGPOSTS);) {
            
            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    var blogpost = loadBlogpostFromResultSet(rs);
                    
                    blogposts.add(blogpost);
                }
            }
        }
        
        return blogposts;
    }

    @Override
    public List<Category> selectCategories() throws Exception {
        List<Category> categories = new ArrayList<>();
        
        DataSource dataSource = DataSourceSingleton.getInstance();
        try(Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_CATEGORIES);) {
            
            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Category cat = new Category();
                    cat.name = rs.getString(CATEGORY_NAME);
                    categories.add(cat);
                }
            }
        }
        
        return categories;
    }
    

    @Override
    public boolean tryRegister(Login login) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try(Connection con = dataSource.getConnection();) {
            try(CallableStatement stmt = con.prepareCall(TRY_REGISTER)) {
                stmt.setString(LOGIN_ALIAS, login.alias);
                stmt.setString(LOGIN_PASSWORD_PLAIN, login.password);
                
                stmt.registerOutParameter(LOGIN_SUCCESS, Types.BOOLEAN);
                stmt.executeUpdate();

                return stmt.getBoolean(LOGIN_SUCCESS);
            }
        }
    }

    @Override
    public Optional<UserInfo> tryLogin(Login login) throws Exception {
        UserInfo userInfo = new UserInfo();
        
        DataSource dataSource = DataSourceSingleton.getInstance();
        try(Connection con = dataSource.getConnection();) {
            try(CallableStatement stmt = con.prepareCall(TRY_LOG_IN)) {
                stmt.setString(LOGIN_ALIAS, login.alias);
                stmt.setString(LOGIN_PASSWORD_PLAIN, login.password);
                
                stmt.registerOutParameter(LOGIN_EXISTS, Types.BOOLEAN);
                stmt.registerOutParameter(LOGIN_IS_ADMIN, Types.BOOLEAN);
                stmt.executeUpdate();
                
                if (stmt.getBoolean(LOGIN_EXISTS)){
                    userInfo.admin = stmt.getBoolean(LOGIN_IS_ADMIN);
                    return Optional.of(userInfo);
                }
            }
        }
        
        return Optional.empty();
    }

}
