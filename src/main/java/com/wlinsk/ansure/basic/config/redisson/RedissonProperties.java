package com.wlinsk.ansure.basic.config.redisson;

import lombok.Data;
import org.redisson.config.ReadMode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: wlinsk
 * @Date: 2024/12/23
 */
@Component
@ConfigurationProperties(prefix = RedissonProperties.PREFIX)
@Data
public class RedissonProperties {
    static final String PREFIX = "spring.redisson";

    /**
     * 单机single 哨兵:sentinel 集群:cluster
     */
    private String mode;
    /**
     * 密码
     */
    private String password;
    /**
     * 单机配置
     */
    private Single single = new Single();
    /**
     * 集群配置
     */
    private Cluster cluster = new Cluster();
    /**
     * 哨兵配置
     */
    private Sentinel sentinel = new Sentinel();
    /**
     * 连接池信息
     */
    private final Pool pool = new Pool();
    /**
     * Redis从节点的最小空闲连接量
     */
    private  int slaveConnectionMinimumIdleSize = 16;
    /**
     * Redis master节点的最小空闲连接量
     */
    private  int masterConnectionMinimumIdleSize = 16;

    /**
     * 连接池配置
     */
    @Data
    public static class Pool {
        /**
         * 连接池大小 默认 8
         */
        private int poolSize = 8;

        /**
         * 最小连接数 默认 0
         */
        private int minIdle = 0;

        /**
         * 最大存活数 默认 8
         */
        private int maxActive = 8;
        /**
         * 连接空闲超时
         */
        private int idleConnectionTimeout = 10000;

        /**
         *命令等待超时，单位：毫秒
         */
        private int timeout = 3000;
        /**
         * 连接超时时间 默认10s 单位：毫秒
         */
        private int connectTimeout = 10000;
        /**
         * pingConnectionInterval心跳时间 默认1
         */
        private int pingConnectionInterval = 1000;
    }

    /**
     * 单机配置
     */
    @Data
    public static class Single {

        /**
         * 主机ip
         */
        private String host = "localhost";
        /**
         * 端口
         */
        private int port = 6379;

    }

    /**
     * 哨兵配置
     */
    @Data
    public static class Sentinel {

        /**
         * 哨兵master 名称
         */
        private String master;

        /**
         * 哨兵节点
         */
        private String nodes;

        /**
         * 主节点是否只写
         */
        private boolean masterOnlyWrite;
        /**
         * 执行失败最大次数 默认值3
         */
        private int failMax = 3;
    }

    /**
     * 集群配置
     */
    @Data
    public static class Cluster {
        /**
         * 集群状态扫描间隔时间，单位是毫秒
         */
        private int scanInterval;

        /**
         * 集群节点
         */
        private String nodes;

        /**
         * 默认值： SLAVE（只在从服务节点里读取）设置读取操作选择节点的模式。
         * 可用值为：
         * SLAVE - 只在从服务节点里读取。
         * MASTER - 只在主服务节点里读取。
         * MASTER_SLAVE - 在主从服务节点里都可以读取
         */
        private ReadMode readMode = ReadMode.SLAVE;
        /**
         * （从节点连接池大小） 默认值：64
         */
        private int slaveConnectionPoolSize = 64;
        /**
         * 主节点连接池大小）默认值：64
         */
        private int masterConnectionPoolSize = 64;
        /**
         * （命令失败重试次数） 默认值：3
         */
        private int retryAttempts = 3;

        /**
         *命令重试发送时间间隔，单位：毫秒 默认值：1500
         */
        private int retryInterval = 1500;

        /**
         * 执行失败最大次数默认值：3
         */
        private int failedAttempts = 3;
    }
}
