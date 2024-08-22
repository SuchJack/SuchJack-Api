package com.such.project;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.such.project.mapper")
@EnableDubbo
public class MyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
//        // 创建SpringApplication实例
//        SpringApplication application = new SpringApplication(MyApplication.class);
//        // 添加自定义的ApplicationContextInitializer
//        application.addInitializers(context -> {
//            // 获取Environment对象
//            Environment env = context.getEnvironment();
//            // 从Environment中读取"spring.application.name"属性值
//            String appName = env.getProperty("spring.application.name");
//            String filePath = System.getProperty("user.home") + File.separator + ".dubbo" +File.separator + appName;
//            // 修改dubbo的本地缓存路径，避免缓存冲突
//            System.setProperty("dubbo.meta.cache.filePath", filePath);
//            System.setProperty("dubbo.mapping.cache.filePath",filePath);
//        });
//        //启动应用
//        application.run(args);



    }

}
