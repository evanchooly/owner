/*
 * Copyright (c) 2012-2014, Luigi R. Viggiano
 * All rights reserved.
 *
 * This software is distributable under the BSD license.
 * See the terms of the BSD license in the documentation provided with this software.
 */

package org.aeonbits.owner;

import org.aeonbits.owner.ConfigFactory.ValidationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Luigi R. Viggiano
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigTest {

    @Mock
    private ScheduledExecutorService scheduler;

    public static interface SampleConfig extends Config {
        String hello(String param);

        @DefaultValue("Bohemian Rapsody - Queen")
        String favoriteSong();

        String unspecifiedProperty();

        @Key("server.http.port")
        @Description("The port to listen to")
        @Required
        int httpPort();

        @Key("salutation.text")
        @DefaultValue("Good Morning")
        String salutation();

        @DefaultValue("foo")
        void voidMethodWithValue();

        void voidMethodWithoutValue();
    }

    @Test
    public void shouldNotReturnNull() {
        assertNotNull(ConfigFactory.create(SampleConfig.class));
    }

    @Test
    public void shouldDoReplacements() {
        SampleConfig config = ConfigFactory.create(SampleConfig.class, new Properties());
        assertEquals("Hello Luigi.", config.hello("Luigi"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testVoidMethodWithValue() {
        SampleConfig cfg = ConfigFactory.create(SampleConfig.class);
        cfg.voidMethodWithValue();
    }

    @Test
    public void testVoidMethodWithoutValue() {
        SampleConfig cfg = ConfigFactory.create(SampleConfig.class);
        cfg.voidMethodWithoutValue();
    }

    @Test(expected = ValidationException.class)
    public void shouldFailValidation() {
        ConfigFactory.validate(SampleConfig.class);
    }

    static interface StringSubstitutionConfig extends Config {
        @DefaultValue("Hello Mr. %s!")
        String helloMr(String name);
    }

    @Test
    public void testDefaultStringValue() {
        StringSubstitutionConfig config = ConfigFactory.create(StringSubstitutionConfig.class);
        assertEquals("Hello Mr. Luigi!", config.helloMr("Luigi"));
    }

    @Test
    public void testDefaultPropertyOverridden() {
        SampleConfig config = ConfigFactory.create(SampleConfig.class);
        assertEquals("Speechless - Lady Gaga", config.favoriteSong());
    }

    @Test
    public void testUnspecifiedProperty() {
        SampleConfig config = ConfigFactory.create(SampleConfig.class);
        assertNull(config.unspecifiedProperty());
    }

    @Test
    public void testPropertyWithCustomizedKey() {
        SampleConfig config = ConfigFactory.create(SampleConfig.class);
        assertEquals(80, config.httpPort());
    }

    @Test
    public void testPropertyWithKeyAndDefaultValue() {
        SampleConfig config = ConfigFactory.create(SampleConfig.class);
        assertEquals("Good Afternoon", config.salutation());
    }

    public static interface SubstituteAndFormat extends Config {
        @DefaultValue("Hello ${mister}")
        String salutation(String name);

        @DefaultValue("Mr. %s")
        String mister(String name);
    }

    @Test
    public void testSubstitutionAndFormat() {
        SubstituteAndFormat cfg = ConfigFactory.create(SubstituteAndFormat.class);
        assertEquals("Hello Mr. Luigi", cfg.salutation("Luigi"));
        assertEquals("Mr. Luigi", cfg.mister("Luigi"));
    }

}
