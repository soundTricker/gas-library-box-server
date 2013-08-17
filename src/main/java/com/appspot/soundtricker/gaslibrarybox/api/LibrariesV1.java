package com.appspot.soundtricker.gaslibrarybox.api;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;

import org.slim3.datastore.Datastore;
import org.slim3.datastore.ModelQuery;
import org.slim3.util.BeanUtil;
import org.slim3.util.CopyOptions;

import com.appspot.soundtricker.gaslibrarybox.api.model.Library;
import com.appspot.soundtricker.gaslibrarybox.meta.GasLibraryMeta;
import com.appspot.soundtricker.gaslibrarybox.model.GasLibrary;
import com.appspot.soundtricker.gaslibrarybox.model.Member;
import com.appspot.soundtricker.gaslibrarybox.service.MemberService;
import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiAuth;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethodCacheControl;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

@Api(name = "libraries",
	version = "v1",
	auth = @ApiAuth(allowCookieAuth = AnnotationBoolean.TRUE),
	scopes = {"https://www.googleapis.com/auth/userinfo.email",
    "https://www.googleapis.com/auth/userinfo.profile"},
	clientIds = {Ids.WEB_CLIENT_ID, Ids.API_EXPLORER_ID},
	audiences = {Ids.WEB_CLIENT_ID},
	defaultVersion = AnnotationBoolean.TRUE,
	description = "The Google Apps Script Library Box API"
)
public class LibrariesV1 {
	
	private static final GasLibraryMeta GLM = GasLibraryMeta.get();
	
	@ApiMethod(
			name="list",
			httpMethod= "get",
			cacheControl=@ApiMethodCacheControl(maxAge=3600)
	)
	public List<Library> listData(
			@Nullable @Named("limit") Integer limit,
			@Nullable @Named("offset") Integer offset,
			@Nullable @Named("sort") String sort
	) {
		
		ModelQuery<GasLibrary> query = Datastore.query(GLM);
		
		if(limit == null || limit > 1000 || limit < 0) limit = 200;
		if(offset == null|| offset < 0) offset = 0;
		if(sort == null) {
			sort = "desc";
		}
		
		if("asc".equals(sort)) {
			query.sort(GLM.modifitedAt.asc);
		} else {
			query.sort(GLM.modifitedAt.desc);
		}
		
		List<GasLibrary> resultList = query.limit(limit).offset(offset).prefetchSize(limit).asList();
		
		return Lists.transform(resultList, new Function<GasLibrary, Library>() {
			@Override
			public Library apply(GasLibrary gasLibrary) {
				
				Library l = new Library();
				BeanUtil.copy(gasLibrary, l);
				
				l.setKey(gasLibrary.getKey().getName());
				
				return l;
			}
		});
	}
	
	@ApiMethod(
			name = "insert",
			httpMethod = "post"
	)
	public Library insert(User user, Library library) throws ServiceException {
		
		if(user == null) {
			throw new UnauthorizedException("insert api is required authorize");
		}
		
		Member member = MemberService.get(user);
		
		if(member == null) {
			throw new ForbiddenException("Did not register your account. Please register.");
		}
		
		if(library == null) {
			throw new BadRequestException("json body is required.");
		}
		
		if(Strings.isNullOrEmpty(library.getKey())) {
			throw new BadRequestException("key property is required");
		}
		
		if(Strings.isNullOrEmpty(library.getLabel())) {
			throw new BadRequestException("label property is required");			
		}
		
		if(Strings.isNullOrEmpty(library.getSourceUrl())) {
			throw new BadRequestException("sourceUrl property is required");
		}
		
		GasLibrary gasLibrary = new GasLibrary();
		
		library.setAuthorName(member.getNickname());
		library.setAuthorUrl(member.getUrl());
		library.setAuthorIconUrl(member.getUserIconUrl());
		BeanUtil.copy(library, gasLibrary, new CopyOptions().exclude(GLM.key));
		
		gasLibrary.setAuthor(user);
		gasLibrary.setKey(Datastore.createKey(GLM, library.getKey()));
		
		Datastore.put(gasLibrary);
		
		return library;
	}
	
}
