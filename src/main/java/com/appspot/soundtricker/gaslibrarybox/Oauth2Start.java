package com.appspot.soundtricker.gaslibrarybox;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.appspot.soundtricker.gaslibrarybox.service.FusionService;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.appengine.auth.oauth2.AbstractAppEngineAuthorizationCodeServlet;

public class Oauth2Start extends AbstractAppEngineAuthorizationCodeServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected AuthorizationCodeFlow initializeFlow() throws ServletException,
			IOException {
		return FusionService.newFlow();
	}

	@Override
	protected String getRedirectUri(HttpServletRequest req)
			throws ServletException, IOException {
		return FusionService.getRedirectUri(req);
	}

}
