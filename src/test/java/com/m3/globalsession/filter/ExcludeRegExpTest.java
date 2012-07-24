package com.m3.globalsession.filter;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ExcludeRegExpTest {

    String regExp = "/.+\\.(html|jpg|jpeg|png|gif|js|css|swf)";

    @Test
    public void staticFiles() throws Exception {

        List<String> assets = Arrays.asList(
                "/img/logo.jpg",
                "/static/image/l-o-g-o.png",
                "/img/logo.jpeg",
                "/assets/img/logo.png",
                "/img/logo.gif",
                "/js/jquery.1.7.2.js",
                "/css/style.css"
        );
        for (String asset : assets) {
            assertThat(asset.matches(regExp), is(true));
        }
    }


    @Test
    public void notStaticFiles() throws Exception {

        List<String> assets = Arrays.asList(
                "/",
                "/foo",
                "/foo/",
                "/foo/bar",
                "/foo/bar/",
                "/foo/jsjsjs",
                "/foo/mycss"
        );
        for (String asset : assets) {
            assertThat(asset.matches(regExp), is(false));
        }
    }

}
