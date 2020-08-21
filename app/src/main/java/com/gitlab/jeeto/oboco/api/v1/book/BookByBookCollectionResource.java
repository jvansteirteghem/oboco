package com.gitlab.jeeto.oboco.api.v1.book;

import java.util.Date;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import com.gitlab.jeeto.oboco.api.v1.bookmark.BookMarkStatus;
import com.gitlab.jeeto.oboco.common.GraphDto;
import com.gitlab.jeeto.oboco.common.GraphDtoHelper;
import com.gitlab.jeeto.oboco.common.PageableList;
import com.gitlab.jeeto.oboco.common.PageableListDto;
import com.gitlab.jeeto.oboco.common.PageableListDtoHelper;
import com.gitlab.jeeto.oboco.common.exception.Problem;
import com.gitlab.jeeto.oboco.common.exception.ProblemDto;
import com.gitlab.jeeto.oboco.common.exception.ProblemException;
import com.gitlab.jeeto.oboco.common.security.authentication.Authentication;
import com.gitlab.jeeto.oboco.common.security.authorization.Authorization;
import com.gitlab.jeeto.oboco.plugin.image.ScaleType;

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
public class BookByBookCollectionResource {
	@Context
    private SecurityContext securityContext;
	@Context
	private UriInfo uriInfo;
	@Inject
	private BookService bookService;
	@Inject
	private BookDtoMapper bookDtoMapper;
	
	private Long bookCollectionId;
	
	public BookByBookCollectionResource(Long bookCollectionId) {
		super();
		
		this.bookCollectionId = bookCollectionId;
	}
	
	@Operation(
		description = "Get the books of the bookCollection.",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "The books.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BooksDto.class))),
    		@ApiResponse(responseCode = "400", description = "The problem: PROBLEM_PAGE_INVALID, PROBLEM_PAGE_SIZE_INVALID, PROBLEM_GRAPH_INVALID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "401", description = "The problem: PROBLEM_USER_NOT_AUTHENTICATED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "403", description = "The problem: PROBLEM_USER_NOT_AUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "500", description = "The problem: PROBLEM", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class)))
    	}
    )
	@GET
	public Response getBooksByBookCollection(
			@Parameter(name = "bookMarkStatus", description = "The status of the bookMark of the book. The status is READ, UNREAD or READING.", required = false) @QueryParam("bookMarkStatus") BookMarkStatus bookMarkStatus, 
			@Parameter(name = "page", description = "The page. The page is >= 1.", required = false) @DefaultValue("1") @QueryParam("page") Integer page, 
			@Parameter(name = "pageSize", description = "The pageSize. The pageSize is >= 1 and <= 100.", required = false) @DefaultValue("25") @QueryParam("pageSize") Integer pageSize, 
			@Parameter(name = "graph", description = "The graph. The full graph is (bookCollection,bookMark).", required = false) @DefaultValue("()") @QueryParam("graph") String graphValue) throws ProblemException {
		PageableListDtoHelper.validatePageableList(page, pageSize);
		
		GraphDto graphDto = GraphDtoHelper.createGraphDto(graphValue);
		GraphDto fullGraphDto = GraphDtoHelper.createGraphDto("(bookCollection,bookMark)");
		
		GraphDtoHelper.validateGraphDto(graphDto, fullGraphDto);
		
		String userName = securityContext.getUserPrincipal().getName();
		
		PageableList<Book> bookPageableList = null;
		
		if(uriInfo.getQueryParameters().containsKey("bookMarkStatus")) {
			bookPageableList = bookService.getBooksByBookCollectionId(bookCollectionId, userName, bookMarkStatus, page, pageSize);
		} else {
			bookPageableList = bookService.getBooksByBookCollectionId(bookCollectionId, page, pageSize);
		}
		
		PageableListDto<BookDto> bookPageableListDto = bookDtoMapper.getBooksDto(bookPageableList, graphDto);;
		
		ResponseBuilder responseBuilder = Response.status(200);
		responseBuilder.entity(bookPageableListDto);
		
		return responseBuilder.build();
	}
	
