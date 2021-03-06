package com.gitlab.jeeto.oboco.api.v1.bookcollection;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import com.gitlab.jeeto.oboco.api.v1.book.BookByBookCollectionResource;
import com.gitlab.jeeto.oboco.api.v1.bookmark.BookMarkByBookCollectionResource;
import com.gitlab.jeeto.oboco.api.v1.user.User;
import com.gitlab.jeeto.oboco.common.Graph;
import com.gitlab.jeeto.oboco.common.GraphHelper;
import com.gitlab.jeeto.oboco.common.PageableList;
import com.gitlab.jeeto.oboco.common.PageableListDto;
import com.gitlab.jeeto.oboco.common.PageableListDtoHelper;
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
public class BookCollectionResource {
	@Context
    private SecurityContext securityContext;
	@Context
	private ResourceContext resourceContext;
	@Context
	private UriInfo uriInfo;
	@Inject
	private BookCollectionService bookCollectionService;
	@Inject
	private BookCollectionDtoMapper bookCollectionDtoMapper;
	
	@Operation(
		description = "Get the bookCollections.",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "The bookCollections.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookCollectionPageableListDto.class))),
    		@ApiResponse(responseCode = "400", description = "The problem: PROBLEM_PAGE_INVALID, PROBLEM_PAGE_SIZE_INVALID, PROBLEM_GRAPH_INVALID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "401", description = "The problem: PROBLEM_USER_NOT_AUTHENTICATED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "403", description = "The problem: PROBLEM_USER_NOT_AUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "404", description = "The problem: PROBLEM_USER_ROOT_BOOK_COLLECTION_NOT_FOUND", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "500", description = "The problem: PROBLEM", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "503", description = "The problem: PROBLEM_BOOK_SCANNER_STATUS_INVALID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class)))
    	}
    )
	@GET
	public Response getBookCollections(
			@Parameter(name = "parentBookCollectionId", description = "The id of the parent of the bookCollection.", required = false) @QueryParam("parentBookCollectionId") Long parentBookCollectionId, 
			@Parameter(name = "name", description = "The name of the bookCollection.", required = false) @QueryParam("name") String name, 
			@Parameter(name = "page", description = "The page. The page is >= 1.", required = false) @DefaultValue("1") @QueryParam("page") Integer page, 
			@Parameter(name = "pageSize", description = "The pageSize. The pageSize is >= 1 and <= 100.", required = false) @DefaultValue("25") @QueryParam("pageSize") Integer pageSize, 
			@Parameter(name = "graph", description = "The graph. The full graph is (parentBookCollection).", required = false) @DefaultValue("()") @QueryParam("graph") String graphValue) throws ProblemException {
		PageableListDtoHelper.validatePageableList(page, pageSize);
		
		Graph graph = GraphHelper.createGraph(graphValue);
		Graph fullGraph = GraphHelper.createGraph("(parentBookCollection)");
		
		GraphHelper.validateGraph(graph, fullGraph);
		
		User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();
		
		if(user.getRootBookCollection() == null) {
			throw new ProblemException(new Problem(404, "PROBLEM_USER_ROOT_BOOK_COLLECTION_NOT_FOUND", "The user.rootBookCollection is not found."));
		}
		
		PageableList<BookCollection> bookCollectionPageableList = null;
		
		if(uriInfo.getQueryParameters().containsKey("parentBookCollectionId")) {
			if(uriInfo.getQueryParameters().containsKey("name")) {
				bookCollectionPageableList = bookCollectionService.getBookCollectionsByUserAndName(user, parentBookCollectionId, name, page, pageSize, graph);
			} else {
				bookCollectionPageableList = bookCollectionService.getBookCollectionsByUser(user, parentBookCollectionId, page, pageSize, graph);
			}
		} else {
			if(uriInfo.getQueryParameters().containsKey("name")) {
				bookCollectionPageableList = bookCollectionService.getBookCollectionsByUserAndName(user, name, page, pageSize, graph);
			} else {
				bookCollectionPageableList = bookCollectionService.getBookCollectionsByUser(user, page, pageSize, graph);
			}
		}
		
		PageableListDto<BookCollectionDto> bookCollectionPageableListDto = bookCollectionDtoMapper.getBookCollectionsDto(bookCollectionPageableList, graph);
		
		ResponseBuilder responseBuilder = Response.status(200);
		responseBuilder.entity(bookCollectionPageableListDto);
		
		return responseBuilder.build();
	}
	
	@Operation(
		description = "Get the root bookCollection.",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "The root bookCollection.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookCollectionDto.class))),
    		@ApiResponse(responseCode = "400", description = "The problem: PROBLEM_GRAPH_INVALID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "401", description = "The problem: PROBLEM_USER_NOT_AUTHENTICATED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "403", description = "The problem: PROBLEM_USER_NOT_AUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "404", description = "The problem: PROBLEM_USER_ROOT_BOOK_COLLECTION_NOT_FOUND, PROBLEM_BOOK_COLLECTION_NOT_FOUND", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "500", description = "The problem: PROBLEM", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "503", description = "The problem: PROBLEM_BOOK_SCANNER_STATUS_INVALID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class)))
    	}
    )
	@Path("ROOT")
	@GET
	public Response getRootBookCollection(
			@Parameter(name = "graph", description = "The graph. The full graph is (parentBookCollection).", required = false) @DefaultValue("()") @QueryParam("graph") String graphValue) throws ProblemException {
		Graph graph = GraphHelper.createGraph(graphValue);
		Graph fullGraph = GraphHelper.createGraph("(parentBookCollection)");
		
		GraphHelper.validateGraph(graph, fullGraph);
		
		User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();
		
		if(user.getRootBookCollection() == null) {
			throw new ProblemException(new Problem(404, "PROBLEM_USER_ROOT_BOOK_COLLECTION_NOT_FOUND", "The user.rootBookCollection is not found."));
		}
		
		Long rootBookCollectionId = user.getRootBookCollection().getId();
		
		BookCollection bookCollection = bookCollectionService.getRootBookCollectionById(rootBookCollectionId, graph);
		
		if(bookCollection == null) {
			throw new ProblemException(new Problem(404, "PROBLEM_BOOK_COLLECTION_NOT_FOUND", "The bookCollection is not found."));
		}
		
		BookCollectionDto bookCollectionDto = bookCollectionDtoMapper.getBookCollectionDto(bookCollection, graph);
	        
		ResponseBuilder responseBuilder = Response.status(200);
		responseBuilder.entity(bookCollectionDto);
		
		return responseBuilder.build();
	}
	
	@Operation(
		description = "Get the latest bookCollections.",
		responses = {
			@ApiResponse(responseCode = "200", description = "The latest bookCollections.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookCollectionPageableListDto.class))),
			@ApiResponse(responseCode = "400", description = "The problem: PROBLEM_PAGE_INVALID, PROBLEM_PAGE_SIZE_INVALID, PROBLEM_GRAPH_INVALID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
			@ApiResponse(responseCode = "401", description = "The problem: PROBLEM_USER_NOT_AUTHENTICATED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
			@ApiResponse(responseCode = "403", description = "The problem: PROBLEM_USER_NOT_AUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
			@ApiResponse(responseCode = "404", description = "The problem: PROBLEM_USER_ROOT_BOOK_COLLECTION_NOT_FOUND", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
			@ApiResponse(responseCode = "500", description = "The problem: PROBLEM", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "503", description = "The problem: PROBLEM_BOOK_SCANNER_STATUS_INVALID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class)))
		}
	)
	@Path("LATEST/bookCollections")
	@GET
	public Response getLatestBookCollections(
			@Parameter(name = "name", description = "The name of the bookCollection.", required = false) @QueryParam("name") String name, 
			@Parameter(name = "page", description = "The page. The page is >= 1.", required = false) @DefaultValue("1") @QueryParam("page") Integer page, 
			@Parameter(name = "pageSize", description = "The pageSize. The pageSize is >= 1 and <= 100.", required = false) @DefaultValue("25") @QueryParam("pageSize") Integer pageSize, 
			@Parameter(name = "graph", description = "The graph. The full graph is (parentBookCollection).", required = false) @DefaultValue("()") @QueryParam("graph") String graphValue) throws ProblemException {
		PageableListDtoHelper.validatePageableList(page, pageSize);
		
		Graph graph = GraphHelper.createGraph(graphValue);
		Graph fullGraph = GraphHelper.createGraph("(parentBookCollection)");
		
		GraphHelper.validateGraph(graph, fullGraph);
		
		User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();
		
		if(user.getRootBookCollection() == null) {
			throw new ProblemException(new Problem(404, "PROBLEM_USER_ROOT_BOOK_COLLECTION_NOT_FOUND", "The user.rootBookCollection is not found."));
		}
		
		PageableList<BookCollection> bookCollectionPageableList = bookCollectionService.getLatestBookCollectionsByUserAndName(user, name, page, pageSize, graph);
		
		PageableListDto<BookCollectionDto> bookCollectionPageableListDto = bookCollectionDtoMapper.getBookCollectionsDto(bookCollectionPageableList, graph);
		
		ResponseBuilder responseBuilder = Response.status(200);
		responseBuilder.entity(bookCollectionPageableListDto);
		
		return responseBuilder.build();
	}
	
	@Operation(
		description = "Get the bookCollection.",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "The bookCollection.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookCollectionDto.class))),
    		@ApiResponse(responseCode = "400", description = "The problem: PROBLEM_GRAPH_INVALID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "401", description = "The problem: PROBLEM_USER_NOT_AUTHENTICATED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "403", description = "The problem: PROBLEM_USER_NOT_AUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "404", description = "The problem: PROBLEM_USER_ROOT_BOOK_COLLECTION_NOT_FOUND, PROBLEM_BOOK_COLLECTION_NOT_FOUND", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "500", description = "The problem: PROBLEM", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "503", description = "The problem: PROBLEM_BOOK_SCANNER_STATUS_INVALID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class)))
    	}
    )
	@Path("{bookCollectionId}")
	@GET
	public Response getBookCollection(
			@Parameter(name = "bookCollectionId", description = "The id of the bookCollection.", required = true) @PathParam("bookCollectionId") Long bookCollectionId, 
			@Parameter(name = "graph", description = "The graph. The full graph is (parentBookCollection).", required = false) @DefaultValue("()") @QueryParam("graph") String graphValue) throws ProblemException {
		Graph graph = GraphHelper.createGraph(graphValue);
		Graph fullGraph = GraphHelper.createGraph("(parentBookCollection)");
		
		GraphHelper.validateGraph(graph, fullGraph);
		
		User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();
		
		if(user.getRootBookCollection() == null) {
			throw new ProblemException(new Problem(404, "PROBLEM_USER_ROOT_BOOK_COLLECTION_NOT_FOUND", "The user.rootBookCollection is not found."));
		}
		
		BookCollection bookCollection = bookCollectionService.getBookCollectionByUserAndId(user, bookCollectionId, graph);
		
		if(bookCollection == null) {
			throw new ProblemException(new Problem(404, "PROBLEM_BOOK_COLLECTION_NOT_FOUND", "The bookCollection is not found."));
		}
		
		BookCollectionDto bookCollectionDto = bookCollectionDtoMapper.getBookCollectionDto(bookCollection, graph);
	        
		ResponseBuilder responseBuilder = Response.status(200);
		responseBuilder.entity(bookCollectionDto);
		
		return responseBuilder.build();
	}
	
	@Path("{bookCollectionId}/books")
	public BookByBookCollectionResource getBookByBookCollectionResource(
			@Parameter(name = "bookCollectionId", description = "The id of the bookCollection.", required = true) @PathParam("bookCollectionId") Long bookCollectionId) {
		return resourceContext.initResource(new BookByBookCollectionResource(bookCollectionId));
	}
	
	@Path("{bookCollectionId}/bookMarks")
	public BookMarkByBookCollectionResource getBookMarkByBookCollectionResource(
			@Parameter(name = "bookCollectionId", description = "The id of the bookCollection.", required = true) @PathParam("bookCollectionId") Long bookCollectionId) {
		return resourceContext.initResource(new BookMarkByBookCollectionResource(bookCollectionId));
	}
}
