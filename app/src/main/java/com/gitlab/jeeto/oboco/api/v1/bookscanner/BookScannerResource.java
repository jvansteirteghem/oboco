package com.gitlab.jeeto.oboco.api.v1.bookscanner;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.glassfish.hk2.api.IterableProvider;

import com.gitlab.jeeto.oboco.common.Graph;
import com.gitlab.jeeto.oboco.common.GraphHelper;
import com.gitlab.jeeto.oboco.common.exception.Problem;
import com.gitlab.jeeto.oboco.common.exception.ProblemDto;
import com.gitlab.jeeto.oboco.common.exception.ProblemException;
import com.gitlab.jeeto.oboco.common.security.authentication.Authentication;
import com.gitlab.jeeto.oboco.common.security.authorization.Authorization;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SecurityRequirement(name = "bearerAuth")
@Authentication(type = "BEARER")
@Authorization(roles = { "ADMINISTRATOR" })
@Produces(MediaType.APPLICATION_JSON)
public class BookScannerResource {
	@Inject
	private IterableProvider<BookScannerService> bookScannerServiceProvider;
	
	@Operation(
		description = "Get the bookScanners.",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "The bookScanners.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookScannerDto.class))),
    		@ApiResponse(responseCode = "400", description = "The problem: PROBLEM_GRAPH_INVALID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "401", description = "The problem: PROBLEM_USER_NOT_AUTHENTICATED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "403", description = "The problem: PROBLEM_USER_NOT_AUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "500", description = "The problem: PROBLEM", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class)))
    	}
    )
	@GET
	public Response getBookScanners(
			@Parameter(name = "graph", description = "The graph. The full graph is ().", required = false) @DefaultValue("()") @QueryParam("graph") String graphValue) throws ProblemException {
		Graph graph = GraphHelper.createGraph(graphValue);
		Graph fullGraph = GraphHelper.createGraph("()");
		
		GraphHelper.validateGraph(graph, fullGraph);
		
		List<BookScannerDto> bookScannerListDto = new ArrayList<BookScannerDto>();
		
		for(BookScannerService bookScannerService: bookScannerServiceProvider) {
			BookScannerDto bookScannerDto = new BookScannerDto();
			bookScannerDto.setId(bookScannerService.getId());
			bookScannerDto.setStatus(bookScannerService.getStatus().toString());
			
			bookScannerListDto.add(bookScannerDto);
        }
		
		ResponseBuilder responseBuilder = Response.status(200);
		responseBuilder.entity(bookScannerListDto);
		
		return responseBuilder.build();
	}
	
	@Operation(
		description = "Get the bookScanner.",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "The bookScanner.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookScannerDto.class))),
    		@ApiResponse(responseCode = "400", description = "The problem: PROBLEM_GRAPH_INVALID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "401", description = "The problem: PROBLEM_USER_NOT_AUTHENTICATED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "403", description = "The problem: PROBLEM_USER_NOT_AUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "404", description = "The problem: PROBLEM_BOOK_SCANNER_NOT_FOUND", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "500", description = "The problem: PROBLEM", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class)))
    	}
    )
	@Path("{bookScannerId}")
	@GET
	public Response getBookScanner(
			@Parameter(name = "bookScannerId", description = "The id of the bookScanner.", required = true) @PathParam("bookScannerId") String bookScannerId,
			@Parameter(name = "graph", description = "The graph. The full graph is ().", required = false) @DefaultValue("()") @QueryParam("graph") String graphValue) throws ProblemException {
		Graph graph = GraphHelper.createGraph(graphValue);
		Graph fullGraph = GraphHelper.createGraph("()");
		
		GraphHelper.validateGraph(graph, fullGraph);
		
		BookScannerService bookScannerService = bookScannerServiceProvider.named(bookScannerId).get();
		
		if(bookScannerService == null) {
			throw new ProblemException(new Problem(404, "PROBLEM_BOOK_SCANNER_NOT_FOUND", "The bookScanner is not found."));
		}
				
		BookScannerDto bookScannerDto = new BookScannerDto();
		bookScannerDto.setId(bookScannerService.getId());
		bookScannerDto.setStatus(bookScannerService.getStatus().toString());
		
		ResponseBuilder responseBuilder = Response.status(200);
		responseBuilder.entity(bookScannerDto);
		
		return responseBuilder.build();
	}
	