	@Operation(
		description = "Get the books in the neighbourhood of the book of the bookCollection.",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "The books.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BooksDto.class))),
    		@ApiResponse(responseCode = "400", description = "The problem: PROBLEM_GRAPH_INVALID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "401", description = "The problem: PROBLEM_USER_NOT_AUTHENTICATED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "403", description = "The problem: PROBLEM_USER_NOT_AUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "500", description = "The problem: PROBLEM", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class)))
    	}
    )
	@Path("{bookId}/NEIGHBOURHOOD")
	@GET
	public Response getBooksByBookCollectionAndBook(
			@Parameter(name = "bookId", description = "The id of the book.", required = false) @PathParam("bookId") Long bookId, 
			@Parameter(name = "graph", description = "The graph. The full graph is (bookCollection,bookMark).", required = false) @DefaultValue("()") @QueryParam("graph") String graphValue) throws ProblemException {
		GraphDto graphDto = GraphDtoHelper.createGraphDto(graphValue);
		GraphDto fullGraphDto = GraphDtoHelper.createGraphDto("(bookCollection,bookMark)");
		
		GraphDtoHelper.validateGraphDto(graphDto, fullGraphDto);
		
		PageableList<Book> bookPageableList = bookService.getBooksByBookCollectionIdAndId(bookCollectionId, bookId);
		PageableListDto<BookDto> bookPageableListDto = bookDtoMapper.getBooksDto(bookPageableList, graphDto);
		
		ResponseBuilder responseBuilder = Response.status(200);
		responseBuilder.entity(bookPageableListDto);
		
		return responseBuilder.build();
	}
	
	@Operation(
		description = "Get the first page of the first book of the bookCollection as *.jpg.",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "The page.", content = @Content(mediaType = "image/jpeg")),
    		@ApiResponse(responseCode = "400", description = "The problem: PROBLEM_BOOK_COLLECTION_ID_INVALID, PROBLEM_BOOK_ID_INVALID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "401", description = "The problem: PROBLEM_USER_NOT_AUTHENTICATED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "403", description = "The problem: PROBLEM_USER_NOT_AUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "404", description = "The problem: PROBLEM_PAGE_NOT_FOUND", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "500", description = "The problem: PROBLEM", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class)))
    	}
    )
	@Path("FIRST/pages/1.jpg")
	@GET
	public Response getBookPageAsByBookCollectionAndBook(
			@Parameter(name = "scaleType", description = "The scaleType. The scaleType is DEFAULT, FIT or FILL.", required = false) @QueryParam("scaleType") ScaleType scaleType, 
			@Parameter(name = "scaleWidth", description = "The scaleWidth.", required = false) @QueryParam("scaleWidth") Integer scaleWidth, 
			@Parameter(name = "scaleHeight", description = "The scaleHeight.", required = false) @QueryParam("scaleHeight") Integer scaleHeight, 
			@Context Request request) throws ProblemException {
		Book book = null;
		
		PageableList<Book> bookPageableList = bookService.getBooksByBookCollectionId(bookCollectionId, 1, 1);
		
		if(bookPageableList.getElements() != null && bookPageableList.getElements().size() == 1) {
			book = bookPageableList.getElements().get(0);
		}
		
        if(book == null) {
        	throw new ProblemException(new Problem(400, "PROBLEM_BOOK_ID_INVALID", "The book.id is invalid."));
        }
        
        String tagValue = book.getFileId();
        tagValue = tagValue + "-page1";
        if(scaleType != null) {
        	tagValue = tagValue + "-scaleType" + scaleType;
        }
        if(scaleWidth != null) {
        	tagValue = tagValue + "-scaleWidth" + scaleWidth;
        }
        if(scaleHeight != null) {
        	tagValue = tagValue + "-scaleHeight" + scaleHeight;
        }
        
        EntityTag tag = new EntityTag(tagValue);
        
        Date updateDate = book.getUpdateDate();
		
		CacheControl cacheControl = new CacheControl();
		cacheControl.setMaxAge(300);
		
		ResponseBuilder responseBuilder = request.evaluatePreconditions(updateDate, tag);
		if(responseBuilder != null) {
			responseBuilder.cacheControl(cacheControl);
			
			return responseBuilder.build();
		}
		
		GetBookPageAsStreamingOutput getBookPageAsStreamingOutput = new GetBookPageAsStreamingOutput(book, 1, scaleType, scaleWidth, scaleHeight);
		
		responseBuilder = Response.status(200);
		responseBuilder.cacheControl(cacheControl);
		responseBuilder.tag(tag);
		responseBuilder.lastModified(updateDate);
		responseBuilder.type("image/jpeg");
		responseBuilder.entity(getBookPageAsStreamingOutput);
		
		return responseBuilder.build();
	}
}
