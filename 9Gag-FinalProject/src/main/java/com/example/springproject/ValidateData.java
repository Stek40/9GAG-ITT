package com.example.springproject;

import com.example.springproject.controller.UserController;
import com.example.springproject.exceptions.UnauthorizedException;
import com.example.springproject.model.User;
import com.example.springproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public  class ValidateData {

    public  static void validatorLogin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute(UserController.User_Id)== null ||
                (!(Boolean) session.getAttribute(UserController.LOGGED)) ||
                (!request.getRemoteAddr().equals(session.getAttribute(UserController.LOGGED_FROM)))) {
            throw new UnauthorizedException("You have to login!");
        }
    }
}
