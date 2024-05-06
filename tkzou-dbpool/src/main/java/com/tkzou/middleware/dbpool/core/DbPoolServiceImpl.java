package com.tkzou.middleware.dbpool.core;

import com.tkzou.middleware.dbpool.config.DbProperties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * 数据库连接池mysql实现
 * 都要加锁，保证线程安全
 *
 * @author zoutongkun
 */
public class DbPoolServiceImpl implements DbPoolService {

    /**
     * 存放空闲连接的容器，除了可以使用并发队列，也可以使用线程安全的集合Vector
     * 阻塞队列很重要，保证线程安全
     */
    private BlockingQueue<Connection> freeConnection = null;
    /**
     * 存放活动连接的容器，除了可以使用并发队列，也可以使用线程安全的集合Vector
     */
    private BlockingQueue<Connection> activeConnection = null;

    /**
     * 存放映射的属性配置文件
     */
    private DbProperties dDbProperties;

    /**
     * 创建一个实例
     *
     * @param dDbProperties
     * @return
     * @throws Exception
     */
    public static DbPoolServiceImpl create(DbProperties dDbProperties) throws Exception {
        return new DbPoolServiceImpl(dDbProperties);
    }

    /**
     * 构造器
     *
     * @param dDbProperties
     * @throws Exception
     */
    private DbPoolServiceImpl(DbProperties dDbProperties) throws Exception {
        // 获取配置文件信息，此时已经初始化
        this.dDbProperties = dDbProperties;
        freeConnection = new LinkedBlockingQueue<>(dDbProperties.getMaxFreeConnections());
        activeConnection = new LinkedBlockingQueue<>(dDbProperties.getMaxConnections());
        initFreeConnection();
    }

    /**
     * 初始化空闲线程池
     * 即先创建若干连接备用
     *
     * @Description:
     */
    private void initFreeConnection() throws Exception {
        System.out.println("初始化线程池开始，线程池配置属性：" + dDbProperties);
        if (dDbProperties == null) {
            throw new Exception("连接池配置属性对象不能为空");
        }
        //获取连接池配置文件中初始化连接数
        for (int i = 0; i < dDbProperties.getInitFreeConnections(); i++) {
            //创建Connection连接
            Connection newConnection = newConnection();
            if (newConnection != null) {
                //将创建的新连接放入到空闲池中
                freeConnection.add(newConnection);
            }
        }
        System.out.println("初始化线程池结束，初始化线程数：" + dDbProperties.getInitFreeConnections());
    }

    /**
     * 从数据库端获取/新建一个连接
     * 使用的就是jdbc-api来进行连接
     *
     * @return
     */
    private synchronized Connection newConnection() {
        try {
            Class.forName(dDbProperties.getDriverName());
            return DriverManager.getConnection(dDbProperties.getUrl(), dDbProperties.getUserName(), dDbProperties.getPassWord());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 判断连接是否可用，可用返回true
     * 直接和jdbc交互，与连接池无关
     *
     * @Description:
     */
    @Override
    public boolean isAvailable(Connection connection) {
        try {
            if (connection == null || connection.isClosed()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 使用重复利用机制获取连接：如果总连接未超过最大连接，
     * 则从空闲连接池获取连接或者创建一个新的连接，
     * 如果超过最大连接，则等待一段时间以后，继续获取连接
     * 核心方法，不负责释放连接，因为需要使用完后才释放，因此需要由使用者释放！
     *
     * @Description:
     */
    @Override
    public synchronized Connection getConnection() {
        Connection connection = null;
        //空闲连接和活动连接的总数加起来 小于 最大配置连接
        System.out.println("当前空闲连接总数：" + freeConnection.size() + " 当前活动连接总数" + activeConnection.size() + ", 配置最大连接数：" + dDbProperties.getMaxConnections());
        if (freeConnection.size() + activeConnection.size() < dDbProperties.getMaxConnections()) {
            //空闲连接池，是否还有连接，有就取出来，没有就创建一个新的。
            if (freeConnection.size() > 0) {
                //从连接池中取出一个直接使用即可！
                connection = freeConnection.poll();
                System.out.println("从空闲线程池取出线程：" + connection + "当前空闲线程总数：" + freeConnection.size());
            } else {
                connection = newConnection();
                System.out.println("空闲连接池没有连接，创建连接" + connection);
            }
            //拿到的连接可用，就添加活动连接池，否则就递归继续找下一个
            boolean available = isAvailable(connection);
            if (available) {
                activeConnection.add(connection);
            } else {
                //递归获取
                connection = getConnection();
            }

        } else {
            System.out.println("当前连接数已达到最大连接数，等待" + dDbProperties.getRetryConnectionTimeOut() + "ms以后再试");
            try {
                //等待一下
                wait(dDbProperties.getRetryConnectionTimeOut());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            connection = getConnection();
        }
        return connection;
    }

    /**
     * 使用可回收机制释放连接：如果连接可用，并且空闲连接池没有满，
     * 则把连接归还到空闲连接池，否则直接关闭连接
     * 同时也从活跃连接池中移除。
     *
     * @Description:
     */
    @Override
    public synchronized void release(Connection connection) {
        try {
            if (isAvailable(connection) && freeConnection.size() < dDbProperties.getMaxFreeConnections()) {
                freeConnection.add(connection);
                System.out.println("空闲线程池未满，归还连接" + connection);
            } else {
                connection.close();
                System.out.println("空闲线程池已满，关闭连接" + connection);
            }
            //同时也从活跃连接池中移除
            activeConnection.remove(connection);
            //此时因为释放了一个连接，
            //因此可以唤醒那些还在等待获取连接的线程！
            notifyAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}