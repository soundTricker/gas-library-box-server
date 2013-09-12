package com.appspot.soundtricker.gaslibrarybox.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.appspot.soundtricker.gaslibrarybox.controller.backend.task.Post2FusionTableController;
import com.appspot.soundtricker.gaslibrarybox.model.GasLibrary;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.datastore.AppEngineDataStoreFactory;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.fusiontables.Fusiontables;
import com.google.api.services.fusiontables.FusiontablesScopes;
import com.google.api.services.fusiontables.model.Sqlresponse;
import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.google.common.base.Strings;


public class FusionService {
	
	public static final String TABLE_ID = "170ZjqYCe7DHSscFPLnqJcpQthwCIsSv3gM0ULTg";

	public static GoogleClientSecrets getClientCredential() throws IOException {
		if (FusionService.clientSecrets == null) {
			FusionService.clientSecrets = GoogleClientSecrets.load(
					FusionService.JSON_FACTORY,
					new InputStreamReader(Post2FusionTableController.class
							.getResourceAsStream("/client_secrets.json")));
		}
		return FusionService.clientSecrets;
	}

	public static final AppEngineDataStoreFactory DATA_STORE_FACTORY = AppEngineDataStoreFactory
	.getDefaultInstance();
	/** Global instance of the HTTP transport. */
	public static final HttpTransport HTTP_TRANSPORT = new UrlFetchTransport();
	/** Global instance of the JSON factory. */
	public static final JsonFactory JSON_FACTORY = JacksonFactory
			.getDefaultInstance();
	public static GoogleClientSecrets clientSecrets = null;
	
	public static String getRedirectUri(HttpServletRequest req) {
		GenericUrl url = new GenericUrl(req.getRequestURL().toString());
		url.setRawPath("/sys/oauth2callback");
		return url.build();
	}
	public static GoogleAuthorizationCodeFlow newFlow() throws IOException {
		return new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT,
				JSON_FACTORY, getClientCredential(),
				Collections.singleton(FusiontablesScopes.FUSIONTABLES))
				.setDataStoreFactory(DATA_STORE_FACTORY)
				.setAccessType("offline").setApprovalPrompt("force").build();
	}
	
	public static String getUserId() {
		return "owner";
	}
	
	public static Fusiontables loadClient() throws IOException {
		Credential credential = newFlow().loadCredential(getUserId());
		
		return new Fusiontables.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				credential).setApplicationName("gas-library-box").build();
	}
	
	public static String getRowID(String key) throws IOException {
		Fusiontables fusiontables = loadClient();
		Sqlresponse res = fusiontables.query().sqlGet("select ROWID from " + TABLE_ID + " where Key='" + key + "'").execute();
		
		if(res.getRows() == null || res.getRows().size() == 0) {
			return null;
		}
		return res.getRows().get(0).get(0).toString();

	}
	
	public static void insert(String key, GasLibrary gasLibrary) throws IOException {
		Fusiontables fusiontables = loadClient();

		fusiontables
		.query()
		.sql("insert into " + TABLE_ID + " (Key , Text, RegisteredAt, ModifiedAt) VALUES ('"
				+ key
				+ "', '"
				+ (Strings.nullToEmpty(gasLibrary.getLabel())
					+ " ," + Strings.nullToEmpty(gasLibrary.getDesc())
					+ " ," + Strings.nullToEmpty(gasLibrary.getLongDesc()).replace(
								"'", "''")) + "'," + gasLibrary.getRegisteredAt().getTime() + "," + gasLibrary.getModifiedAt().getTime() + ")").execute();
		
	}
	
	public static void update(String rowID, GasLibrary gasLibrary) throws IOException {
		Fusiontables fusiontables = loadClient();
		
		fusiontables
		.query()
		.sql("update " + TABLE_ID + " set Text='"
				+ (Strings.nullToEmpty(gasLibrary.getLabel())
					+ " ," + Strings.nullToEmpty(gasLibrary.getDesc())
					+ " ," + Strings.nullToEmpty(gasLibrary.getLongDesc()).replace(
								"'", "''")) + "',"
				+ " ModifiedAt=" + gasLibrary.getModifiedAt().getTime()
				+ " Where ROWID='" + rowID + "'"
				).execute();
	}
	
	public static void delete(String rowID) throws IOException {
		Fusiontables fusiontables = loadClient();
		fusiontables.query().sql("DELETE From " + TABLE_ID + " Where ROWID = '" + rowID + "'").execute();		
	}
	
	public static List<String> searchLibraryKey(String query, int offset, int limit) throws IOException, InterruptedException {
		
		Fusiontables fusiontables = loadClient();
		int errorCount = 0;
		while(true) {
			try{
		
				Sqlresponse res = fusiontables.query().sqlGet("select Key from " + TABLE_ID + " where Text CONTAINS IGNORING CASE '" + query.replace("'", "''") + "'" + 
						" ORDER BY ModifiedAt DESC" +
						" offset " + offset + 
						" limit " + limit
						).execute();
				
				
				if(res.getRows() == null || res.getRows().size() == 0) {
					return Collections.emptyList();
				}
				
				List<String> result = Lists.newArrayList();
		
				for (List<Object> col : res.getRows()) {
					result.add((String)col.get(0));
				}
		
				return result;
			} catch (Exception e) {
				errorCount++;
				
				if(errorCount > 5) {
					throw e;
				}
				Thread.sleep(100);
				
			}
		}
	}
	
	
	/**
	 * Returns an {@link IOException} (but not a subclass) in order to work
	 * around restrictive GWT serialization policy.
	 */
	static IOException wrappedIOException(IOException e) {
		if (e.getClass() == IOException.class) {
			return e;
		}
		return new IOException(e.getMessage());
	}

}
