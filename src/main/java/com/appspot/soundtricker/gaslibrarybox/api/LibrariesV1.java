package com.appspot.soundtricker.gaslibrarybox.api;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import org.slim3.datastore.Datastore;
import org.slim3.datastore.ModelQuery;
import org.slim3.datastore.S3QueryResultList;
import org.slim3.util.BeanUtil;
import org.slim3.util.CopyOptions;

import com.appspot.soundtricker.gaslibrarybox.api.model.Library;
import com.appspot.soundtricker.gaslibrarybox.meta.GasLibraryMeta;
import com.appspot.soundtricker.gaslibrarybox.meta.MemberMeta;
import com.appspot.soundtricker.gaslibrarybox.model.GasLibrary;
import com.appspot.soundtricker.gaslibrarybox.model.Member;
import com.appspot.soundtricker.gaslibrarybox.service.FusionService;
import com.appspot.soundtricker.gaslibrarybox.service.LibraryService;
import com.appspot.soundtricker.gaslibrarybox.service.MemberService;
import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiAuth;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.ApiMethodCacheControl;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.CollectionResponse.Builder;
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.InternalServerErrorException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;


@Api(name = "libraries",
	version = "v1",
	auth = @ApiAuth(allowCookieAuth = AnnotationBoolean.TRUE),
	scopes = {"https://www.googleapis.com/auth/userinfo.email"},
	clientIds = {Ids.WEB_CLIENT_ID, Ids.CHROME_EXTENSION_ID, Ids.API_EXPLORER_ID},
	audiences = {Ids.WEB_CLIENT_ID},
	defaultVersion = AnnotationBoolean.TRUE,
	description = "The Google Apps Script Library Box API"
)
public class LibrariesV1 {
	
	private static final GasLibraryMeta GLM = GasLibraryMeta.get();
	
	private static final Logger logger = Logger.getLogger(LibrariesV1.class.getName());
	
	@ApiMethod(
			name="list",
			httpMethod= HttpMethod.GET,
			cacheControl=@ApiMethodCacheControl(maxAge=3600)
	)
	public CollectionResponse<Library> listData(
			@Nullable @Named("limit") Integer limit,
			@Nullable @Named("cursor") String cursor
	) {
		ModelQuery<GasLibrary> query = Datastore.query(GLM);
		
		if(limit == null || limit > 1000 || limit < 0) {
			limit = 200;
		}
		
		if(!Strings.isNullOrEmpty(cursor)) {
			query.encodedStartCursor(cursor);
		}
		
		S3QueryResultList<GasLibrary> resultList = query.limit(limit).prefetchSize(limit).asQueryResultList();
		List<Library> convertedList = convertList(resultList);
		
		Builder<Library> builder = CollectionResponse.<Library>builder();
		
		builder.setItems(convertedList);
		
		if(resultList.hasNext()) {
			builder.setNextPageToken(resultList.getEncodedCursor());
		}
		
		return builder.build();
	}

	private static List<Library> convertList(List<GasLibrary> resultList) {
		return Lists.transform(resultList, new Function<GasLibrary, Library>() {
			@Override
			public Library apply(GasLibrary gasLibrary) {
				
				Library l = new Library();
				copy(gasLibrary, l);
				
				return l;
			}
		});
	}
	
	@ApiMethod(
			name = "get",
			httpMethod = HttpMethod.GET,
			path = "{key}"
			)
	public Library get(@Named("key") String key) throws ServiceException {
		
		GasLibrary gasLibrary = LibraryService.get(key);
		
		if(gasLibrary == null) {
			throw new NotFoundException("does not exist");
		}
		
		Library l = new Library();
		copy(gasLibrary, l);
		
		return l;
	}
	
	@ApiMethod(
			name = "users",
			path = "user/{userKey}",
			httpMethod = HttpMethod.GET
	)
	public List<Library> users(User user, @Named("userKey") String userKey) throws ServiceException {
		
		Key uk = null;
		if(user != null && "me".equals(userKey)) {
			uk = Datastore.createKey(MemberMeta.get(), user.getEmail());
		} else {
			uk = Datastore.stringToKey(userKey);
		}
		
		if(MemberService.get(uk) == null) {
			throw new NotFoundException("Not found user");
		}
			
		return convertList(LibraryService.getUserLibraries(uk));
	}
	
