package com.gitlab.jeeto.oboco.api.v1.bookscanner;

import java.io.IOException;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.Provider;

import org.glassfish.hk2.api.IterableProvider;

import com.gitlab.jeeto.oboco.api.ProblemDto;
import com.gitlab.jeeto.oboco.data.bookscanner.BookScanner;
import com.gitlab.jeeto.oboco.data.bookscanner.BookScannerStatus;

@Provider
@Priority(Priorities.USER)
public class BookScannerRequestFilter implements ContainerRequestFilter {
	@Inject
	private IterableProvider<BookScanner> bookScannerProvider;
	
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
    	String path = requestContext.getUriInfo().getPath();
    	if(path.startsWith("v1/books") || path.startsWith("v1/bookCollections") || path.startsWith("v1/bookMarks")) {
    		for(BookScanner bookScanner: bookScannerProvider) {
    			if(BookScannerStatus.STOPPED.equals(bookScanner.getStatus()) == false) {
    				ResponseBuilder responseBuilder = Response.status(503);
					responseBuilder.entity(new ProblemDto(503, "PROBLEM_BOOK_SCANNER_STATUS_INVALID", "The bookScanner.status is invalid: " + bookScanner.getStatus() + "."));
		    		
					requestContext.abortWith(responseBuilder.build());
    			}
    		}
    	}
    }
}