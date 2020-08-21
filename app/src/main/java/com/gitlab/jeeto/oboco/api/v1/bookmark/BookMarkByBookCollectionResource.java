package com.gitlab.jeeto.oboco.api.v1.bookmark;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.SecurityContext;

import com.gitlab.jeeto.oboco.api.v1.book.Book;
import com.gitlab.jeeto.oboco.api.v1.book.BookService;
import com.gitlab.jeeto.oboco.api.v1.bookcollection.BookCollection;
import com.gitlab.jeeto.oboco.api.v1.bookcollection.BookCollectionService;
import com.gitlab.jeeto.oboco.common.exception.Problem;
import com.gitlab.jeeto.oboco.common.exception.ProblemDto;
import com.gitlab.jeeto.oboco.common.exception.ProblemException;
import com.gitlab.jeeto.oboco.common.security.authentication.Authentication;
import com.gitlab.jeeto.oboco.common.security.authorization.Authorization;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SecurityRequirement(name = "bearerAuth")
@Authentication(type = "BEARER")
@Authorization(roles = { "USER" })
@Produces(MediaType.APPLICATION_JSON)
public class BookMarkByBookCollectionResource {
	@Context
    private SecurityContext securityContext;
	@Inject
	private BookMarkService bookMarkService;
	@Inject
	private BookCollectionService bookCollectionService;
	@Inject
	private BookService bookService;
	
	private Long bookCollectionId;
	
	public BookMarkByBookCollectionResource(Long bookCollectionId) {
		super();
		
		this.bookCollectionId = bookCollectionId;
	}
	
	@Operation(
		description = "Create or update the bookMarks of the books of the bookCollection. The bookMark.page is the last page of the book.",
    	responses = {
    		@ApiResponse(responseCode = "200"),
    		@ApiResponse(responseCode = "400", description = "The problem: PROBLEM_BOOK_COLLECTION_ID_INVALID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "401", description = "The problem: PROBLEM_USER_NOT_AUTHENTICATED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "403", description = "The problem: PROBLEM_USER_NOT_AUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "404", description = "The problem: PROBLEM_BOOK_MARK_NOT_FOUND", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "500", description = "The problem: PROBLEM", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class)))
    	}
    )
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createOrUpdateBookMarksByBookCollection() throws ProblemException {
		String userName = securityContext.getUserPrincipal().getName();
		
		BookCollection bookCollection = bookCollectionService.getBookCollectionById(bookCollectionId);
		
		if(bookCollection == null) {
			throw new ProblemException(new Problem(400, "PROBLEM_BOOK_COLLECTION_ID_INVALID", "The bookCollection.id is invalid."));
		}
		
		Date updateDate = new Date();
		
		List<Book> bookList = bookCollection.getBooks();
		
		for(Book book: bookList) {
			BookMarkReference bookMarkReference = bookMarkService.getBookMarkReferenceByUserNameAndBookId(userName, book.getId());
			
			if(bookMarkReference == null) {
				BookMark bookMark = new BookMark();
				bookMark.setUserName(userName);
				bookMark.setFileId(book.getFileId());
				bookMark.setUpdateDate(updateDate);
				bookMark.setPage(book.getNumberOfPages());
				
				List<BookMarkReference> bookMarkReferenceList = new ArrayList<BookMarkReference>();
				
				List<Book> referencedBookList = bookService.getBooksByFileId(book.getFileId());
				
				for(Book referencedBook: referencedBookList) {
					bookMarkReference = new BookMarkReference();
					bookMarkReference.setUserName(userName);
					bookMarkReference.setFileId(referencedBook.getFileId());
					bookMarkReference.setUpdateDate(updateDate);
					bookMarkReference.setBook(referencedBook);
					bookMarkReference.setBookMark(bookMark);
					
					bookMarkReferenceList.add(bookMarkReference);
				}
				
				bookMark.setBookMarkReferences(bookMarkReferenceList);
				
				bookMarkService.createBookMark(bookMark);
			} else {
				BookMark bookMark = bookMarkReference.getBookMark();
				bookMark.setUpdateDate(updateDate);
				bookMark.setPage(book.getNumberOfPages());
				
				bookMark = bookMarkService.updateBookMark(bookMark);
			}
		}
		
		ResponseBuilder responseBuilder = Response.status(200);
		
		return responseBuilder.build();
	}
	
	@Operation(
		description = "Delete the bookMarks of the books of the bookCollection.",
    	responses = {
    		@ApiResponse(responseCode = "200"),
    		@ApiResponse(responseCode = "400", description = "The problem: PROBLEM_BOOK_COLLECTION_ID_INVALID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "401", description = "The problem: PROBLEM_USER_NOT_AUTHENTICATED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "403", description = "The problem: PROBLEM_USER_NOT_AUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "404", description = "The problem: PROBLEM_BOOK_MARK_NOT_FOUND", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "500", description = "The problem: PROBLEM", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class)))
    	}
    )
	@DELETE
	public Response deleteBookMarksByBookCollection() throws ProblemException {
		String userName = securityContext.getUserPrincipal().getName();
		
		BookCollection bookCollection = bookCollectionService.getBookCollectionById(bookCollectionId);
		
		if(bookCollection == null) {
			throw new ProblemException(new Problem(400, "PROBLEM_BOOK_COLLECTION_ID_INVALID", "The bookCollection.id is invalid."));
		}
		
		List<Book> bookList = bookCollection.getBooks();
		
		for(Book book: bookList) {
			BookMarkReference bookMarkReference = bookMarkService.getBookMarkReferenceByUserNameAndBookId(userName, book.getId());
			
			if(bookMarkReference != null) {
				BookMark bookMark = bookMarkReference.getBookMark();
				
				bookMarkService.deleteBookMark(bookMark);
			}
		}
		
		ResponseBuilder responseBuilder = Response.status(200);
		
		return responseBuilder.build();
	}
}
