/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.util.List;
import java.util.Optional;
import model.repo.blogpost.Blogpost;
import model.repo.user.Login;
import model.repo.user.UserInfo;

/**
 *
 * @author Domi
 */
public interface IRepository {
    int createBlogpost(Blogpost blogpost) throws Exception;
    void createBlogposts(List<Blogpost> blogposts) throws Exception;
    void updateBlogpost(Blogpost blogpost) throws Exception;
    void deleteBlogpost(int id) throws Exception;
    void deleteAllBlogpostData() throws Exception;
    Optional<Blogpost> selectBlogpost(int id) throws Exception;
    List<Blogpost> selectBlogpostsNoCategory() throws Exception;
    
    boolean tryRegister(Login login) throws Exception;
    Optional<UserInfo> tryLogin(Login login) throws Exception;
}
