package com.springbootwebsocketexample.service;

import com.springbootwebsocketexample.model.User;
import com.springbootwebsocketexample.model.UserResponse;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public UserResponse getUserResponse(User user){
        return new UserResponse("Hi " + user.getName());
    }
}
