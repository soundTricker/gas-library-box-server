package com.appspot.soundtricker.gaslibrarybox.controller;

import org.slim3.tester.ControllerTestCase;
import org.junit.Test;

import com.appspot.soundtricker.gaslibrarybox.controller.sys.Oauth2callbackController;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class Oauth2callbackControllerTest extends ControllerTestCase {

    @Test
    public void run() throws Exception {
        tester.start("/oauth2callback");
        Oauth2callbackController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.isRedirect(), is(false));
        assertThat(tester.getDestinationPath(), is(nullValue()));
    }
}
