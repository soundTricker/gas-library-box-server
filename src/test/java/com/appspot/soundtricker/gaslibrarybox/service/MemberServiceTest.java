package com.appspot.soundtricker.gaslibrarybox.service;

import org.slim3.tester.AppEngineTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class MemberServiceTest extends AppEngineTestCase {

    private MemberService service = new MemberService();

    @Test
    public void test() throws Exception {
        assertThat(service, is(notNullValue()));
    }
}
