package com.springbootwebsocketexample.facade;

import com.springbootwebsocketexample.model.User;
import com.springbootwebsocketexample.model.UserResponse;
import com.springbootwebsocketexample.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserFacade {

    @Autowired
    private UserService userService;

    public UserResponse getUser(User user){
        UserResponse userResponse = userService.getUserResponse(user);
        return userResponse;
    }
}
