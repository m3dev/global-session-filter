package com.m3.globalsession;

import com.m3.globalsession.filter.GlobalSessionFilter;
import com.m3.globalsession.memcached.MemcachedClient;
import com.m3.globalsession.memcached.MemcachedClientFactory;
import com.m3.globalsession.store.MemcachedSessionStore;
import com.m3.globalsession.store.SessionStore;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

public class GlobalHttpSessionTest {

    InetSocketAddress address = new InetSocketAddress("memcached", 11211);
    MemcachedClient memcached;

    @Before
    public void setUp() throws Exception {
        if (memcached == null) {
            memcached = MemcachedClientFactory.create(Arrays.asList(address));
        }
    }

    protected GlobalHttpSession getInstance() throws Exception {
        String sessionId = "_____" + System.currentTimeMillis();
        SessionStore store = new MemcachedSessionStore(memcached);
        String namespace = GlobalSessionFilter.GLOBAL_NAMESPACE;
        Integer timeoutMinutes = 30;

        HttpSession session = mock(HttpSession.class);

        given(session.getCreationTime()).willReturn(123L);
        given(session.getLastAccessedTime()).willReturn(234L);
        given(session.getMaxInactiveInterval()).willReturn(345);

        ServletContext context = mock(ServletContext.class);
        given(session.getServletContext()).willReturn(context);

        HttpSessionContext sessionContext = mock(HttpSessionContext.class);
        given(session.getSessionContext()).willReturn(sessionContext);

        GlobalHttpSession globalHttpSession = new GlobalHttpSession(sessionId, store, namespace, timeoutMinutes, session);
        String attributesKey = globalHttpSession.getKeyGenerator().generate(GlobalHttpSession.ATTRIBUTES_KEY);
        store.set(attributesKey, 60, globalHttpSession.toMap());
        Thread.sleep(10L);
        return globalHttpSession;
    }

    @Test
    public void type() throws Exception {
        assertThat(GlobalHttpSession.class, notNullValue());
    }

    @Test
    public void instantiation() throws Exception {
        String sessionId = "_____" + System.currentTimeMillis();
        SessionStore store = new MemcachedSessionStore(memcached);
        Integer timeoutMinutes = 30;
        String namespace = GlobalSessionFilter.GLOBAL_NAMESPACE;
        HttpSession session = mock(HttpSession.class);
        GlobalHttpSession globalHttpSession = new GlobalHttpSession(sessionId, store, namespace, timeoutMinutes, session);
        assertThat(globalHttpSession, notNullValue());
    }

    @Test
    public void toMap_A$() throws Exception {
        // given
        GlobalHttpSession globalHttpSession = getInstance();
        // when
        ConcurrentHashMap<String, Object> actual = globalHttpSession.toMap();
        // then
        assertThat(actual.toString(), is(equalTo("{}")));
    }

    @Test
    public void isValid_A$() throws Exception {
        // given
        GlobalHttpSession globalHttpSession = getInstance();
        // when
        // then
        assertThat(globalHttpSession.isValid(), is(true));
        globalHttpSession.invalidate();
        assertThat(globalHttpSession.isValid(), is(false));
    }

