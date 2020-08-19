package com.fuxl.springGen.service;


import com.fuxl.springGen.InitializingBean;
import com.fuxl.springGen.annotation.Autowired;
import com.fuxl.springGen.annotation.Component;
import com.fuxl.springGen.annotation.Scope;

@Component("genSpringService")
//@Scope("singleton")
public class GenSpringService implements InitializingBean {
    @Autowired
    public UserService userService;

    public void springDemo() {
        System.out.println("********springDemo*******"+ userService.getUserName());
    }

    public void afterPropertiesSet() throws Exception {
        System.out.println("******初始化******");
    }
}
