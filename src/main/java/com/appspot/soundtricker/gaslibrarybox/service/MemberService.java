package com.appspot.soundtricker.gaslibrarybox.service;

import org.slim3.datastore.Datastore;
import org.slim3.memcache.Memcache;

import com.appspot.soundtricker.gaslibrarybox.meta.MemberMeta;
import com.appspot.soundtricker.gaslibrarybox.model.Member;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;


public class MemberService {
	
	private final static String MEMBER_CACHE_KEY_FORMAT = "member_%s";
	
	private final static MemberMeta MM = MemberMeta.get();
	
	public static Member get(User user) {
		String email = user.getEmail();
		return get(email);
	}

	public static Member get(Key key) {
		return get(key.getName());
	}
	
	public static Member get(String email) {
		Member member = getFromCache(email);
		
		if(member != null) {
			return member;
		}
		
		member = Datastore.getOrNull(MM, Datastore.createKey(MM, email));
		
		if(member != null) {
			put2Cache(member);
		}
		return member;
	}
	
	private static Member getFromCache(String email) {
		return Memcache.get(String.format(MEMBER_CACHE_KEY_FORMAT, email));
	}
	
	private static void put2Cache(Member member) {
		Memcache.put(String.format(MEMBER_CACHE_KEY_FORMAT, member.getUser().getEmail()), member);
	}

	public static void register(User user, Member m) {
		m.setKey(Datastore.createKey(MM, user.getEmail()));
		m.setUser(user);
		Datastore.put(m);
		put2Cache(m);
	}
			
		

}
