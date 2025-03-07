package com.membership.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 应用程序启动监听器，用于确保日志目录存在
 */
@Component
public class ApplicationStartupListener implements ApplicationListener<ApplicationStartedEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartupListener.class);

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        try {
            // 获取当前工作目录
            String currentDir = System.getProperty("user.dir");
            logger.info("当前工作目录: {}", currentDir);
            
            // 创建日志目录
            Path logPath = Paths.get(currentDir, "log");
            if (!Files.exists(logPath)) {
                Files.createDirectories(logPath);
                logger.info("日志目录已创建: {}", logPath.toAbsolutePath());
            } else {
                logger.info("日志目录已存在: {}", logPath.toAbsolutePath());
            }
            
            // 检查日志目录是否可写
            if (!Files.isWritable(logPath)) {
                logger.warn("警告: 日志目录不可写: {}", logPath.toAbsolutePath());
            } else {
                logger.info("日志目录可写");
            }
            
            // 创建一个测试文件以验证目录是否可写
            try {
                Path testFile = logPath.resolve("test.txt");
                Files.write(testFile, "测试日志目录是否可写".getBytes(StandardCharsets.UTF_8));
                Files.deleteIfExists(testFile);
                logger.info("日志目录可写性测试通过");
            } catch (IOException e) {
                logger.warn("日志目录可写性测试失败: {}", e.getMessage());
            }
            
            // 记录应用程序启动信息
            logger.info("应用程序已成功启动");
            logger.info("应用程序版本: 1.0.0");
            logger.info("Spring Boot版本: {}", event.getSpringApplication().getClass().getPackage().getImplementationVersion());
            
        } catch (Exception e) {
            logger.error("创建日志目录时发生错误: {}", e.getMessage(), e);
        }
    }
} 