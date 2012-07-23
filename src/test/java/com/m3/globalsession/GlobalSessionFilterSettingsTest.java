package com.m3.globalsession;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class GlobalSessionFilterSettingsTest {

    @Test
    public void type() throws Exception {
        assertThat(GlobalSessionFilterSettings.class, notNullValue());
    }

    @Test
    public void instantiation() throws Exception {
        GlobalSessionFilterSettings instance = new GlobalSessionFilterSettings();
        assertThat(instance, notNullValue());
    }

    @Test
    public void setSecure_A$Boolean_null() throws Exception {
        GlobalSessionFilterSettings instance = new GlobalSessionFilterSettings();
        // given
        Boolean secure = null;
        // when
        instance.setSecure(secure);
        // then
    }

    @Test
    public void setSecure_A$Boolean_true() throws Exception {
        GlobalSessionFilterSettings instance = new GlobalSessionFilterSettings();
        // given
        Boolean secure = true;
        // when
        instance.setSecure(secure);
        // then
    }

}
