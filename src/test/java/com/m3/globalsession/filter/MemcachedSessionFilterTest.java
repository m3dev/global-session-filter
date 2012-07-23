package com.m3.globalsession.filter;

import com.m3.globalsession.memcached.adaptor.MemcachedClientAdaptor;
import org.junit.Test;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.testing.HttpTester;
import org.mortbay.jetty.testing.ServletTester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

public class MemcachedSessionFilterTest {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public static class RootServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setStatus(200);
            HttpSession session = req.getSession();
            if (session.getAttribute("value") == null) {
                session.setAttribute("value", "Created " + new Date());
            }
            try {
                Thread.sleep(100L);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
            Object value = session.getAttribute("value");
            if (value != null) {
                resp.getOutputStream().println(session.getCreationTime());
                resp.getOutputStream().println(value.toString());
            }
        }
    }

    @Test
    public void integrationWithSpymemcached() throws Exception {

        ServletTester tester = new ServletTester();
        FilterHolder filter = tester.addFilter(MemcachedSessionFilter.class, "/*", 0);
        filter.setInitParameter("memcachedServers", "memcached:11211");
        tester.addServlet(RootServlet.class, "/");
        tester.start();

        HttpTester request = new HttpTester();
        request.setMethod("GET");
        request.setHeader("Host", "tester"); // should be "tester"
        request.setURI("/");
        request.setVersion("HTTP/1.1");
        request.setContent("");

        String rawRequest = request.generate();
        log.debug(rawRequest);

        String responses = tester.getResponses(rawRequest);
        log.debug(responses);

        HttpTester response = new HttpTester();
        response.parse(responses);

        assertThat(response.getStatus(), is(equalTo(200)));
        List<String> setCookies = Collections.list(response.getHeaderValues("Set-Cookie"));
        assertThat(setCookies.get(0).matches("JSESSIONID=[^;]+;Path=/"), is(true));
        assertThat(setCookies.get(1).matches("__gsid__=[^;]+;Path=/"), is(true));

        // save the result
        String content1 = response.getContent();

        Thread.sleep(1500L);

        // restart jetty server
        tester.stop();
        tester.start();

        // create a request with cookies
        for (String setCookie : setCookies) {
            request.addHeader("Cookie", setCookie);
        }
        String rawRequest2 = request.generate();
        log.debug(rawRequest2);

        String responses2 = tester.getResponses(rawRequest2);
        response.parse(responses2);
        log.debug(responses2);

        List<String> setCookies2 = Collections.list(response.getHeaderValues("Set-Cookie"));
        assertThat(setCookies2.size(), is(equalTo(1)));
        assertThat(setCookies2.get(0).matches("JSESSIONID=[^;]+;Path=/"), is(true));
        assertThat(response.getContent(), is(equalTo(content1)));

    }

    @Test
    public void integrationWithXmemcached() throws Exception {

        ServletTester tester = new ServletTester();
        FilterHolder filter = tester.addFilter(MemcachedSessionFilter.class, "/*", 0);
        filter.setInitParameter("memcachedServers", "memcached:11211");
        filter.setInitParameter("memcachedClientAdaptorClassName", MemcachedClientAdaptor.Xmemcached);
        tester.addServlet(RootServlet.class, "/");
        tester.start();

        HttpTester request = new HttpTester();
        request.setMethod("GET");
        request.setHeader("Host", "tester"); // should be "tester"
        request.setURI("/");
        request.setVersion("HTTP/1.1");
        request.setContent("");

        String rawRequest = request.generate();
        log.debug(rawRequest);

        String responses = tester.getResponses(rawRequest);
        log.debug(responses);

        HttpTester response = new HttpTester();
        response.parse(responses);

        assertThat(response.getStatus(), is(equalTo(200)));
        List<String> setCookies = Collections.list(response.getHeaderValues("Set-Cookie"));
        assertThat(setCookies.get(0).matches("JSESSIONID=[^;]+;Path=/"), is(true));
        assertThat(setCookies.get(1).matches("__gsid__=[^;]+;Path=/"), is(true));

        // save the result
        String content1 = response.getContent();

        Thread.sleep(1500L);

        // restart jetty server
        tester.stop();
        tester.start();

        // create a request with cookies
        for (String setCookie : setCookies) {
            request.addHeader("Cookie", setCookie);
        }
        String rawRequest2 = request.generate();
        log.debug(rawRequest2);

        String responses2 = tester.getResponses(rawRequest2);
        response.parse(responses2);
        log.debug(responses2);

        List<String> setCookies2 = Collections.list(response.getHeaderValues("Set-Cookie"));
        assertThat(setCookies2.size(), is(equalTo(1)));
        assertThat(setCookies2.get(0).matches("JSESSIONID=[^;]+;Path=/"), is(true));
        assertThat(response.getContent(), is(equalTo(content1)));

    }

    @Test
    public void type() throws Exception {
        assertThat(MemcachedSessionFilter.class, notNullValue());
    }

    @Test
    public void instantiation() throws Exception {
        MemcachedSessionFilter instance = new MemcachedSessionFilter();
        assertThat(instance, notNullValue());
    }

    @Test(expected = IllegalStateException.class)
    public void getMemcachedServerAddresses_A$FilterConfig_NotSpecified() throws Exception {
        // given
        MemcachedSessionFilter target = new MemcachedSessionFilter();
        FilterConfig config = mock(FilterConfig.class);
        // when
        target.getMemcachedServerAddresses(config);
        // then
    }

    @Test
    public void getMemcachedServerAddresses_A$FilterConfig() throws Exception {
        // given
        MemcachedSessionFilter target = new MemcachedSessionFilter();
        FilterConfig config = mock(FilterConfig.class);
        given(config.getInitParameter(MemcachedSessionFilter.MEMCACHED_SERVERS_KEY)).willReturn("memcached:11211");
        // when
        List<InetSocketAddress> addresses = target.getMemcachedServerAddresses(config);
        // then
        assertThat(addresses.size(), is(equalTo(1)));
        assertThat(addresses.get(0).getHostName(), is(equalTo("memcached")));
        assertThat(addresses.get(0).getPort(), is(equalTo(11211)));
    }

    @Test(expected = IllegalStateException.class)
    public void init_A$FilterConfig_NotSpecified() throws Exception {
        // given
        MemcachedSessionFilter target = new MemcachedSessionFilter();
        FilterConfig config = mock(FilterConfig.class);
        // when
        target.init(config);
        // then
    }

    @Test
    public void init_A$FilterConfig() throws Exception {
        // given
        MemcachedSessionFilter target = new MemcachedSessionFilter();
        FilterConfig config = mock(FilterConfig.class);
        given(config.getInitParameter(MemcachedSessionFilter.MEMCACHED_SERVERS_KEY)).willReturn("memcached:11211");
        // when
        target.init(config);
        // then
    }

    @Test
    public void getMemcachedClientAdaptorClass_A$FilterConfig() throws Exception {
        // given
        MemcachedSessionFilter target = new MemcachedSessionFilter();
        FilterConfig config = mock(FilterConfig.class);
        // when
        Object actual = target.getMemcachedClientAdaptorClass(config);
        // then
        Object expected = "class com.m3.globalsession.memcached.adaptor.SpymemcachedAdaptor";
        assertThat(actual.toString(), is(equalTo(expected)));
    }

}
