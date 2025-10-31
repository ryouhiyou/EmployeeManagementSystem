package com.company.ems.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * MyBatis工具类，用于创建和管理SqlSessionFactory
 */
public class MyBatisUtil {

    private static SqlSessionFactory sqlSessionFactory;
    private static final String RESOURCE = "mybatis-config.xml";

    // 静态代码块：类加载时初始化 SqlSessionFactory
    static {
        try {
            InputStream inputStream = Resources.getResourceAsStream(RESOURCE);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            System.err.println("Error initializing SqlSessionFactory: Could not load " + RESOURCE);
            e.printStackTrace();
            throw new ExceptionInInitializerError("Error initializing SqlSessionFactory: " + e.getMessage());
        }
    }

    /**
     * 获取SqlSessionFactory实例
     * @return SqlSessionFactory
     */
    public static SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }
}
