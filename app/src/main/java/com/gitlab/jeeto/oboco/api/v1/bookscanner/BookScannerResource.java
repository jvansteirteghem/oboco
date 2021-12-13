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

import com.gitlab.jeeto.oboco.data.bookscanner.BookScanner;
import com.gitlab.jeeto.oboco.data.bookscanner.BookScannerMode;
import com.gitlab.jeeto.oboco.data.bookscanner.BookScannerStatus;
import com.gitlab.jeeto.oboco.database.Graph;
import com.gitlab.jeeto.oboco.database.GraphHelper;
import com.gitlab.jeeto.oboco.problem.Problem;
import com.gitlab.jeeto.oboco.problem.ProblemDto;
import com.gitlab.jeeto.oboco.problem.ProblemException;
import com.gitlab.jeeto.oboco.server.authentication.Authentication;
import com.gitlab.jeeto.oboco.server.authorization.Authorization;

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
	private IterableProvider<BookScanner> bookScannerProvider;
	
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
		
		for(BookScanner bookScanner: bookScannerProvider) {
			BookScannerDto bookScannerDto = new BookScannerDto();
			bookScannerDto.setId(bookScanner.getId());
			if(bookScanner.getMode() != null) {
				bookScannerDto.setMode(bookScanner.getMode().toString());
			}
			if(bookScanner.getStatus() != null) {
				bookScannerDto.setStatus(bookScanner.getStatus().toString());
			}
			
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
	@Path("{bookScannerId: [a-zA-Z0-9\\_\\-]+}")
	@GET
	public Response getBookScanner(
			@Parameter(name = "bookScannerId", description = "The id of the bookScanner.", required = true) @PathParam("bookScannerId") String bookScannerId,
			@Parameter(name = "graph", description = "The graph. The full graph is ().", required = false) @DefaultValue("()") @QueryParam("graph") String graphValue) throws ProblemException {
		Graph graph = GraphHelper.createGraph(graphValue);
		Graph fullGraph = GraphHelper.createGraph("()");
		
		GraphHelper.validateGraph(graph, fullGraph);
		
		BookScanner bookScanner = bookScannerProvider.named(bookScannerId).get();
		
		if(bookScanner == null) {
			throw new ProblemException(new Problem(404, "PROBLEM_BOOK_SCANNER_NOT_FOUND", "The bookScanner is not found."));
		}
				
		BookScannerDto bookScannerDto = new BookScannerDto();
		bookScannerDto.setId(bookScanner.getId());
		if(bookScanner.getMode() != null) {
			bookScannerDto.setMode(bookScanner.getMode().toString());
		}
		if(bookScanner.getStatus() != null) {
			bookScannerDto.setStatus(bookScanner.getStatus().toString());
		}
		
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
	@Path("{bookScannerId: [a-zA-Z0-9\\_\\-]+}/start")
	@POST
	public void startBookScanner(
			@Parameter(name = "bookScannerId", description = "The id of the bookScanner.", required = true) @PathParam("bookScannerId") String bookScannerId, 
			@Parameter(name = "mode", description = "The mode.", required = true) @QueryParam("mode") BookScannerMode mode, 
			@Suspended AsyncResponse asyncResponse) {
		try {
			for(BookScanner bookScanner: bookScannerProvider) {
    			if(BookScannerStatus.STOPPED.equals(bookScanner.getStatus()) == false) {
    				throw new ProblemException(new Problem(400, "PROBLEM_BOOK_SCANNER_STATUS_INVALID", "The bookScanner.status is invalid: " + bookScanner.getStatus() + "."));
    			}
    		}
			
			BookScanner bookScanner = bookScannerProvider.named(bookScannerId).get();
			
			if(bookScanner == null) {
				throw new ProblemException(new Problem(404, "PROBLEM_BOOK_SCANNER_NOT_FOUND", "The bookScanner is not found."));
			}
			
			bookScanner.start(mode);
			
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
	@Path("{bookScannerId: [a-zA-Z0-9\\_\\-]+}/stop")
	@POST
	public Response stopBookScanner(
			@Parameter(name = "bookScannerId", description = "The id of the bookScanner.", required = true) @PathParam("bookScannerId") String bookScannerId) throws ProblemException {
		BookScanner bookScanner = bookScannerProvider.named(bookScannerId).get();
		
		if(bookScanner == null) {
			throw new ProblemException(new Problem(404, "PROBLEM_BOOK_SCANNER_NOT_FOUND", "The bookScanner is not found."));
		}
		
		if(BookScannerStatus.STARTED.equals(bookScanner.getStatus())) {
			bookScanner.stop();
			
			ResponseBuilder responseBuilder = Response.status(200);
			
			return responseBuilder.build();
		} else {
			throw new ProblemException(new Problem(400, "PROBLEM_BOOK_SCANNER_STATUS_INVALID", "The bookScanner.status is invalid: " + bookScanner.getStatus() + "."));
		}
	}
}
