package com.membership.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志配置类，用于确保日志目录存在
 */
@Configuration
public class LogConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(LogConfig.class);
    
    @Bean
    public CommandLineRunner initLogDirectory() {
        return args -> {
            // 记录应用启动时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            logger.info("应用程序启动时间: {}", sdf.format(new Date()));
            
            // 检查并创建日志目录
            File logDir = new File("log");
            if (!logDir.exists()) {
                boolean created = logDir.mkdirs();
                if (created) {
                    logger.info("日志目录创建成功: {}", logDir.getAbsolutePath());
                } else {
                    logger.warn("无法创建日志目录: {}", logDir.getAbsolutePath());
                    // 尝试获取更多信息
                    File parentDir = logDir.getParentFile();
                    if (parentDir != null) {
                        logger.info("父目录: {}, 可写: {}", parentDir.getAbsolutePath(), parentDir.canWrite());
                    }
                    logger.error("创建日志目录失败，可能没有写入权限");
                }
            } else {
                logger.info("日志目录已存在: {}", logDir.getAbsolutePath());
                // 检查目录权限
                if (!logDir.canWrite()) {
                    logger.warn("日志目录不可写: {}", logDir.getAbsolutePath());
                } else {
                    logger.info("日志目录权限正常，可写: {}", logDir.getAbsolutePath());
                }
            }
            
            // 记录系统信息
            logger.info("操作系统: {}", System.getProperty("os.name"));
            logger.info("Java版本: {}", System.getProperty("java.version"));
            logger.info("用户目录: {}", System.getProperty("user.home"));
            logger.info("工作目录: {}", System.getProperty("user.dir"));
        };
    }
} 