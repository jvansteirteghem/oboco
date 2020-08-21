package com.gitlab.jeeto.oboco.opds.v12;

import java.util.Date;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.gitlab.jeeto.oboco.api.v1.book.Book;
import com.gitlab.jeeto.oboco.api.v1.book.BookService;
import com.gitlab.jeeto.oboco.api.v1.book.GetBookAsStreamingOutput;
import com.gitlab.jeeto.oboco.api.v1.book.GetBookPageAsStreamingOutput;
import com.gitlab.jeeto.oboco.api.v1.bookcollection.BookCollection;
import com.gitlab.jeeto.oboco.api.v1.bookcollection.BookCollectionService;
import com.gitlab.jeeto.oboco.common.PageableList;
import com.gitlab.jeeto.oboco.common.PageableListDtoHelper;
import com.gitlab.jeeto.oboco.common.exception.Problem;
import com.gitlab.jeeto.oboco.common.exception.ProblemException;
import com.gitlab.jeeto.oboco.common.security.authentication.Authentication;
import com.gitlab.jeeto.oboco.common.security.authorization.Authorization;
import com.gitlab.jeeto.oboco.opds.opds.Author;
import com.gitlab.jeeto.oboco.opds.opds.ContentType;
import com.gitlab.jeeto.oboco.opds.opds.Contents;
import com.gitlab.jeeto.oboco.opds.opds.Entry;
import com.gitlab.jeeto.oboco.opds.opds.Feed;
import com.gitlab.jeeto.oboco.opds.opds.Link;
import com.gitlab.jeeto.oboco.plugin.image.ScaleType;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden()
@Authentication(type = "BASIC")
@Authorization(roles = { "USER" })
@Path("v1.2")
@Produces(MediaType.APPLICATION_XML)
public class V12Resource {
	@Context
	private UriInfo uriInfo;
	@Inject
	private BookCollectionService bookCollectionService;
	@Inject
	private BookService bookService;
	
	@GET
    public Response getDefault() throws ProblemException {
    	Feed.Builder feedBuilder = Feed.builder()
            .withId("opds")
            .withTitle("OPDS")
            .withUpdateDate(new Date())
            .withAuthor(Author.builder("OPDS").build())
            .addLink(Link.builder("/opds/v1.2/").withRel("self").withType("application/atom+xml; profile=opds-catalog; kind=navigation").build())
            .addLink(Link.builder("/opds/v1.2/").withRel("start").withType("application/atom+xml; profile=opds-catalog; kind=navigation").build());
    	
        Entry.Builder entryBuilder = Entry.builder()
        	.withId("opds-bookCollections")
        	.withTitle("OPDS-BookCollections")
            .withUpdateDate(new Date())
            .withContents(Contents.builder().withType(ContentType.builder("text").build()).withContents("BookCollections").build())
            .addLink(Link.builder("/opds/v1.2/bookCollections?parentBookCollectionId=").withRel("subsection").withType("application/atom+xml; profile=opds-catalog; kind=acquisition").build());
        
        feedBuilder.addEntry(entryBuilder.build());
        
        entryBuilder = Entry.builder()
        	.withId("opds-books")
        	.withTitle("OPDS-Books")
            .withUpdateDate(new Date())
            .withContents(Contents.builder().withType(ContentType.builder("text").build()).withContents("Books").build())
            .addLink(Link.builder("/opds/v1.2/books").withRel("subsection").withType("application/atom+xml; profile=opds-catalog; kind=acquisition").build());
        
        feedBuilder.addEntry(entryBuilder.build());
    	
        Feed feed = feedBuilder.build();
    	
        return Response.status(200).entity(feed).build();
    }
	
