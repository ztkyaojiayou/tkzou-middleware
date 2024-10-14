package com.tkzou.middleware.mybatis.core.builder;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
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
import org.xml.sax.InputSource;

import javax.sql.DataSource;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
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
 * @author tkzou.middleware.mybatis.coreya
 * @description
 * @date 2024/4/22 18:10
 */
public class MapperAnnotationBuilder {
    /**
     * 支持的sql执行类型，也即crud操作
     */
    private List<Class<? extends Annotation>> sqlAnnotationTypeList = Lists.newArrayList(Insert.class, Delete.class, Update.class, Select.class);

    public Configuration parse() {
        Configuration configuration = new Configuration();
        // 解析mapper接口下各方法的元信息
        this.parseMapper(configuration, "com.tkzou.middleware.mybatis.core.mapper");
        //解析xml中的动态sql
        this.parseMapperXml(configuration);
        return configuration;
    }

    public Configuration parse(DataSource dataSource, Transaction transaction, String mapperPackageName) {
        Configuration configuration = this.parse();
        //解析mapper接口下各方法的元信息
        this.parseMapper(configuration, mapperPackageName);
        configuration.setDataSource(dataSource);
        configuration.setTransaction(transaction);
        return configuration;
    }

    /**
     * 解析mapper接口下各方法的元信息
     *
     * @param configuration
     * @param packageName
     */
    @SneakyThrows
    private void parseMapper(Configuration configuration, String packageName) {
        if (StrUtil.isBlank(packageName)) {
            return;
        }
        //扫描指定的mapper接口的包路径
        Set<Class<?>> classes = ClassUtil.scanPackage(packageName);
        //遍历处理每一个mapper接口
        for (Class<?> curClazz : classes) {
            //获取二级缓存注解，判断是否需要开启二级缓存-全局本地缓存，跨session
            CacheNamespace cacheNamespace = curClazz.getAnnotation(CacheNamespace.class);
            boolean isCache = cacheNamespace != null;
            //扫描每个mapper接口中的所有方法
            Method[] methods = curClazz.getMethods();
            for (Method curMethod : methods) {
                boolean isExistAnnotation = false;
                SqlCommandType sqlCommandType = null;
                // 原始sql
                String originalSql = "";
                //扫描当前方法中的curd注解
                for (Class<? extends Annotation> sqlAnnotationType : this.sqlAnnotationTypeList) {
                    //试图获取该注解
                    Annotation curAnnotation = curMethod.getAnnotation(sqlAnnotationType);
                    if (ObjectUtil.isNotEmpty(curAnnotation)) {
                        //获取该注解的value字段值，也即原始sql
                        originalSql = AnnotationUtil.getAnnotationValue(curClazz, sqlAnnotationType, "value");
                        if (curAnnotation instanceof Insert) {
                            sqlCommandType = SqlCommandType.INSERT;
                        } else if (curAnnotation instanceof Delete) {
                            sqlCommandType = SqlCommandType.DELETE;
                        } else if (curAnnotation instanceof Update) {
                            sqlCommandType = SqlCommandType.UPDATE;
                        } else if (curAnnotation instanceof Select) {
                            sqlCommandType = SqlCommandType.SELECT;
                        }
                        //只可能存在其中一个注解，这是约定！
                        isExistAnnotation = true;
                        break;
                    }
                }
                if (!isExistAnnotation) {
                    continue;
                }

                // 拿到mapper中方法的返回类型，主要是关注查询类接口
                Class<?> returnType = null;
                //是否是查list
                boolean isSelectMany = false;
                // 注意：这里获取方法的泛型中的返回类型，因为可能是list<T>，那么T才是实际的返回类型
                Type genericReturnType = curMethod.getGenericReturnType();
                //是否为list类型
                if (genericReturnType instanceof ParameterizedType) {
                    returnType = (Class<?>) ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
                    isSelectMany = true;
                    //一般类型
                } else if (genericReturnType instanceof Class) {
                    returnType = (Class<?>) genericReturnType;
                }

                // 封装成MappedStatement
                MappedStatement mappedStatement = MappedStatement.builder()
                    .id(curClazz.getName() + "." + curMethod.getName())
                    .sql(originalSql)
                    .returnType(returnType)
                    .sqlCommandType(sqlCommandType)
                    .isSelectMany(isSelectMany)
                    //开启二级缓存，也使用PerpetualCache，但它先于或高于一级缓存，可以跨session
                    //key取class名称，不重要，只是一个标识！
                    .cache(isCache ? new PerpetualCache(curClazz.getName()) : null)
                    .build();
                //添加到configuration中
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
        saxReader.setEntityResolver((publicId, systemId) -> new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes())));  // 跳过 xml DTD 验证 -- 解决解析慢的问题

        String xmlPath = System.getProperty("user.dir") + "/src/com/tkzou.middleware.mybatis.core/demo/mapper/UserMapper.xml";

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
