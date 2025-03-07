package com.membership;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.io.File;

@SpringBootApplication
@EnableJpaAuditing
public class MembershipApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(MembershipApplication.class);

    static {
        // 确保日志目录存在
        File logDir = new File("log");
        if (!logDir.exists()) {
            boolean created = logDir.mkdirs();
            if (created) {
                // 这里不能使用logger，因为静态代码块在logger初始化之前执行
                System.out.println("日志目录创建成功: " + logDir.getAbsolutePath());
            } else {
                System.err.println("无法创建日志目录: " + logDir.getAbsolutePath());
            }
        }
    }

    public static void main(String[] args) {
        logger.info("正在启动会员管理系统...");
        SpringApplication.run(MembershipApplication.class, args);
        logger.info("会员管理系统启动完成");
    }
}