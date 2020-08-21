package com.gitlab.jeeto.oboco.api.v1;

import javax.ws.rs.Path;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;

import com.gitlab.jeeto.oboco.api.v1.book.BookResource;
import com.gitlab.jeeto.oboco.api.v1.bookcollection.BookCollectionResource;
import com.gitlab.jeeto.oboco.api.v1.bookmark.BookMarkResource;
import com.gitlab.jeeto.oboco.api.v1.bookscanner.BookScannerResource;
import com.gitlab.jeeto.oboco.api.v1.user.UserResource;
import com.gitlab.jeeto.oboco.common.security.authentication.AuthenticationResource;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
    		title = "Api.",
    		version = "v1",
            description = "Api."
    ), 
    servers = {
    	@Server(
    		url = "/api"
    	)
    }
)
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer")
@Path("v1")
public class V1Resource {
	@Context
	private ResourceContext resourceContext;
	
	@Path("books")
	public BookResource getBookResource() {
		return resourceContext.initResource(new BookResource());
	}
	
	@Path("bookCollections")
	public BookCollectionResource getBookCollectionResource() {
		return resourceContext.initResource(new BookCollectionResource());
	}
	
	@Path("bookMarks")
	public BookMarkResource getBookMarkResource() {
		return resourceContext.initResource(new BookMarkResource());
	}
	
	@Path("bookScanners")
	public BookScannerResource getBookScannerResource() {
		return resourceContext.initResource(new BookScannerResource());
	}
	
	@Path("users")
	public UserResource getUserResource() {
		return resourceContext.initResource(new UserResource());
	}
	
	@Path("authentication")
	public AuthenticationResource getAuthenticationResource() {
		return resourceContext.initResource(new AuthenticationResource());
	}
}
