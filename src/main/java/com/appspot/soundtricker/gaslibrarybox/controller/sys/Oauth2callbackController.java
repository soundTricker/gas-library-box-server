package com.appspot.soundtricker.gaslibrarybox.controller.sys;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import com.appspot.soundtricker.gaslibrarybox.service.FusionService;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;

public class Oauth2callbackController extends Controller {
	
	private static final Logger logger = Logger.getLogger(Oauth2callbackController.class.getName());

    @Override
    public Navigation run() throws Exception {
    	
    	StringBuffer requestURL = request.getRequestURL();
    	
    	if(request.getQueryString() != null) {
    		requestURL.append("?").append(request.getQueryString());
    	}
    	
    	AuthorizationCodeResponseUrl responseUrl = new AuthorizationCodeResponseUrl(requestURL.toString());
    	
    	String code = responseUrl.getCode();
    	
    	if(responseUrl.getError() != null) {
    		logger.severe(responseUrl.getError());
    		return null;
    	} 
    	
    	if (code == null) {
    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    		return null;
    	}
    	
    	GoogleAuthorizationCodeFlow flow = FusionService.newFlow();
    	
    	String redirectUri = FusionService.getRedirectUri(request);
    	
    	GoogleTokenResponse res = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
    	
    	flow.createAndStoreCredential(res, FusionService.getUserId());
    	
    	logger.fine("success");
    	
    	
        return null;
    }
}
