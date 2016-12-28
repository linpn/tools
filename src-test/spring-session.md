

    <!-- spring session filter -->
    <filter>
        <filter-name>springSessionRepositoryFilter</filter-name>
        <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>springSessionRepositoryFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

    <!-- spring security filter -->
    <filter>
        <filter-name>springSecurityFilterChain</filter-name>
        <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>springSecurityFilterChain</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>



    <!-- ================================= spring session ================================= -->

    <!-- 将Session放入Redis -->
    <bean class="org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration">
        <property name="maxInactiveIntervalInSeconds" value="1800"></property>
    </bean>

    <!-- Redis连接工厂 -->
    <bean id="connectionFactory" class="org.springframework.data.redis.connection.jedis.JedisConnectionFactory">
        <property name="hostName" value="${cache.redis.host}"/>
        <property name="port" value="${cache.redis.port}"/>
        <property name="poolConfig">
            <bean class="redis.clients.jedis.JedisPoolConfig">
                <property name="maxTotal" value="100"/>
                <property name="maxWaitMillis" value="5000"/>
                <property name="timeBetweenEvictionRunsMillis" value="300000"/>
                <property name="testWhileIdle" value="true"/>
            </bean>
        </property>
    </bean>