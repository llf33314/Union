package com.gt.union.common.config;

/**
 * Created by Administrator on 2017/10/12 0012.
 */
//@Configuration
public class RedisClusterConfig {
//    @Value("${spring.redis.cluster.nodes}")
//    private String nodes;
//
//    @Value("${spring.redis.cluster.timeout}")
//    private int timeout;
//
//    @Value("${spring.redis.cluster.max-attempts}")
//    private int maxAttempts;
//
//    @Value("${spring.redis.cluster.password}")
//    private String password;
//
//    @Value("${spring.redis.cluster.pool.max-total}")
//    private int maxTotal;
//
//    @Value("${spring.redis.cluster.pool.min-idle}")
//    private int minIdle;
//
//    @Value("${spring.redis.cluster.pool.max-idle}")
//    private int maxIdle;
//
//    @Bean
//    public GenericObjectPoolConfig clusterGenericObjectPoolConfig() {
//        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
//        config.setMaxTotal(maxTotal);
//        config.setMaxIdle(maxIdle);
//        config.setMinIdle(minIdle);
//        return config;
//    }
//
//    @Bean
//    public JedisCluster jedisCluster() {
//        String[] nodeArray = nodes.split(",");
//        Set<HostAndPort> hostAndPortSet = new HashSet<>();
//
//        for (String node : nodeArray) {
//            String[] hostAndPort = node.split(":");
//            hostAndPortSet.add(new HostAndPort(hostAndPort[0].trim(), Integer.valueOf(hostAndPort[1])));
//        }
//
//        return new JedisCluster(hostAndPortSet, timeout, timeout, maxAttempts, password, clusterGenericObjectPoolConfig());
//    }
}
