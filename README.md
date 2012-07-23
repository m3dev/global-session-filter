## Global Session Filter

This library provides global session variables across multiple Servlet containers.

The usage is very simple. After applying ServletFilter, just use `javax.servlet.http.HttpSession` as usual.

Session attributes and lifecycle will be managed by not a single Servlet container but an external datastore.


### Supported Datastores

It's also possible to add new datastore (e.g. Redis).

* Memcached (via Spymemcached / Xmemcached)


## How to use

### MemcachedSessionFilter with Spymemcached

```xml
<repositories>
    <repository>
        <id>couchbase.com</id>
        <url>http://files.couchbase.com/maven2/</url>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.m3</groupId>
        <artifactId>global-session-filter</artifactId>
        <version>[0.1,)</version>
    </dependency>
    <dependency>
        <groupId>spy</groupId>
        <artifactId>spymemcached</artifactId>
        <version>[2.8,)</version>
    </dependency>
<dependencies>
```

### MemcachedSessionFilter with Xmemcached

```xml
<dependencies>
    <dependency>
        <groupId>com.m3</groupId>
        <artifactId>global-session-filter</artifactId>
        <version>[0.1,)</version>
    </dependency>
    <dependency>
        <groupId>com.googlecode.xmemcached</groupId>
        <artifactId>xmemcached</artifactId>
        <version>[1.3,)</version>
    </dependency>
<dependencies>
```


### web.xml

```xml
<!-- Global Session Filter -->
<filter>

    <filter-name>GlobalSessionFilter</filter-name>
    <filter-class>com.m3.globalsession.filter.MemcachedSessionFilter</filter-class>
    <init-param>
        <param-name>memcachedServers</param-name>
        <param-value>server1:11211,server2:11211</param-value>
    </init-param>

    <!-- Optional -->
    <!--
    <init-param>
        <param-name>memcachedClientAdaptorClassName</param-name>
        <param-value>com.m3.globalsession.memcached.adaptor.SpymemcachedAdaptor</param-value>
    </init-param>
    <init-param>
        <param-name>memcachedClientAdaptorClassName</param-name>
        <param-value>com.m3.globalsession.memcached.adaptor.XmemcachedAdaptor</param-value>
    </init-param>
    -->

    <!-- Optional -->
    <!--
    <init-param>
        <param-name>namespace</param-name>
        <param-value>myapp</param-value>
    </init-param>
    <init-param>
        <param-name>sessionId</param-name>
        <param-value>__ssid__</param-value>
    </init-param>
    <init-param>
        <param-name>domain</param-name>
        <param-value></param-value>
    </init-param>
    <init-param>
        <param-name>path</param-name>
        <param-value>/</param-value>
    </init-param>
    <init-param>
        <param-name>secure</param-name>
        <param-value>false</param-value>
    </init-param>
    <init-param>
        <param-name>httpOnly</param-name>
        <param-value>true</param-value>
    </init-param>
    <init-param>
        <param-name>sessionTimeout</param-name>
        <param-value>30</param-value>
    </init-param>
    -->

</filter>

<filter-mapping>
    <filter-name>GlobalSessionFilter</filter-name>
    <url-pattern>/*</url-pattern>
    <dispatcher>REQUEST</dispatcher>
</filter-mapping>
```

## License

Apache License, Version 2.0

http://www.apache.org/licenses/LICENSE-2.0.html