	@ApiMethod(
			name = "insert",
			httpMethod = HttpMethod.POST
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
		
		if(Strings.isNullOrEmpty(library.getLibraryKey())) {
			throw new BadRequestException("key property is required");
		}
		
		if(Strings.isNullOrEmpty(library.getLabel())) {
			throw new BadRequestException("label property is required");			
		}
		
		if(Strings.isNullOrEmpty(library.getSourceUrl())) {
			throw new BadRequestException("sourceUrl property is required");
		}
		
		if(LibraryService.get(library.getLibraryKey()) != null) {
			throw new ConflictException("This library have been registered");
		}
		
		GasLibrary gasLibrary = new GasLibrary();
		
		library.setAuthorName(member.getNickname());
		library.setAuthorUrl(member.getUrl());
		library.setAuthorIconUrl(member.getUserIconUrl());
		BeanUtil.copy(library, gasLibrary, new CopyOptions().exclude(GLM.authorKey));
		
		gasLibrary.setAuthorKey(member.getKey());
		
		LibraryService.put(gasLibrary, library.getLibraryKey());		
		
		copy(gasLibrary, library);
		
		return library;
	}
	
	@ApiMethod(
			name ="put",
			path = "{libraryKey}",
			httpMethod = HttpMethod.PUT
			)
	public Library put(User user, @Named("libraryKey") String key, Library library) throws ServiceException {
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
		
		GasLibrary gasLibrary = LibraryService.get(key);
		if(gasLibrary == null) {
			throw new NotFoundException("Not found library");
		}
		
		BeanUtil.copy(library, gasLibrary, new CopyOptions().include(GLM.desc, GLM.longDesc, GLM.label, GLM.sourceUrl));

		LibraryService.put(gasLibrary, key);
		
		copy(gasLibrary, library);
		
		return library;
	}
	
	@ApiMethod(
			name = "search",
			httpMethod = HttpMethod.GET
	)
	public CollectionResponse<Library> search(@Named("query") String query, @Nullable @Named("nextToken") String next) throws ServiceException, InterruptedException {
		
		if(Strings.isNullOrEmpty(query)) {
			throw new BadRequestException("given query parameter");
		}
		
		int offset = 0;
		if(!Strings.isNullOrEmpty(next)) {
			offset = Integer.parseInt(next);
		}
		
		int limit = 100;
		
		List<String> keys = null;
		try{
			keys = FusionService.searchLibraryKey(query, offset, limit);
		} catch(Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			throw new InternalServerErrorException("Sorry, please wait a few hours");
		}
		
		if(keys == null || keys.size() == 0) {
			throw new NotFoundException("Not found any libraries.");
		}
		
		List<GasLibrary> list = LibraryService.get(keys);
		
		List<Library> convertedList = convertList(list);
		
		Builder<Library> builder = CollectionResponse.<Library>builder();
		
		builder.setItems(convertedList);
		
		if(convertedList.size() >= limit) {
			builder.setNextPageToken(String.valueOf(offset + limit));
		}
		
		return builder.build();
	}
	
	@ApiMethod(
			name = "delete",
			path = "{key}",
			httpMethod = HttpMethod.DELETE
	)
	public void delete(User user, @Named("key") String key) throws ServiceException {
		
		if(user == null) {
			throw new UnauthorizedException("required authorize");
		}
		
		GasLibrary gasLibrary = LibraryService.get(key);
		
		if(gasLibrary == null) {
			throw new NotFoundException("not found library");
		}
		
		if(!gasLibrary.getAuthorKey().getName().equals(user.getEmail())) {
			throw new ForbiddenException("it's not your library, you can't delete it");
		}
		
		LibraryService.delete(gasLibrary);
	}

	private static void copy(GasLibrary gasLibrary, Library l) {
		BeanUtil.copy(gasLibrary, l, new CopyOptions().exclude(GLM.key, GLM.authorKey));

		l.setLibraryKey(gasLibrary.getKey().getName());
		l.setAuthorKey(Datastore.keyToString(gasLibrary.getAuthorKey()));
	}
	
}
