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
 * <p> mapper xml解析器 </p>
 * 即解析xml配置文件中的sql
 * 就是统一扫mapper接口，将其元数据封装起来
 * 具体而言：
 * mapper中的每个方法都会被封装为MappedStatement
 * 而所有的mapper信息及其需要使用到的参数映射器等会进一步封装到Configuration中！！！
 * 因此Configuration这个类中应有尽有，也因此这个类非常重要！
 *
 * @author zoutongkun
 * @description
 * @date 2024/4/22 18:10
 */
public class XMLConfigBuilder {

    private List<Class<? extends Annotation>> sqlAnnotationTypeList = Lists.newArrayList(Insert.class, Delete.class,
            Update.class, Select.class);

    public Configuration parse() {
        Configuration configuration = new Configuration();
        // 解析mapper
        this.parseMapper(configuration, "com.zhengqing.demo.mapper");
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

    /**
     * 解析指定包如mapper包下的所有mapper接口
     * 这里假设该包下全是mapper接口，但实际上不一定，
     * 因此在源码中就出现了@Mapper注解以标识该接口为mapper接口！
     * 逻辑就是这么简单！
     *
     * @param configuration
     * @param packageName   需要扫描的包名的全限定名，比如xxx.mapper包
     */
    @SneakyThrows
    private void parseMapper(Configuration configuration, String packageName) {
        if (StrUtil.isBlank(packageName)) {
            return;
        }
        //直接扫包！比如扫描所有的mapper接口
        Set<Class<?>> classes = ClassUtil.scanPackage(packageName);
        //这里假设该包下的接口全是mapper接口
        // todo 后续可以使用@Mapper注解来进行标识！
        //核心逻辑：遍历，解析，封装，保存备用
        for (Class<?> aClass : classes) {
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
                        //使用反射反射获取注解上的值，这里就是获取原始sql，
                        //这种方式就无须强转了！
                        //
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
                //此时有两种情况，即list<User>集合类型和单纯的User类型，
                //但我们真正需要的是User类型，因此需要判断一下，
                //使用ParameterizedType即可区分是否为集合类型啦！
                if (genericReturnType instanceof ParameterizedType) {
                    returnType = (Class) ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
                    isSelectMany = true;
                } else if (genericReturnType instanceof Class) {
                    returnType = (Class) genericReturnType;
                }

                // 封装为MappedStatement（一个方法的元信息就对应一个MappedStatement）
                MappedStatement mappedStatement = MappedStatement.builder()
                        .id(aClass.getName() + "." + method.getName())
                        .sql(originalSql)
                        .returnType(returnType)
                        .sqlCommandType(sqlCommandType)
                        .isSelectMany(isSelectMany)
                        //二级缓存，这里就复用了一级缓存PerpetualCache，
                        //易知它在一级缓存的上一级！
                        // 了解一下即可，实际项目中上基本不用
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
