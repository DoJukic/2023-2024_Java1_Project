/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.util.List;
import java.util.Optional;
import model.repo.blogpost.Blogpost;
import model.repo.user.Login;
import model.repo.user.User;

/**
 *
 * @author Domi
 */
public interface IRepository {
    int createBlogpost(Blogpost blogpost) throws Exception;
    void createBlogposts(List<Blogpost> blogposts) throws Exception;
    void updateBlogpost(int id, Blogpost blogpost) throws Exception;
    void deleteBlogpost(int id) throws Exception;
    Optional<Blogpost> selectBlogpost(int id) throws Exception;
    List<Blogpost> selectBlogpostsNoCategory() throws Exception;
    
    int createUser(Login login, User user) throws Exception;
    Optional<User> attemptLogin(Login login) throws Exception;
}
