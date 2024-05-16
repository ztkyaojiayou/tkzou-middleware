package com.tkzou.middleware.mybatis.core.builder;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.tkzou.middleware.mybatis.core.annotations.*;
import com.tkzou.middleware.mybatis.core.cache.PerpetualCache;
import com.tkzou.middleware.mybatis.core.mapping.MappedStatement;
import com.tkzou.middleware.mybatis.core.mapping.SqlCommandType;
import com.tkzou.middleware.mybatis.core.scripting.*;
import com.tkzou.middleware.mybatis.core.session.Configuration;
import com.tkzou.middleware.mybatis.core.transaction.Transaction;
import lombok.SneakyThrows;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.sql.DataSource;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

/**
 * <p> mapper注解解析配置构建器 </p>
 * 即解析mapper接口方法上的注解中的sql
 * 务必掌握！
 *
 * @author zhengqingya
 * @description
 * @date 2024/4/22 18:10
 */
public class MapperAnnotationBuilder {

    private List<Class<? extends Annotation>> sqlAnnotationTypeList = Lists.newArrayList(Insert.class, Delete.class, Update.class, Select.class);

    public Configuration parse() {
        Configuration configuration = new Configuration();
        // 解析mapper
        this.parseMapper(configuration, "com.zhengqing.demo.mapper");
        //解析xml中的动态sql
        this.parseMapperXml(configuration);
        return configuration;
    }

    public Configuration parse(DataSource dataSource, Transaction transaction, String mapperPackageName) {
        Configuration configuration = this.parse();
        this.parseMapper(configuration, mapperPackageName);
        configuration.setDataSource(dataSource);
        configuration.setTransaction(transaction);
        return configuration;
    }

    @SneakyThrows
    private void parseMapper(Configuration configuration, String packageName) {
        if (StrUtil.isBlank(packageName)) {
            return;
        }
        Set<Class<?>> classes = ClassUtil.scanPackage(packageName);
        for (Class<?> aClass : classes) {
            //获取二级缓存注解，判断是否需要开启二级缓存-全局本地缓存，跨session
            CacheNamespace cacheNamespace = aClass.getAnnotation(CacheNamespace.class);
            boolean isCache = cacheNamespace != null;
            Method[] methods = aClass.getMethods();
            for (Method method : methods) {
                boolean isExistAnnotation = false;
                SqlCommandType sqlCommandType = null;
                String originalSql = ""; // 原始sql
                for (Class<? extends Annotation> sqlAnnotationType : this.sqlAnnotationTypeList) {
                    Annotation annotation = method.getAnnotation(sqlAnnotationType);
                    if (annotation != null) {
                        originalSql = (String) annotation.getClass().getMethod("value").invoke(annotation);
                        if (annotation instanceof Insert) {
                            sqlCommandType = SqlCommandType.INSERT;
                        } else if (annotation instanceof Delete) {
                            sqlCommandType = SqlCommandType.DELETE;
                        } else if (annotation instanceof Update) {
                            sqlCommandType = SqlCommandType.UPDATE;
                        } else if (annotation instanceof Select) {
                            sqlCommandType = SqlCommandType.SELECT;
                        }
                        isExistAnnotation = true;
                        break;
                    }
                }
                if (!isExistAnnotation) {
                    continue;
                }

                // 拿到mapper的返回类型
                Class returnType = null;
                boolean isSelectMany = false;
                Type genericReturnType = method.getGenericReturnType();
                if (genericReturnType instanceof ParameterizedType) {
                    returnType = (Class) ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
                    isSelectMany = true;
                } else if (genericReturnType instanceof Class) {
                    returnType = (Class) genericReturnType;
                }

                // 封装
                MappedStatement mappedStatement = MappedStatement.builder()
                        .id(aClass.getName() + "." + method.getName())
                        .sql(originalSql)
                        .returnType(returnType)
                        .sqlCommandType(sqlCommandType)
                        .isSelectMany(isSelectMany)
                        //开启二级缓存，也使用PerpetualCache，但它先于或高于一级缓存，可以跨session
                        //key取class名称，不重要，只是一个标识！
                        .cache(isCache ? new PerpetualCache(aClass.getName()) : null)
                        .build();
                configuration.addMappedStatement(mappedStatement);
            }
        }
    }

    /**
     * 解析xml，主要是动态sql的解析
     *
     * @param configuration
     */
    @SneakyThrows
    public void parseMapperXml(Configuration configuration) {
        // 解析xml
        SAXReader saxReader = new SAXReader();
        saxReader.setEntityResolver(new EntityResolver() {
            @Override
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
            }
        });  // 跳过 xml DTD 验证 -- 解决解析慢的问题

        String xmlPath = System.getProperty("user.dir") + "/src/main/java/com/zhengqing/demo/mapper/UserMapper.xml";

        if (!FileUtil.exist(xmlPath)) {
            return;
        }

        BufferedInputStream inputStream = FileUtil.getInputStream(xmlPath);
        Document document = saxReader.read(inputStream);
        Element rootElement = document.getRootElement();
        String namespace = rootElement.attributeValue("namespace");
        List<Element> list = rootElement.selectNodes("//select"); // insert update delete select
        for (Element selectElement : list) {
            String methodName = selectElement.attributeValue("id");
            String resultType = selectElement.attributeValue("resultType");
            MixedSqlNode mixedSqlNode = this.parseTags(selectElement);

            Class<?> resultTypeClass = Class.forName(resultType);
            // 封装
            MappedStatement mappedStatement = MappedStatement.builder()
                    .id(namespace + "." + methodName)
                    .sql("")
                    .sqlSource(mixedSqlNode)
                    .returnType(resultTypeClass)
                    .sqlCommandType(SqlCommandType.SELECT)
                    .isSelectMany(false)
                    .cache(new PerpetualCache(resultTypeClass.getName()))
                    .build();
            configuration.addMappedStatement(mappedStatement);
        }
    }

    private MixedSqlNode parseTags(Element element) {
        List<SqlNode> contents = Lists.newArrayList();
        List<Node> contentList = element.content();
        for (Node node : contentList) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element childNodeElement = (Element) node;
                String sqlNodeType = childNodeElement.getName();
                String test = childNodeElement.attributeValue("test");
//                System.out.println("类型：" + sqlNodeType);
//                System.out.println("表达式：" + test);

                if (sqlNodeType.equals("if")) {
                    contents.add(new IfSqlNode(test, this.parseTags(childNodeElement)));
                } else if (sqlNodeType.equals("choose")) {
//                contents.add(new ChooseSqlNode(test, this.parseTags(childNodeElement)));
                }
            } else {
                String sql = node.getText();
                if (sql.contains("${")) {
                    contents.add(new TextSqlNode(sql));
                } else {
                    contents.add(new StaticTextSqlNode(sql));
                }
            }
        }
        return new MixedSqlNode(contents);
    }

}
