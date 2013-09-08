package com.appspot.soundtricker.gaslibrarybox.controller._ah.task;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.slim3.datastore.Datastore;
import org.slim3.tester.ControllerTestCase;

import com.appspot.soundtricker.gaslibrarybox.controller.backend.task.Post2FusionTableController;
import com.appspot.soundtricker.gaslibrarybox.meta.GasLibraryMeta;
import com.appspot.soundtricker.gaslibrarybox.model.GasLibrary;

public class Post2FusionTableControllerTest extends ControllerTestCase {

    @Test
    public void run() throws Exception {
    	
    	GasLibrary gasLibrary = new GasLibrary();
    	gasLibrary.setKey(Datastore.createKey(GasLibraryMeta.get(), "test"));
		Datastore.put(gasLibrary);
    	
		tester.request.addParameter("key", "test");
        tester.start("/backend/task/post2FusionTable");
        
        Post2FusionTableController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.isRedirect(), is(false));
        assertThat(tester.getDestinationPath(), is(nullValue()));
    }
}
