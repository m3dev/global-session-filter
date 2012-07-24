package com.m3.globalsession.filter;

import com.m3.globalsession.GlobalHttpSession;
import com.m3.globalsession.GlobalSessionFilterSettings;
import com.m3.globalsession.GlobalSessionHttpRequest;
import com.m3.globalsession.store.SessionStore;
import com.m3.globalsession.util.CookieUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class GlobalSessionFilter implements Filter {

    private static Logger log = LoggerFactory.getLogger(GlobalSessionFilter.class);

    public static final String GLOBAL_NAMESPACE = "GLOBAL";
    public static final String DEFAULT_SESSION_ID_NAME = "__gsid__";

    public static class ConfigKey {
        public static final String NAMESPACE = "namespace";
        public static final String SESSION_ID = "sessionId";
        public static final String DOMAIN = "domain";
        public static final String PATH = "path";
        public static final String SECURE = "secure";
        public static final String HTTP_ONLY = "httpOnly";
        public static final String SESSION_TIMEOUT = "sessionTimeout";
        public static final String EXCLUDE_REG_EXP = "excludeRegExp";
    }

    public static class RequestAttributeKey {
        protected static final String SESSION_STATUS = "__sessionStatus__";
    }

    static enum SessionStatus {
        unknown, fixed
    }

    protected SessionStore store;
    protected GlobalSessionFilterSettings settings;

    protected GlobalSessionFilterSettings getGlobalSessionFilterSettings(FilterConfig config) {

        GlobalSessionFilterSettings settings = new GlobalSessionFilterSettings();

        settings.setNamespace(getConfigValue(config, ConfigKey.NAMESPACE));
        if (settings.getNamespace() == null) {
            settings.setNamespace(GLOBAL_NAMESPACE);
        }

        settings.setExcludeRegExp(getConfigValue(config, ConfigKey.EXCLUDE_REG_EXP));

        settings.setSessionIdKey(getConfigValue(config, ConfigKey.SESSION_ID));
        if (settings.getSessionIdKey() == null) {
            settings.setSessionIdKey(DEFAULT_SESSION_ID_NAME);
        }

        settings.setDomain(getConfigValue(config, ConfigKey.DOMAIN));

        settings.setPath(getConfigValue(config, ConfigKey.PATH));
        if (settings.getPath() == null) {
            settings.setPath("/");
        }

        settings.setSecure(getConfigValue(config, ConfigKey.SECURE) != null && getConfigValue(config, ConfigKey.SECURE).equals("true"));

        settings.setHttpOnly(getConfigValue(config, ConfigKey.HTTP_ONLY) != null && getConfigValue(config, ConfigKey.HTTP_ONLY).equals("true"));

        String sessionTimeout = getConfigValue(config, ConfigKey.SESSION_TIMEOUT);
        if (sessionTimeout == null) {
            settings.setSessionTimeoutMinutes(10);
        } else {
            settings.setSessionTimeoutMinutes(Integer.valueOf(sessionTimeout));
        }

        return settings;
    }

    protected Cookie getCurrentValidSessionIdCookie(HttpServletRequest req) {
        if (req.getCookies() != null) {
            for (Cookie cookie : req.getCookies()) {
                if (cookie.getName().equals(settings.getSessionIdKey())
                        && cookie.getValue() != null && cookie.getValue().trim().length() > 0) {
                    if (isValidSession(createGlobalSessionRequest(req, cookie.getValue()))) {
                        if (log.isDebugEnabled()) {
                            log.debug("SessionId cookie is found. (" + settings.getSessionIdKey() + " -> " + cookie.getValue() + ")");
                        }
                        return cookie;
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("SessionId cookie is found but it's invalid. (" + settings.getSessionIdKey() + " -> " + cookie.getValue() + ")");
                        }
                        continue;
                    }
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("SessionId cookie is not found.");
        }
        return null;
    }

    protected Cookie generateSessionIdCookie(String sessionIdValue) {

        Cookie sessionIdCookie = new Cookie(settings.getSessionIdKey(), sessionIdValue);
        if (settings.getDomain() != null) {
            sessionIdCookie.setDomain(settings.getDomain());
        }
        if (settings.getPath() != null) {
            sessionIdCookie.setPath(settings.getPath());
        } else {
            sessionIdCookie.setPath("/");
        }
        sessionIdCookie.setSecure(settings.isSecure());

        // [Note] httpOnly is not supported by Servlet API 2.x, so add it manually later.
        return sessionIdCookie;
    }

    protected GlobalSessionHttpRequest createGlobalSessionRequest(HttpServletRequest req, String sessionIdValue) {
        return new GlobalSessionHttpRequest(req, sessionIdValue, settings.getNamespace(),
                settings.getSessionTimeoutMinutes(), store);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        settings = getGlobalSessionFilterSettings(config);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest _req = (HttpServletRequest) req;
        HttpServletResponse _res = (HttpServletResponse) res;

        if (isGlobalSessionHttpRequest(_req)) {

            if (log.isDebugEnabled()) {
                log.debug("GlobalSessionHttpRequest is already applied.");
            }
            chain.doFilter(_req, _res);

        } else if (settings.getExcludeRegExp() != null
                && _req.getRequestURI().matches(settings.getExcludeRegExp())) {

            if (log.isDebugEnabled()) {
                log.debug("This URI is excluded. (URI: " + _req.getRequestURI() + ")");
            }
            chain.doFilter(_req, _res);

        } else {

            Cookie currentValidSessionIdCookie = getCurrentValidSessionIdCookie(_req);

            String sessionIdValue = null;
            if (currentValidSessionIdCookie == null) {
                // copy JSESSIONID value to original session
                sessionIdValue = _req.getSession().getId();
            } else {
                // current original session is valid
                sessionIdValue = currentValidSessionIdCookie.getValue();
            }

            if (currentValidSessionIdCookie == null) {
                Cookie newSessionIdCookie = generateSessionIdCookie(sessionIdValue);
                // [Note] httpOnly is not supported by Servlet API 2.x, so need to call #addHeader instead of #addCookie
                String setCookie = CookieUtil.createSetCookieHeaderValue(newSessionIdCookie, settings.isHttpOnly());
                _res.addHeader("Set-Cookie", setCookie);
                setSessionStatus(_req, SessionStatus.fixed);

                if (log.isDebugEnabled()) {
                    log.debug("SessionId cookie is updated. (" + sessionIdValue + ")");
                }
            }

            // doFilter with the request wrapper
            GlobalSessionHttpRequest _wrappedReq = createGlobalSessionRequest(_req, sessionIdValue);
            chain.doFilter(_wrappedReq, _res);

            // save attributes to the specified SessionStore
            _wrappedReq.getSession().save();
        }
    }

    @Override
    public void destroy() {
    }

    protected static String getConfigValue(FilterConfig config, String keyName) {
        String fromInitParam = config.getInitParameter(keyName);
        if (fromInitParam != null) {
            return fromInitParam;
        }
        return System.getProperty(keyName);
    }

    protected static void setSessionStatus(HttpServletRequest req, SessionStatus status) {
        req.setAttribute(RequestAttributeKey.SESSION_STATUS, status);
    }

    protected static SessionStatus getSessionStatus(HttpServletRequest req) {
        Object status = req.getAttribute(RequestAttributeKey.SESSION_STATUS);
        if (status == null) {
            return SessionStatus.unknown;
        } else {
            return (SessionStatus) status;
        }
    }

    protected static boolean isValidSession(GlobalSessionHttpRequest req) {
        if (getSessionStatus(req) == SessionStatus.fixed) {
            return true;
        }
        return req.getSession().isValid();
    }

    protected static boolean isGlobalSessionHttpRequest(HttpServletRequest req) {
        return req.getSession() instanceof GlobalHttpSession;
    }

}
