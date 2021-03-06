package com.gitlab.jeeto.oboco.api.v1.bookmark;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.SecurityContext;

import com.gitlab.jeeto.oboco.api.v1.book.Book;
import com.gitlab.jeeto.oboco.api.v1.book.BookService;
import com.gitlab.jeeto.oboco.api.v1.user.User;
import com.gitlab.jeeto.oboco.common.Graph;
import com.gitlab.jeeto.oboco.common.GraphHelper;
import com.gitlab.jeeto.oboco.common.exception.Problem;
import com.gitlab.jeeto.oboco.common.exception.ProblemDto;
import com.gitlab.jeeto.oboco.common.exception.ProblemException;
import com.gitlab.jeeto.oboco.common.security.authentication.Authentication;
import com.gitlab.jeeto.oboco.common.security.authentication.UserPrincipal;
import com.gitlab.jeeto.oboco.common.security.authorization.Authorization;

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
public class BookMarkByBookResource {
	@Context
    private SecurityContext securityContext;
	@Inject
	private BookMarkService bookMarkService;
	@Inject
	private BookService bookService;
	@Inject
	private BookMarkDtoMapper bookMarkDtoMapper;
	
	private Long bookId;
	
	public BookMarkByBookResource(Long bookId) {
		super();
		
		this.bookId = bookId;
	}
	
	@Operation(
		description = "Create or update the bookMark of the book.",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "The bookMark.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookMarkDto.class))),
    		@ApiResponse(responseCode = "400", description = "The problem: PROBLEM_BOOK_MARK_PAGE_INVALID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "401", description = "The problem: PROBLEM_USER_NOT_AUTHENTICATED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "403", description = "The problem: PROBLEM_USER_NOT_AUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "404", description = "The problem: PROBLEM_USER_ROOT_BOOK_COLLECTION_NOT_FOUND, PROBLEM_BOOK_NOT_FOUND", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "500", description = "The problem: PROBLEM", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "503", description = "The problem: PROBLEM_BOOK_SCANNER_STATUS_INVALID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class)))
    	}
    )
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createOrUpdateBookMark(
			@Parameter(name = "bookMark", description = "The bookMark.", required = true) BookMarkDto bookMarkDto) throws ProblemException {
		Graph graph = GraphHelper.createGraph("()");
		Graph fullGraph = GraphHelper.createGraph("(book(bookCollection))");
		
		GraphHelper.validateGraph(graph, fullGraph);
		
		User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();
		
		if(user.getRootBookCollection() == null) {
			throw new ProblemException(new Problem(404, "PROBLEM_USER_ROOT_BOOK_COLLECTION_NOT_FOUND", "The user.rootBookCollection is not found."));
		}
		
		Book book = bookService.getBookByUserAndId(user, bookId, null);
		
		if(book == null) {
			throw new ProblemException(new Problem(404, "PROBLEM_BOOK_NOT_FOUND", "The book is not found."));
		}
		
		if(bookMarkDto.getPage() == null) {
			throw new ProblemException(new Problem(400, "PROBLEM_BOOK_MARK_PAGE_INVALID", "The bookMark.page is invalid: bookMark.page is null."));
		}
		
		if(bookMarkDto.getPage() < 0 || bookMarkDto.getPage() > book.getNumberOfPages()) {
			throw new ProblemException(new Problem(400, "PROBLEM_BOOK_MARK_PAGE_INVALID", "The bookMark.page is invalid: bookMark.page is < 0 or bookMark.page is > book.numberOfPages."));
		}
		
		BookMarkReference bookMarkReference = bookMarkService.createOrUpdateBookMarkByUserAndBook(user, book, bookMarkDto.getPage(), graph);
		
		bookMarkDto = bookMarkDtoMapper.getBookMarkDto(bookMarkReference, graph);
		
		ResponseBuilder responseBuilder = Response.status(200);
		responseBuilder.entity(bookMarkDto);
		
		return responseBuilder.build();
	}
	
	@Operation(
		description = "Delete the bookMark of the book.",
    	responses = {
    		@ApiResponse(responseCode = "200"),
    		@ApiResponse(responseCode = "401", description = "The problem: PROBLEM_USER_NOT_AUTHENTICATED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "403", description = "The problem: PROBLEM_USER_NOT_AUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "404", description = "The problem: PROBLEM_USER_ROOT_BOOK_COLLECTION_NOT_FOUND, PROBLEM_BOOK_NOT_FOUND", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "500", description = "The problem: PROBLEM", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "503", description = "The problem: PROBLEM_BOOK_SCANNER_STATUS_INVALID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class)))
    	}
    )
	@DELETE
	public Response deleteBookMark() throws ProblemException {
		User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();
		
		if(user.getRootBookCollection() == null) {
			throw new ProblemException(new Problem(404, "PROBLEM_USER_ROOT_BOOK_COLLECTION_NOT_FOUND", "The user.rootBookCollection is not found."));
		}
		
		Book book = bookService.getBookByUserAndId(user, bookId, null);
		
		if(book == null) {
			throw new ProblemException(new Problem(404, "PROBLEM_BOOK_NOT_FOUND", "The book is not found."));
		}
		
		bookMarkService.deleteBookMarkByUserAndBook(user, book);
		
		ResponseBuilder responseBuilder = Response.status(200);
		
		return responseBuilder.build();
	}
	
	@Operation(
		description = "Get the bookMark of the book.",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "The bookMark.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookMarkDto.class))),
    		@ApiResponse(responseCode = "400", description = "The problem: PROBLEM_GRAPH_INVALID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "401", description = "The problem: PROBLEM_USER_NOT_AUTHENTICATED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "403", description = "The problem: PROBLEM_USER_NOT_AUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "404", description = "The problem: PROBLEM_USER_ROOT_BOOK_COLLECTION_NOT_FOUND, PROBLEM_BOOK_NOT_FOUND, PROBLEM_BOOK_MARK_NOT_FOUND", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "500", description = "The problem: PROBLEM", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "503", description = "The problem: PROBLEM_BOOK_SCANNER_STATUS_INVALID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class)))
    	}
    )
	@GET
	public Response getBookMark(
			@Parameter(name = "graph", description = "The graph. The full graph is (book(bookCollection)).", required = false) @DefaultValue("()") @QueryParam("graph") String graphValue) throws ProblemException {
		Graph graph = GraphHelper.createGraph(graphValue);
		Graph fullGraph = GraphHelper.createGraph("(book(bookCollection))");
		
		GraphHelper.validateGraph(graph, fullGraph);
		
		User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();
		
		if(user.getRootBookCollection() == null) {
			throw new ProblemException(new Problem(404, "PROBLEM_USER_ROOT_BOOK_COLLECTION_NOT_FOUND", "The user.rootBookCollection is not found."));
		}
		
		Book book = bookService.getBookByUserAndId(user, bookId, null);
		
		if(book == null) {
			throw new ProblemException(new Problem(404, "PROBLEM_BOOK_NOT_FOUND", "The book is not found."));
		}
		
		BookMarkReference bookMarkReference = bookMarkService.getBookMarkReferenceByUserAndBook(user, book, graph);
		
		if(bookMarkReference == null) {
			throw new ProblemException(new Problem(404, "PROBLEM_BOOK_MARK_NOT_FOUND", "The bookMark is not found."));
		}
		
		BookMarkDto bookMarkDto = bookMarkDtoMapper.getBookMarkDto(bookMarkReference, graph);
	        
		ResponseBuilder responseBuilder = Response.status(200);
		responseBuilder.entity(bookMarkDto);
		
		return responseBuilder.build();
	}
}
