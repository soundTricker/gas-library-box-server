package com.appspot.soundtricker.gaslibrarybox.controller.backend.task;

import java.util.logging.Logger;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import com.appspot.soundtricker.gaslibrarybox.service.FusionService;
import com.google.common.base.Strings;

public class DeleteFromFusionTableController extends Controller {

	private static final Logger logger = Logger
			.getLogger(DeleteFromFusionTableController.class.getName());

	@Override
	public Navigation run() throws Exception {

		String key = asString("key");

		if (Strings.isNullOrEmpty(key)) {
			logger.severe("key parameter is empty.");
			return null;
		}

		String rowID = FusionService.getRowID(key);

		if (rowID == null) {
			logger.warning("Not found library:" + key);
			return null;
		}

		FusionService.delete(rowID);

		logger.fine("Delete " + key);
		return null;
	}
}
