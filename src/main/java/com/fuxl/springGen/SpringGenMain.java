package com.fuxl.springGen;

import com.fuxl.springGen.service.GenSpringService;
import com.fuxl.springGen.service.UserService;

public class SpringGenMain {
    public static void main(String[] args) {
        GAnnotationConfigApplicationContext gAnnotationConfigApplicationContext
                = new GAnnotationConfigApplicationContext(GAppConfig.class);
        UserService userService = (UserService) gAnnotationConfigApplicationContext.getBean("userService");
        UserService userService1 = (UserService) gAnnotationConfigApplicationContext.getBean("userService");
        System.out.println(userService);
        System.out.println(userService1);
        userService.getUserName();
        GenSpringService genSpringService = (GenSpringService) gAnnotationConfigApplicationContext.getBean("genSpringService");
        GenSpringService genSpringService1 = (GenSpringService) gAnnotationConfigApplicationContext.getBean("genSpringService");
        System.out.println(genSpringService);
        System.out.println(genSpringService1);
        genSpringService.springDemo();
    }
}
