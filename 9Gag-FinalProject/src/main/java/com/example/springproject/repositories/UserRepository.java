package com.example.springproject.repositories;

import com.example.springproject.controller.UserController;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByEmail(String email);

    User findUserByUsername(String username);
    User findUserByUsernameOrEmail(String username,String email);

     default User getUserByRequest(HttpServletRequest request){
        long id = (long) request.getSession().getAttribute(UserController.User_Id);
         return getById(id);
    }

   default long getIdByRequest(HttpServletRequest request){
         long id = (long) request.getSession().getAttribute(UserController.User_Id);
         return id;
    }
}
