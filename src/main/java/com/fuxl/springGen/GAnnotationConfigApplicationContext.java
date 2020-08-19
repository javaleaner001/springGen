package com.fuxl.springGen;

import com.fuxl.springGen.annotation.Autowired;
import com.fuxl.springGen.annotation.Component;
import com.fuxl.springGen.annotation.ComponentScan;
import com.fuxl.springGen.annotation.Scope;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GAnnotationConfigApplicationContext {

    private static ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();
    private static ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<String, Object>();

    public GAnnotationConfigApplicationContext(Class<GAppConfig> gAppConfigClass) {
        //扫描类
        List<Class<?>> classList = scan(gAppConfigClass);

        //存储到beanDefinitionMap
        for (Class<?> aClass : classList) {
            String beanName = aClass.getAnnotation(Component.class).value();
//                System.out.println(beanName);
            //将类信息放到bendefiniton中 将bendefiniton放到beandefinationMap中
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setBeanClass(aClass);
            if (aClass.isAnnotationPresent(Scope.class)) {
                String scope = aClass.getAnnotation(Scope.class).value();
                beanDefinition.setScope(scope);
            } else {
                beanDefinition.setScope("singleton");
            }
            beanDefinitionMap.put(beanName, beanDefinition);
        }

        //遍历beanDefinitionMap创建队形并初始化
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            String scope = beanDefinition.getScope();
            //将benandefintion反射为对象
            if (scope.equals("singleton")) {
                Object bean = createBean(beanDefinition);
                singletonObjects.put(beanName, bean);
            } else if (scope.equals("prototype")) {
                createBean(beanDefinition);
            }
        }
    }

    private List<Class<?>> scan(Class<GAppConfig> gAppConfigClass) {
        List classList = new ArrayList();
        //获取service路径
//        gAppConfigClass.isAnnotationPresent(ComponentScan.class)
        String value = gAppConfigClass.getAnnotation(ComponentScan.class).value();
        value = value.replace(".", "/");//com/fuxl/springGen/service
//        System.out.println(value);

        //扫描路径下component注解的类
        ClassLoader classLoader = GAnnotationConfigApplicationContext.class.getClassLoader();
        URL resource = classLoader.getResource(value);
//        System.out.println(resource.getPath());///D:/workspaceLean/springGen/target/classes/com/fuxl/springGen/service
        File file = new File(resource.getPath());
        for (File listFile : file.listFiles()) {
//            System.out.println(listFile);// D:\workspaceLean\springGen\target\classes\com\fuxl\springGen\service\GenSpringService.class
//            System.out.println(listFile.getPath().substring(listFile.getPath().indexOf("com"), listFile.getPath().indexOf(".class")));//com\fuxl\springGen\service\GenSpringService
            try {
                String className = listFile.getPath().substring(listFile.getPath().indexOf("com"), listFile.getPath().indexOf(".class"));
                Class<?> aClass = classLoader.loadClass(className.replace("\\", "."));
                classList.add(aClass);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return classList;
    }

    private Object createBean(BeanDefinition beanDefinition) {
        Object bean = null;
        try {
            bean = beanDefinition.getBeanClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        //填充属性autowire
        Field[] fields = beanDefinition.getBeanClass().getFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                try {
//                    System.out.println(field.getName());
                    field.setAccessible(true);
//                    System.out.println(getBean(field.getName()));
                    Object bean1 = getBean(field.getName());
                    field.set(bean, bean1);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        //aware
//        Class[] interfaces = beanDefinition.getBeanClass().getInterfaces();
        if (bean instanceof InitializingBean) {
            try {
                ((InitializingBean) bean).afterPropertiesSet();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bean;
    }

    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        String scope = beanDefinition.getScope();
//        System.out.println(beanName);
        if (scope.equals("prototype")) {
            //创建bean
            return createBean(beanDefinition);
        } else {
            Object bean = singletonObjects.get(beanName);
            if (bean == null) {
                bean = createBean(beanDefinition);
                singletonObjects.put(beanName, bean);
            }
            return bean;
        }
    }

}