	@Path("bookCollections")
	@GET
	public Response getBookCollections(@QueryParam("parentBookCollectionId") Long parentBookCollectionId, @DefaultValue("1") @QueryParam("page") Integer page, @DefaultValue("25") @QueryParam("pageSize") Integer pageSize) throws ProblemException {
		PageableListDtoHelper.validatePageableList(page, pageSize);
		
		PageableList<BookCollection> bookCollectionPageableList = null;
		
		boolean hasParentBookCollectionId = uriInfo.getQueryParameters().containsKey("parentBookCollectionId");
		
		if(hasParentBookCollectionId) {
			bookCollectionPageableList = bookCollectionService.getBookCollectionsByParentBookCollectionId(parentBookCollectionId, page, pageSize);
		} else {
			bookCollectionPageableList = bookCollectionService.getBookCollections(page, pageSize);
		}
		
		Feed.Builder feedBuilder = Feed.builder()
            .withId("opds-bookCollection" + (parentBookCollectionId == null? "" : "-" + parentBookCollectionId.toString()) + "-bookCollections")
            .withTitle("OPDS-BookCollection" + (parentBookCollectionId == null? "" : "-" + parentBookCollectionId.toString()) + "-BookCollections")
            .withUpdateDate(new Date())
            .withAuthor(Author.builder("OPDS").build())
            .addLink(Link.builder("/opds/v1.2/bookCollections").withRel("self").withType("application/atom+xml; profile=opds-catalog; kind=navigation").build())
            .addLink(Link.builder("/opds/v1.2/").withRel("start").withType("application/atom+xml; profile=opds-catalog; kind=navigation").build());
		
		if(bookCollectionPageableList != null) {
			if(bookCollectionPageableList.getNumberOfElements() != 0L) {
				if(bookCollectionPageableList.getPreviousPage() != null) {
					if(hasParentBookCollectionId) {
						feedBuilder.addLink(Link.builder("/opds/v1.2/bookCollections?parentBookCollectionId=" + (parentBookCollectionId == null? "" : parentBookCollectionId) + "&page=" + bookCollectionPageableList.getPreviousPage() + "&pageSize=" + bookCollectionPageableList.getPageSize()).withRel("previous").withType("application/atom+xml; profile=opds-catalog; kind=navigation").build());
					} else {
						feedBuilder.addLink(Link.builder("/opds/v1.2/bookCollections?page=" + bookCollectionPageableList.getPreviousPage() + "&pageSize=" + bookCollectionPageableList.getPageSize()).withRel("previous").withType("application/atom+xml; profile=opds-catalog; kind=navigation").build());
					}
				}
				if(bookCollectionPageableList.getNextPage() != null) {
					if(hasParentBookCollectionId) {
						feedBuilder.addLink(Link.builder("/opds/v1.2/bookCollections?parentBookCollectionId=" + (parentBookCollectionId == null? "" : parentBookCollectionId) + "&page=" + bookCollectionPageableList.getNextPage() + "&pageSize=" + bookCollectionPageableList.getPageSize()).withRel("next").withType("application/atom+xml; profile=opds-catalog; kind=navigation").build());
					} else {
						feedBuilder.addLink(Link.builder("/opds/v1.2/bookCollections?page=" + bookCollectionPageableList.getNextPage() + "&pageSize=" + bookCollectionPageableList.getPageSize()).withRel("next").withType("application/atom+xml; profile=opds-catalog; kind=navigation").build());
					}
				}
			}
			
			Entry.Builder entryBuilder = Entry.builder()
	        	.withId("opds-bookCollection" + (parentBookCollectionId == null? "" : "-" + parentBookCollectionId.toString()) + "-books")
	        	.withTitle("OPDS-BookCollection" + (parentBookCollectionId == null? "" : "-" + parentBookCollectionId.toString()) + "-Books")
	            .withUpdateDate(new Date())
	            .withContents(Contents.builder().withType(ContentType.builder("text").build()).withContents("books").build())
	            .addLink(
	            		Link.builder("/opds/v1.2/books/?bookCollectionId=" + (parentBookCollectionId == null? "" : parentBookCollectionId.toString()))
	            			.withRel("subsection")
	            			.withType("application/atom+xml; profile=opds-catalog; kind=acquisition")
	            			.build()
	            );
	        
	        feedBuilder.addEntry(entryBuilder.build());
			
	        for(BookCollection bookCollection: bookCollectionPageableList.getElements()) {
	        	entryBuilder = Entry.builder()
		        	.withId(bookCollection.getId().toString())
		        	.withTitle(bookCollection.getName())
		            .withUpdateDate(new Date())
		            .withContents(Contents.builder().withType(ContentType.builder("text").build()).withContents(bookCollection.getName()).build())
		            .addLink(Link.builder("/opds/v1.2/bookCollections?parentBookCollectionId=" + bookCollection.getId()).withRel("subsection").withType("application/atom+xml; profile=opds-catalog; kind=acquisition").build());
		        
		        feedBuilder.addEntry(entryBuilder.build());
	        }
		}
    	
        Feed feed = feedBuilder.build();
	        
		return Response.status(200).entity(feed).build();
	}
	