    @Test
    public void getAttribute_A$String_NotExist() throws Exception {
        GlobalHttpSession globalHttpSession = getInstance();
        // given
        String name = "name";
        // when
        Object actual = globalHttpSession.getAttribute(name);
        // then
        Object expected = null;
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void getAttribute_A$String_Exist() throws Exception {
        // given
        GlobalHttpSession globalHttpSession = getInstance();
        String name = "name";
        globalHttpSession.setAttribute(name, "xxx");
        // when
        Object actual = globalHttpSession.getAttribute(name);
        // then
        Object expected = "xxx";
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void invalidate_A$() throws Exception {
        // given
        GlobalHttpSession globalHttpSession = getInstance();
        // when
        globalHttpSession.invalidate();
        // then
        assertThat(globalHttpSession.isValid(), is(equalTo(false)));
        assertThat(globalHttpSession.isNew(), is(equalTo(true)));
    }

    @Test
    public void removeAttribute_A$String() throws Exception {
        // given
        GlobalHttpSession globalHttpSession = getInstance();
        String name = "xxx";
        globalHttpSession.setAttribute(name, "yyy");
        assertThat(globalHttpSession.getAttribute(name), is(not(nullValue())));
        // when
        globalHttpSession.removeAttribute(name);
        // then
        assertThat(globalHttpSession.getAttribute(name), is(nullValue()));
    }

    @Test
    public void setAttribute_A$String$Object() throws Exception {
        // given
        GlobalHttpSession globalHttpSession = getInstance();
        String name = "xxx";
        Object value = "yyy";
        // when
        globalHttpSession.setAttribute(name, value);
        // then
        assertThat(globalHttpSession.getAttribute(name), is(equalTo(value)));
    }

    @Test
    public void getValue_A$String_NotSetYet() throws Exception {
        // given
        GlobalHttpSession globalHttpSession = getInstance();
        String name = "xxx";
        // when
        Object actual = globalHttpSession.getValue(name);
        // then
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void getValueNames_A$() throws Exception {
        // given
        GlobalHttpSession globalHttpSession = getInstance();
        // when
        String[] actual = globalHttpSession.getValueNames();
        // then
        assertThat(actual.length, is(equalTo(0)));
    }

    @Test
    public void getCreationTime_A$() throws Exception {
        // given
        long before = System.currentTimeMillis();
        GlobalHttpSession globalHttpSession = getInstance();
        // when
        long actual = globalHttpSession.getCreationTime();
        // then
        assertThat(actual, is(greaterThanOrEqualTo(before)));
    }

    @Test
    public void getId_A$() throws Exception {
        // given
        GlobalHttpSession globalHttpSession = getInstance();
        // when
        String actual = globalHttpSession.getId();
        // then
        assertThat(actual.matches("_____\\d+$"), is(true));
    }

    @Test
    public void getLastAccessedTime_A$() throws Exception {
        // given
        GlobalHttpSession globalHttpSession = getInstance();
        // when
        long actual = globalHttpSession.getLastAccessedTime();
        // then
        long expected = 0L;
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void getMaxInactiveInterval_A$() throws Exception {
        // given
        GlobalHttpSession globalHttpSession = getInstance();
        // when
        int actual = globalHttpSession.getMaxInactiveInterval();
        // then
        int expected = 1800;
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void getServletContext_A$() throws Exception {
        // given
        GlobalHttpSession globalHttpSession = getInstance();
        // when
        ServletContext actual = globalHttpSession.getServletContext();
        // then
        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void getSessionContext_A$() throws Exception {
        // given
        GlobalHttpSession globalHttpSession = getInstance();
        // when
        HttpSessionContext actual = globalHttpSession.getSessionContext();
        // then
        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void isNew_A$() throws Exception {
        // given
        GlobalHttpSession globalHttpSession = getInstance();
        // when
        boolean actual = globalHttpSession.isNew();
        // then
        boolean expected = true;
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void isNew_A$_AfterInvalidation() throws Exception {
        // given
        GlobalHttpSession globalHttpSession = getInstance();
        globalHttpSession.invalidate();
        // when
        boolean actual = globalHttpSession.isNew();
        // then
        boolean expected = true;
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void putValue_A$String$Object() throws Exception {
        // given
        GlobalHttpSession globalHttpSession = getInstance();
        String name = "xxx";
        Object value = "yyy";
        // when
        globalHttpSession.putValue(name, value);
        // then
    }

    @Test
    public void removeValue_A$String() throws Exception {
        // given
        GlobalHttpSession globalHttpSession = getInstance();
        String name = "xxx";
        // when
        globalHttpSession.removeValue(name);
        // then
    }

    @Test
    public void setMaxInactiveInterval_A$int() throws Exception {
        // given
        GlobalHttpSession globalHttpSession = getInstance();
        int interval = 0;
        // when
        globalHttpSession.setMaxInactiveInterval(interval);
        // then
    }

    @Test
    public void getKeyGenerator_A$() throws Exception {
        // given
        GlobalHttpSession globalHttpSession = getInstance();
        // when
        StoreKeyGenerator generator = globalHttpSession.getKeyGenerator();
        // then
        assertThat(generator, is(not(nullValue())));
        assertThat(generator.generate("foo").matches("GlobalSession::_____\\d+::GLOBAL::foo"), is(true));
    }

    @Test
    public void save_A$() throws Exception {
        // given
        GlobalHttpSession globalHttpSession = getInstance();
        // when
        globalHttpSession.save();
        // then
    }

}
