package com.wlinsk.ansure.basic.config.redisson;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: wlinsk
 * @Date: 2024/12/23
 */
@EnableConfigurationProperties(RedissonProperties.class)
@Configuration
@ConditionalOnExpression("'${spring.redisson.mode}'=='single' or '${spring.redisson.mode}'=='cluster' or '${spring.redisson.mode}'=='sentinel'")
public class RedissonAutoConfiguration {
    private static final String PROTOCOL = "redis://";

    private final RedissonProperties redissonProperties;

    public RedissonAutoConfiguration(RedissonProperties redissonProperties) {
        this.redissonProperties = redissonProperties;
    }
    @Bean
    @Primary
    @ConditionalOnProperty(name = "spring.redisson.mode", havingValue = "single")
    public RedissonClient singleRedissonClient() {
        Config config = new Config();
        String host = redissonProperties.getSingle().getHost();
        int port = redissonProperties.getSingle().getPort();
        SingleServerConfig singleServerConfig = config.useSingleServer()
                .setAddress(PROTOCOL + host + ":" + port)
                .setTimeout(redissonProperties.getPool().getTimeout())
                .setConnectionMinimumIdleSize(redissonProperties.getPool().getMinIdle())
                .setConnectionPoolSize(redissonProperties.getPool().getPoolSize())
                .setPingConnectionInterval(redissonProperties.getPool().getPingConnectionInterval());
        if (StringUtils.isNotBlank(redissonProperties.getPassword())){
            singleServerConfig.setPassword(redissonProperties.getPassword());
        }
        return Redisson.create(config);
    }
    @Bean
    @ConditionalOnProperty(name = "spring.redisson.mode", havingValue = "cluster")
    public RedissonClient clusterRedissonClient() {
        Config config = new Config();
        String[] nodes = redissonProperties.getCluster().getNodes().split(",");
        List<String> newNodes = new ArrayList<>(nodes.length);
        Arrays.stream(nodes).forEach(address -> newNodes.add(
                address.startsWith(PROTOCOL) ? address : PROTOCOL + address));

        ClusterServersConfig serverConfig = config.useClusterServers()
                .addNodeAddress(newNodes.toArray(new String[0]))
                .setScanInterval(redissonProperties.getCluster().getScanInterval())
                .setIdleConnectionTimeout(redissonProperties.getPool().getIdleConnectionTimeout())
                .setConnectTimeout(redissonProperties.getPool().getConnectTimeout())
                .setRetryAttempts(redissonProperties.getCluster().getRetryAttempts())
                .setRetryInterval(redissonProperties.getCluster().getRetryInterval())
                .setMasterConnectionPoolSize(redissonProperties.getCluster()
                        .getMasterConnectionPoolSize())
                .setSlaveConnectionPoolSize(redissonProperties.getCluster()
                        .getSlaveConnectionPoolSize())
                .setTimeout(redissonProperties.getPool().getTimeout())
                .setPingConnectionInterval(redissonProperties.getPool().getPingConnectionInterval())
                .setMasterConnectionMinimumIdleSize(redissonProperties.getMasterConnectionMinimumIdleSize())
                .setSlaveConnectionMinimumIdleSize(redissonProperties.getSlaveConnectionMinimumIdleSize());
        if (StringUtils.isNotBlank(redissonProperties.getPassword())) {
            serverConfig.setPassword(redissonProperties.getPassword());
        }
        return Redisson.create(config);
    }

    @Bean
    @ConditionalOnProperty(name = "spring.redisson.mode", havingValue = "sentinel")
    RedissonClient redissonSentinel() {
        Config config = new Config();
        String[] nodes = redissonProperties.getSentinel().getNodes().split(",");
        List<String> newNodes = new ArrayList<>(nodes.length);
        Arrays.stream(nodes).forEach(address -> newNodes.add(
                address.startsWith(PROTOCOL) ? address : PROTOCOL + address));

        SentinelServersConfig serverConfig = config.useSentinelServers()
                .addSentinelAddress(newNodes.toArray(new String[0]))
                .setMasterName(redissonProperties.getSentinel().getMaster())
                .setReadMode(redissonProperties.getCluster().getReadMode())
                .setTimeout(redissonProperties.getPool().getTimeout())
                .setMasterConnectionPoolSize(redissonProperties.getPool().getPoolSize())
                .setSlaveConnectionPoolSize(redissonProperties.getPool().getPoolSize())
                .setPingConnectionInterval(redissonProperties.getPool().getPingConnectionInterval())
                .setMasterConnectionMinimumIdleSize(redissonProperties.getMasterConnectionMinimumIdleSize())
                .setSlaveConnectionMinimumIdleSize(redissonProperties.getSlaveConnectionMinimumIdleSize());

        if (StringUtils.isNotBlank(redissonProperties.getPassword())) {
            serverConfig.setPassword(redissonProperties.getPassword());
        }
        return Redisson.create(config);
    }
}