	@Path("books")
	@GET
	public Response getBooks(@QueryParam("bookCollectionId") Long bookCollectionId, @DefaultValue("1") @QueryParam("page") Integer page, @DefaultValue("25") @QueryParam("pageSize") Integer pageSize) throws ProblemException {
		PageableListDtoHelper.validatePageableList(page, pageSize);
		
		PageableList<Book> bookPageableList = null;
		
		boolean hasBookCollectionId = uriInfo.getQueryParameters().containsKey("bookCollectionId");
		
		if(hasBookCollectionId) {
			bookPageableList = bookService.getBooksByBookCollectionId(bookCollectionId, page, pageSize);
		} else {
			bookPageableList = bookService.getBooks(page, pageSize);
		}
		
		Feed.Builder feedBuilder = Feed.builder()
            .withId("opds-bookCollection" + (bookCollectionId == null? "" : "-" + bookCollectionId) + "-books")
            .withTitle("OPDS-BookCollection" + (bookCollectionId == null? "" : "-" + bookCollectionId) + "-Books")
            .withUpdateDate(new Date())
            .withAuthor(Author.builder("OPDS").build())
            .addLink(Link.builder("/opds/v1.2/books").withRel("self").withType("application/atom+xml; profile=opds-catalog; kind=navigation").build())
            .addLink(Link.builder("/opds/v1.2/").withRel("start").withType("application/atom+xml; profile=opds-catalog; kind=navigation").build());
		
		if(bookPageableList != null) {
			if(bookPageableList.getNumberOfElements() != 0L) {
				if(bookPageableList.getPreviousPage() != null) {
					if(hasBookCollectionId) {
						feedBuilder.addLink(Link.builder("/opds/v1.2/books?bookCollectionId=" + (bookCollectionId == null? "" : bookCollectionId) + "&page=" + bookPageableList.getPreviousPage() + "&pageSize=" + bookPageableList.getPageSize()).withRel("previous").withType("application/atom+xml; profile=opds-catalog; kind=navigation").build());
					} else {
						feedBuilder.addLink(Link.builder("/opds/v1.2/books?page=" + bookPageableList.getPreviousPage() + "&pageSize=" + bookPageableList.getPageSize()).withRel("previous").withType("application/atom+xml; profile=opds-catalog; kind=navigation").build());
					}
				}
				if(bookPageableList.getNextPage() != null) {
					if(hasBookCollectionId) {
						feedBuilder.addLink(Link.builder("/opds/v1.2/books?bookCollectionId=" + (bookCollectionId == null? "" : bookCollectionId) + "&page=" + bookPageableList.getNextPage() + "&pageSize=" + bookPageableList.getPageSize()).withRel("next").withType("application/atom+xml; profile=opds-catalog; kind=navigation").build());
					} else {
						feedBuilder.addLink(Link.builder("/opds/v1.2/books?page=" + bookPageableList.getNextPage() + "&pageSize=" + bookPageableList.getPageSize()).withRel("next").withType("application/atom+xml; profile=opds-catalog; kind=navigation").build());
					}
				}
			}
			
	        for(Book book: bookPageableList.getElements()) {
	        	Entry.Builder entryBuilder = Entry.builder()
		        	.withId(book.getId().toString())
		        	.withTitle(book.getName())
		            .withUpdateDate(new Date())
		            .withContents(Contents.builder().withType(ContentType.builder("text").build()).withContents(book.getName()).build())
		            .addLink(Link.builder("/opds/v1.2/books/" + book.getId() + ".cbz").withRel("http://opds-spec.org/acquisition").withType("application/octet-stream").build())
		            .addLink(Link.builder("/opds/v1.2/books/" + book.getId() + "/pages/1.jpg?scaleType=FILL&scaleWidth=240&scaleHeight=330").withRel("http://opds-spec.org/image").withType("image/jpeg").build())
		            .addLink(Link.builder("/opds/v1.2/books/" + book.getId() + "/pages/1.jpg?scaleType=FILL&scaleWidth=240&scaleHeight=330").withRel("http://opds-spec.org/image/thumbnail").withType("image/jpeg").build());
		        
		        feedBuilder.addEntry(entryBuilder.build());
	        }
		}
    	
        Feed feed = feedBuilder.build();
	        
		return Response.status(200).entity(feed).build();
	}
	
	@Path("books/{bookId}.cbz")
	@GET
	public Response getBookAs(@PathParam("bookId") Long bookId) throws ProblemException {
		Book book = bookService.getBookById(bookId);
		
        if(book == null) {
        	throw new ProblemException(new Problem(404, "PROBLEM_BOOK_NOT_FOUND", "The book is not found."));
        }
		
        GetBookAsStreamingOutput getBookAsStreamingOutput = new GetBookAsStreamingOutput(book);
		
		return Response.status(200)
				.header("Content-Disposition", "attachment; filename=\"" + book.getName() + ".cbz\"")
				.type("application/zip")
				.entity(getBookAsStreamingOutput)
				.build();
	}
	
	@Path("books/{bookId}/pages/{page}.jpg")
	@GET
	public Response getBookPageAs(@PathParam("bookId") Long bookId, @PathParam("page") Integer page, @QueryParam("scaleType") ScaleType scaleType, @QueryParam("scaleWidth") Integer scaleWidth, @QueryParam("scaleHeight") Integer scaleHeight) throws ProblemException {
		Book book = bookService.getBookById(bookId);
		
        if(book == null) {
        	throw new ProblemException(new Problem(404, "PROBLEM_BOOK_NOT_FOUND", "The book is not found."));
        }
        
        GetBookPageAsStreamingOutput getBookPageAsStreamingOutput = new GetBookPageAsStreamingOutput(book, 1, scaleType, scaleWidth, scaleHeight);
		
		return Response.status(200)
				.header("Content-Disposition", "attachment; filename=\"" + book.getName() + "_" + page + ".jpg\"")
				.type("image/jpeg")
				.entity(getBookPageAsStreamingOutput)
				.build();
	}
}
