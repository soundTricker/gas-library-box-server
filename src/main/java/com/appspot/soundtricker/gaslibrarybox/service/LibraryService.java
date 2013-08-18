package com.appspot.soundtricker.gaslibrarybox.service;

import java.util.List;

import org.slim3.datastore.Datastore;
import org.slim3.memcache.Memcache;

import com.appspot.soundtricker.gaslibrarybox.meta.GasLibraryMeta;
import com.appspot.soundtricker.gaslibrarybox.model.GasLibrary;
import com.google.appengine.api.datastore.Key;
import com.google.common.collect.Lists;


public class LibraryService {

	private static final GasLibraryMeta GLM = GasLibraryMeta.get();
	
	private final static String LIBRARY_CACHE_KEY_FORMAT = "library_%s";

	
	
	public static GasLibrary get(String key) {
		
		GasLibrary gasLibrary = getFromCache(key);
		
		if(gasLibrary != null) {
			return gasLibrary;
		}
		
		GasLibrary library = Datastore.getOrNull(GLM, Datastore.createKey(GLM, key));
		
		put2Cache(library, key);
		
		return library;
	}
	
	private static void put2Cache(GasLibrary library, String key) {
		Memcache.put(String.format(LIBRARY_CACHE_KEY_FORMAT, key), library);
	}

	private static GasLibrary getFromCache(String key) {
		return Memcache.get(String.format(LIBRARY_CACHE_KEY_FORMAT, key));
	}

	public static List<GasLibrary> getUserLibraries(Key userKey) {
		
		List<Key> libraryKeys = Datastore.query(GLM).filter(GLM.authorKey.equal(userKey)).asKeyList();
		
		
		List<GasLibrary> result = Lists.newArrayList();
		List<Key> unhitKeys = Lists.newArrayList();
		for (Key key : libraryKeys) {
			GasLibrary gasLibrary = getFromCache(key.getName());
			
			if(gasLibrary == null) {
				unhitKeys.add(key);
				continue;
			}
			
			result.add(gasLibrary);
		}
		
		result.addAll(Datastore.get(GLM, unhitKeys));
		
		return Datastore.sortInMemory(result, GLM.modifiedAt.desc);
		
	}

	public static void delete(GasLibrary gasLibrary) {
		Memcache.delete(String.format(LIBRARY_CACHE_KEY_FORMAT, gasLibrary.getKey().getName()));
		Datastore.delete(gasLibrary.getKey());
	}

	public static void put(GasLibrary gasLibrary, String key) {
		gasLibrary.setKey(Datastore.createKey(GLM, key));
		
		Datastore.put(gasLibrary);
		
		put2Cache(gasLibrary, key);
		
		//TODO fts
	}
	
}
