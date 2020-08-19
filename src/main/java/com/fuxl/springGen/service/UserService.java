package com.fuxl.springGen.service;

import com.fuxl.springGen.annotation.Component;
import com.fuxl.springGen.annotation.Scope;

@Component("userService")
@Scope("prototype")
public class UserService {

    public String getUserName() {
        System.out.println("****UserService.getUserName*****");
        return "****UserService.getUserName*****";

    }
}
