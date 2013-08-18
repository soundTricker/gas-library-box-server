package com.appspot.soundtricker.gaslibrarybox.api;

import javax.inject.Named;

import org.slim3.datastore.Datastore;
import org.slim3.util.BeanUtil;
import org.slim3.util.CopyOptions;

import com.appspot.soundtricker.gaslibrarybox.api.model.LibraryBoxMember;
import com.appspot.soundtricker.gaslibrarybox.meta.MemberMeta;
import com.appspot.soundtricker.gaslibrarybox.model.Member;
import com.appspot.soundtricker.gaslibrarybox.service.MemberService;
import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiAuth;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;
import com.google.common.base.Strings;

@Api(
		name = "members",
		version = "v1",
		auth = @ApiAuth(allowCookieAuth = AnnotationBoolean.TRUE),
		scopes = {"https://www.googleapis.com/auth/userinfo.email",
        "https://www.googleapis.com/auth/userinfo.profile"},
		clientIds = {Ids.WEB_CLIENT_ID, Ids.API_EXPLORER_ID},
		audiences = {Ids.WEB_CLIENT_ID},
		defaultVersion = AnnotationBoolean.TRUE,
		description = "The gas library box members API"
		)
public class MembersV1 {
	
	private static final MemberMeta MM = MemberMeta.get();
	
	@ApiMethod(
			name = "get",
			httpMethod = HttpMethod.GET,
			path = "{key}"
			)
	public LibraryBoxMember get(User user, @Named("key") String key) {
		
		if(user != null && "me".equals(key)) {
			LibraryBoxMember lbm = new LibraryBoxMember();
			Member member = MemberService.get(user);
			
			copy(member,lbm);
			
			return lbm;
		}
		
		Member member = MemberService.get(Datastore.stringToKey(key));
		LibraryBoxMember lbm = new LibraryBoxMember();
		copy(member, lbm);
		return lbm;
	}

	private void copy(Member member,LibraryBoxMember lbm) {
		BeanUtil.copy(member, lbm);
		lbm.setKey(Datastore.keyToString(member.getKey()));
	}

	@ApiMethod(
			name="register",
			httpMethod = HttpMethod.POST			
	)
	public LibraryBoxMember register(User user, LibraryBoxMember member) throws ServiceException {
		
		if(user == null) {
			throw new UnauthorizedException("api is required authorize");
		}
		
		if(member == null) {
			throw new BadRequestException("json body is required.");
		}
		
		{
			Member m = MemberService.get(user);
			
			if(m != null) {
				throw new ConflictException("you've already registered");
			}
		}
		
		if(Strings.isNullOrEmpty(member.getNickname())) {
			throw new BadRequestException("nickname property is required");
		}
		
		if(Strings.isNullOrEmpty(member.getUrl())) {
			throw new BadRequestException("url property is required");
		}
		
		if(!member.getUrl().startsWith("https://plus.google.com")) {
			throw new BadRequestException("url property should be https://plus.google.com/*");
		}
		
		Member m = new Member();
		BeanUtil.copy(member, m, new CopyOptions().exclude(MM.key));
		
		MemberService.register(user, m);
		
		BeanUtil.copy(m, member, new CopyOptions().exclude(MM.key));
		
		member.setKey(Datastore.keyToString(m.getKey()));
		
		return member;
	}
}
