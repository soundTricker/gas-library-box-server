package com.appspot.soundtricker.gaslibrarybox.model;

import org.slim3.tester.AppEngineTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class GasLibraryTest extends AppEngineTestCase {

    private GasLibrary model = new GasLibrary();

    @Test
    public void test() throws Exception {
        assertThat(model, is(notNullValue()));
    }
}