	@Operation(
		description = "Start the bookScanner.",
    	responses = {
    		@ApiResponse(responseCode = "200"),
    		@ApiResponse(responseCode = "400", description = "The problem: PROBLEM_BOOK_SCANNER_STATUS_INVALID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "401", description = "The problem: PROBLEM_USER_NOT_AUTHENTICATED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "403", description = "The problem: PROBLEM_USER_NOT_AUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "404", description = "The problem: PROBLEM_BOOK_SCANNER_NOT_FOUND", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "500", description = "The problem: PROBLEM", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class)))
    	}
    )
	@Path("{bookScannerId}/start")
	@POST
	public void startBookScanner(
			@Parameter(name = "bookScannerId", description = "The id of the bookScanner.", required = true) @PathParam("bookScannerId") String bookScannerId, 
			@Suspended AsyncResponse asyncResponse) {
		try {
			for(BookScannerService bookScannerService: bookScannerServiceProvider) {
    			if(bookScannerService.getStatus().equals(BookScannerServiceStatus.STOPPED) == false) {
    				throw new ProblemException(new Problem(400, "PROBLEM_BOOK_SCANNER_STATUS_INVALID", "The bookScanner.status is invalid: " + bookScannerService.getStatus() + "."));
    			}
    		}
			
			BookScannerService bookScannerService = bookScannerServiceProvider.named(bookScannerId).get();
			
			if(bookScannerService == null) {
				throw new ProblemException(new Problem(404, "PROBLEM_BOOK_SCANNER_NOT_FOUND", "The bookScanner is not found."));
			}
			
			bookScannerService.start();
			
			ResponseBuilder responseBuilder = Response.status(200);
			
			asyncResponse.resume(responseBuilder.build());
		} catch(ProblemException e) {
			Problem problem = e.getProblem();
			
			ProblemDto problemDto = new ProblemDto();
			problemDto.setStatusCode(problem.getStatusCode());
			problemDto.setCode(problem.getCode());
			problemDto.setDescription(problem.getDescription());
			
			ResponseBuilder responseBuilder = Response.status(problemDto.getStatusCode());
			responseBuilder.entity(problemDto);
			
			asyncResponse.resume(responseBuilder.build());
		}
	}
	
	@Operation(
		description = "Stop the bookScanner.",
    	responses = {
    		@ApiResponse(responseCode = "200"),
    		@ApiResponse(responseCode = "400", description = "The problem: PROBLEM_BOOK_SCANNER_STATUS_INVALID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "401", description = "The problem: PROBLEM_USER_NOT_AUTHENTICATED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "403", description = "The problem: PROBLEM_USER_NOT_AUTHORIZED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "404", description = "The problem: PROBLEM_BOOK_SCANNER_NOT_FOUND", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "500", description = "The problem: PROBLEM", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class)))
    	}
    )
	@Path("{bookScannerId}/stop")
	@POST
	public Response stopBookScanner(
			@Parameter(name = "bookScannerId", description = "The id of the bookScanner.", required = true) @PathParam("bookScannerId") String bookScannerId) throws ProblemException {
		BookScannerService bookScannerService = bookScannerServiceProvider.named(bookScannerId).get();
		
		if(bookScannerService == null) {
			throw new ProblemException(new Problem(404, "PROBLEM_BOOK_SCANNER_NOT_FOUND", "The bookScanner is not found."));
		}
		
		if(bookScannerService.getStatus().equals(BookScannerServiceStatus.STARTED)) {
			bookScannerService.stop();
			
			ResponseBuilder responseBuilder = Response.status(200);
			
			return responseBuilder.build();
		} else {
			throw new ProblemException(new Problem(400, "PROBLEM_BOOK_SCANNER_STATUS_INVALID", "The bookScanner.status is invalid: " + bookScannerService.getStatus() + "."));
		}
	}
}
