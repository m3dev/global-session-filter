# Sample with Memcached

## /etc/hosts

If a memcached server is working on localhost, modify /etc/hosts as follows:

```
127.0.0.1       memcached
```

## mvn jetty:run

`mvn jetty:run` and access the following URL:

http://localhost:8080/global-session-filter-memcached-demo/

