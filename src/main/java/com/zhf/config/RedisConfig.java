package com.zhf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.zhf.util.RedisUtil;

import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis配置类
 * 
 * @author Administrator
 *
 */
@Configuration
@PropertySource("classpath:config/redis.properties")
public class RedisConfig {

	@Value("${redis.maxIdle}")
	private Integer maxIdle;

	@Value("${redis.maxTotal}")
	private Integer maxTotal;

	@Value("${redis.maxWaitMillis}")
	private Integer maxWaitMillis;

	@Value("${redis.testOnBorrow}")
	private boolean testOnBorrow;

	@Value("${redis.hostName}")
	private String hostName;

	@Value("${redis.port}")
	private Integer port;

	@Value("${redis.password}")
	private String password;

	/**
	 * JedisPoolConfig 连接池
	 * 
	 * @return
	 */
	@Bean
	public JedisPoolConfig jedisPoolConfig() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		// 最大空闲数
		jedisPoolConfig.setMaxIdle(maxIdle);
		// 连接池的最大数据库连接数
		jedisPoolConfig.setMaxTotal(maxTotal);
		// 最大建立连接等待时间
		jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
		// 是否在从池中取出连接前进行检验,如果检验失败,则从池中去除连接并尝试取出另一个
		jedisPoolConfig.setTestOnBorrow(testOnBorrow);
		return jedisPoolConfig;
	}

	/**
	 * 单机版配置 @Title: JedisConnectionFactory @param @param
	 * jedisPoolConfig @param @return @return JedisConnectionFactory @throws
	 */
	@Bean
	public JedisConnectionFactory JedisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
		JedisConnectionFactory JedisConnectionFactory = new JedisConnectionFactory(jedisPoolConfig);
		// 连接池
		JedisConnectionFactory.setPoolConfig(jedisPoolConfig);
		// IP地址
		JedisConnectionFactory.setHostName(hostName);
		// 端口号
		JedisConnectionFactory.setPort(port);
		// 如果Redis设置有密码
		JedisConnectionFactory.setPassword(password);
		return JedisConnectionFactory;
	}

	/**
	 * 实例化 RedisTemplate 对象
	 *
	 * @return
	 */
	@Bean
	public RedisTemplate<String, Object> functionDomainRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		initDomainRedisTemplate(redisTemplate, redisConnectionFactory);
		return redisTemplate;
	}

	/**
	 * 设置数据存入 redis 的序列化方式,并开启事务
	 * 
	 * @param redisTemplate
	 * @param factory
	 */
	private void initDomainRedisTemplate(RedisTemplate<String, Object> redisTemplate, RedisConnectionFactory factory) {
		// 如果不配置Serializer，那么存储的时候缺省使用String，如果用User类型存储，那么会提示错误User can't cast
		// to String！
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
		redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
		// 开启事务
		redisTemplate.setEnableTransactionSupport(true);
		redisTemplate.setConnectionFactory(factory);
	}
	
	/**
     * 注入封装RedisTemplate
    * @Title: redisUtil 
    * @return RedisUtil
    * @date 2017年12月21日
    * @throws
     */
    @Bean(name = "redisUtil")
    public RedisUtil redisUtil(RedisTemplate<String, Object> redisTemplate) {
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.setRedisTemplate(redisTemplate);
        return redisUtil;
    }

}
