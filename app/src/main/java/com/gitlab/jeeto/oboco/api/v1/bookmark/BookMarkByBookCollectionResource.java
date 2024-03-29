package com.gitlab.jeeto.oboco.api.v1.bookmark;

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

import com.gitlab.jeeto.oboco.api.ProblemDto;
import com.gitlab.jeeto.oboco.database.Graph;
import com.gitlab.jeeto.oboco.database.GraphHelper;
import com.gitlab.jeeto.oboco.database.book.Book;
import com.gitlab.jeeto.oboco.database.book.BookService;
import com.gitlab.jeeto.oboco.database.bookcollection.BookCollection;
import com.gitlab.jeeto.oboco.database.bookcollection.BookCollectionService;
import com.gitlab.jeeto.oboco.database.bookmark.BookCollectionMark;
import com.gitlab.jeeto.oboco.database.bookmark.BookMarkService;
import com.gitlab.jeeto.oboco.database.user.User;
import com.gitlab.jeeto.oboco.problem.Problem;
import com.gitlab.jeeto.oboco.problem.ProblemException;
import com.gitlab.jeeto.oboco.server.authentication.Authentication;
import com.gitlab.jeeto.oboco.server.authentication.UserPrincipal;
import com.gitlab.jeeto.oboco.server.authorization.Authorization;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
	private BookService bookService;
	@Inject
	private BookCollectionService bookCollectionService;
	@Inject
	private BookCollectionMarkDtoMapper bookCollectionMarkDtoMapper;
	
	private Long bookCollectionId;
	
	public BookMarkByBookCollectionResource(Long bookCollectionId) {
		super();
		
		this.bookCollectionId = bookCollectionId;
	}
	
	@Operation(
		description = "Create or update the bookMarks of the books of the bookCollection.",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "The bookCollectionMark.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookCollectionMarkDto.class))),
    		@ApiResponse(responseCode = "400", description = "The problem: PROBLEM_BOOK_COLLECTION_MARK_BOOK_PAGE_INVALID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "401", description = "The problem: PROBLEM_USER_NOT_AUTHENTICATED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "403", description = "The problem: PROBLEM_USER_NOT_AUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "404", description = "The problem: PROBLEM_USER_ROOT_BOOK_COLLECTION_NOT_FOUND, PROBLEM_BOOK_COLLECTION_NOT_FOUND, PROBLEM_BOOK_COLLECTION_BOOKS_NOT_FOUND", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "500", description = "The problem: PROBLEM", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "503", description = "The problem: PROBLEM_BOOK_SCANNER_STATUS_INVALID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class)))
    	}
    )
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createOrUpdateBookMarksByBookCollection(
			@Parameter(name = "bookCollectionMark", description = "The bookCollectionMark.", required = true) BookCollectionMarkDto bookCollectionMarkDto) throws ProblemException {
		Graph graph = GraphHelper.createGraph("()");
		Graph fullGraph = GraphHelper.createGraph("(bookCollection(parentBookCollection))");
		
		GraphHelper.validateGraph(graph, fullGraph);
		
		User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();
		
		if(user.getRootBookCollection() == null) {
			throw new ProblemException(new Problem(404, "PROBLEM_USER_ROOT_BOOK_COLLECTION_NOT_FOUND", "The user.rootBookCollection is not found."));
		}
		
		BookCollection bookCollection = bookCollectionService.getBookCollectionByUser(user, bookCollectionId, null);
		
		if(bookCollection == null) {
			throw new ProblemException(new Problem(404, "PROBLEM_BOOK_COLLECTION_NOT_FOUND", "The bookCollection is not found."));
		}
		
		List<Book> bookList = bookService.getBooksByUserAndBookCollection(user, bookCollection.getId());
		
		if(bookList.size() == 0) {
			throw new ProblemException(new Problem(404, "PROBLEM_BOOK_COLLECTION_BOOKS_NOT_FOUND", "The bookCollection.books are not found."));
		}
		
		if(bookCollectionMarkDto.getBookPage() == null) {
			throw new ProblemException(new Problem(400, "PROBLEM_BOOK_COLLECTION_MARK_BOOK_PAGE_INVALID", "The bookCollectionMark.bookPage is invalid: bookCollectionMark.bookPage is null."));
		}
		
		if(bookCollectionMarkDto.getBookPage() < -1 || bookCollectionMarkDto.getBookPage() > 0) {
			throw new ProblemException(new Problem(400, "PROBLEM_BOOK_COLLECTION_MARK_BOOK_PAGE_INVALID", "The bookCollectionMark.bookPage is invalid: bookCollectionMark.bookPage is < -1 or bookCollectionMark.bookPage is > 0."));
		}
		
		BookCollectionMark bookCollectionMark = bookMarkService.createOrUpdateBookMarksByUserAndBookCollection(user, bookCollection, bookList, bookCollectionMarkDto.getBookPage(), graph);
		
		bookCollectionMarkDto = bookCollectionMarkDtoMapper.getBookCollectionMarkDto(bookCollectionMark, graph);
		
		ResponseBuilder responseBuilder = Response.status(200);
		responseBuilder.entity(bookCollectionMarkDto);
		
		return responseBuilder.build();
	}
	
	@Operation(
		description = "Delete the bookMarks of the books of the bookCollection.",
    	responses = {
    		@ApiResponse(responseCode = "200"),
    		@ApiResponse(responseCode = "401", description = "The problem: PROBLEM_USER_NOT_AUTHENTICATED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "403", description = "The problem: PROBLEM_USER_NOT_AUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "404", description = "The problem: PROBLEM_USER_ROOT_BOOK_COLLECTION_NOT_FOUND, PROBLEM_BOOK_COLLECTION_NOT_FOUND, PROBLEM_BOOK_COLLECTION_BOOKS_NOT_FOUND", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "500", description = "The problem: PROBLEM", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "503", description = "The problem: PROBLEM_BOOK_SCANNER_STATUS_INVALID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class)))
    	}
    )
	@DELETE
	public Response deleteBookMarksByBookCollection() throws ProblemException {
		User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();
		
		if(user.getRootBookCollection() == null) {
			throw new ProblemException(new Problem(404, "PROBLEM_USER_ROOT_BOOK_COLLECTION_NOT_FOUND", "The user.rootBookCollection is not found."));
		}
		
		BookCollection bookCollection = bookCollectionService.getBookCollectionByUser(user, bookCollectionId, null);
		
		if(bookCollection == null) {
			throw new ProblemException(new Problem(404, "PROBLEM_BOOK_COLLECTION_NOT_FOUND", "The bookCollection is not found."));
		}
		
		List<Book> bookList = bookService.getBooksByUserAndBookCollection(user, bookCollection.getId());
		
		if(bookList.size() == 0) {
			throw new ProblemException(new Problem(404, "PROBLEM_BOOK_COLLECTION_BOOKS_NOT_FOUND", "The bookCollection.books are not found."));
		}
		
		bookMarkService.deleteBookMarksByUserAndBookCollection(user, bookCollection, bookList);
		
		ResponseBuilder responseBuilder = Response.status(200);
		
		return responseBuilder.build();
	}
}
