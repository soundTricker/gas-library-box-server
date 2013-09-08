package com.appspot.soundtricker.gaslibrarybox.service;

import org.slim3.tester.AppEngineTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class FusionServiceTest extends AppEngineTestCase {

    private FusionService service = new FusionService();

    @Test
    public void test() throws Exception {
        assertThat(service, is(notNullValue()));
    }
}
