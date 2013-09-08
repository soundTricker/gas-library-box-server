package com.appspot.soundtricker.gaslibrarybox.controller.backend.task;

import java.util.logging.Logger;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import com.appspot.soundtricker.gaslibrarybox.model.GasLibrary;
import com.appspot.soundtricker.gaslibrarybox.service.FusionService;
import com.appspot.soundtricker.gaslibrarybox.service.LibraryService;
import com.google.common.base.Strings;

public class Post2FusionTableController extends Controller {

	private static final Logger logger = Logger
			.getLogger(Post2FusionTableController.class.getName());

	@Override
	public Navigation run() throws Exception {

		String key = asString("key");

		if (Strings.isNullOrEmpty(key)) {
			logger.severe("key parameter is empty.");
			return null;
		}

		GasLibrary gasLibrary = LibraryService.get(key);

		String rowID = FusionService.getRowID(key);

		if (rowID != null) {
			FusionService.update(rowID, gasLibrary);
			logger.fine("Update " + key);
			return null;
		}

		FusionService.insert(key, gasLibrary);
		logger.fine("Insert " + key);

		return null;
	}
}
