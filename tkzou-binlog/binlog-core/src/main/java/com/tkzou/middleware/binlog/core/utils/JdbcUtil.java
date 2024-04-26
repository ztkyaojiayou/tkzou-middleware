package com.tkzou.middleware.binlog.core.utils;

import com.tkzou.middleware.binlog.core.common.meta.ColumnMetadata;
import com.tkzou.middleware.binlog.core.config.BinlogClientConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JDBC工具类
 *
 * @author zoutongkun
 */
public class JdbcUtil {

    /**
     * 数据源列表 (线程安全)
     * <p>
     * serverId 与 dataSource 配置
     */
    private static ConcurrentHashMap<Long, DataSource> dataSourceMap = new ConcurrentHashMap<>();

    /**
     * 获取列信息
     *
     * @param dataSourceProperties 数据源配置
     * @param database             数据库
     * @param table                数据表
     */
    public static List<ColumnMetadata> getColumns(BinlogClientConfig dataSourceProperties, String database, String table) {
        try (Connection connection = getConnection(dataSourceProperties);
             //拼接sql
             PreparedStatement statement = connection.prepareStatement("select COLUMN_NAME, DATA_TYPE, CHARACTER_SET_NAME from INFORMATION_SCHEMA.COLUMNS where TABLE_SCHEMA=? and TABLE_NAME=? order by ORDINAL_POSITION asc;")) {
            //设置参数
            statement.setString(1, database);
            statement.setString(2, table);
            //执行查询
            try (ResultSet resultSet = statement.executeQuery()) {
                //解析和封装结果
                return parseResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取数据库（链接）
     *
     * @param dataSourceProperties 数据源配置
     */
    public static Connection getConnection(BinlogClientConfig dataSourceProperties) throws SQLException {
        //先获取数据源
        DataSource dataSource = getDataSource(dataSourceProperties);
        //再获取连接
        return dataSource.getConnection();
    }

    /**
     * 获取数据源
     *
     * @param dataSourceProperties 数据源配置
     */
    public static DataSource getDataSource(BinlogClientConfig dataSourceProperties) {
        Long serverId = dataSourceProperties.getServerId();
        return dataSourceMap.computeIfAbsent(serverId, (key) -> createDataSource(dataSourceProperties));
    }

    /**
     * 创建数据源
     * 使用Hikari
     *
     * @return {@link HikariDataSource}
     */
    public static HikariDataSource createDataSource(BinlogClientConfig dataSourceProperties) {
        HikariConfig hikariConfig = dataSourceProperties.getHikariConfig();

        if (hikariConfig == null) {
            hikariConfig = new HikariConfig();
        }

        hikariConfig.setJdbcUrl("jdbc:mysql://" + dataSourceProperties.getHost() + ":" + dataSourceProperties.getPort() + "/INFORMATION_SCHEMA?useUnicode=true&characterEncoding=UTF-8&useSSL=false");
        hikariConfig.setUsername(dataSourceProperties.getUsername());
        hikariConfig.setPassword(dataSourceProperties.getPassword());
        return new HikariDataSource(hikariConfig);
    }

    /**
     * 解析 ResultSet 查询结果集
     *
     * @param resultSet 结果集
     */
    @SneakyThrows
    public static List<ColumnMetadata> parseResultSet(ResultSet resultSet) {
        List<ColumnMetadata> columns = new ArrayList<>();
        while (resultSet.next()) {
            ColumnMetadata column = new ColumnMetadata();
            column.setColumnName(resultSet.getString("COLUMN_NAME"));
            column.setDataType(resultSet.getString("DATA_TYPE"));
            column.setCharacterSetName(resultSet.getString("CHARACTER_SET_NAME"));
            columns.add(column);
        }
        return columns;
    }
}
