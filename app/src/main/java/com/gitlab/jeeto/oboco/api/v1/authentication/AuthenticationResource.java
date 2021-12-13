package com.gitlab.jeeto.oboco.api.v1.authentication;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.gitlab.jeeto.oboco.database.user.User;
import com.gitlab.jeeto.oboco.database.user.UserService;
import com.gitlab.jeeto.oboco.problem.Problem;
import com.gitlab.jeeto.oboco.problem.ProblemDto;
import com.gitlab.jeeto.oboco.problem.ProblemException;
import com.gitlab.jeeto.oboco.server.authentication.UserToken;
import com.gitlab.jeeto.oboco.server.authentication.UserTokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Produces(MediaType.APPLICATION_JSON)
public class AuthenticationResource {
	@Inject
	private UserService userService;
	@Inject
	private UserTokenService userTokenService;
	
	@Operation(
		description = "Create a userId by userNamePassword.",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "A userId.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserIdDto.class))),
    		@ApiResponse(responseCode = "400", description = "A problem: PROBLEM_USER_NAME_PASSWORD_INVALID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "500", description = "A problem: PROBLEM", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class)))
    	}
    )
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    public Response createUserIdByUserNamePassword(
    		@Parameter(name = "userNamePassword", description = "A userNamePassword.", required = true) UserNamePasswordDto userNamePasswordDto) throws ProblemException {
		User user = userService.getUserByNameAndPassword(userNamePasswordDto.getName(), userNamePasswordDto.getPassword());
		
		if(user == null) {
			throw new ProblemException(new Problem(400, "PROBLEM_USER_NAME_PASSWORD_INVALID", "The user.name or user.password is invalid."));
		}
		
		String accessTokenValue = userTokenService.getAccessTokenValue(user.getName());
		
		String refreshTokenValue = userTokenService.getRefreshTokenValue(user.getName());
        
		UserIdDto userIdDto = new UserIdDto();
		userIdDto.setName(user.getName());
		userIdDto.setRoles(user.getRoles());
		userIdDto.setAccessToken(accessTokenValue);
		userIdDto.setRefreshToken(refreshTokenValue);
        
        ResponseBuilder responseBuilder = Response.status(200);
		responseBuilder.entity(userIdDto);
		
		return responseBuilder.build();
    }
	
	@Operation(
		description = "create a userId by userToken.",
    	responses = {
    		@ApiResponse(responseCode = "200", description = "A userId.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserIdDto.class))),
    		@ApiResponse(responseCode = "400", description = "A problem: PROBLEM_USER_TOKEN_INVALID", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class))),
    		@ApiResponse(responseCode = "500", description = "A problem: PROBLEM", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDto.class)))
    	}
    )
	@POST
    @Path("refresh")
	@Consumes(MediaType.APPLICATION_JSON)
    public Response createUserIdByUserToken(
    		@Parameter(name = "userToken", description = "A userToken.", required = true) UserTokenDto userTokenDto) throws ProblemException {
		String refreshTokenValue = userTokenDto.getToken();
		
		UserToken refreshToken = userTokenService.getRefreshToken(refreshTokenValue);
		
		User user = userService.getUserByName(refreshToken.getName());
		
		if(user == null) {
			throw new ProblemException(new Problem(400, "PROBLEM_USER_TOKEN_INVALID", "The userToken is invalid."));
		}
		
		if(user.getUpdateDate() == null || user.getUpdateDate().compareTo(refreshToken.getStartDate()) > 0) {
			throw new ProblemException(new Problem(400, "PROBLEM_USER_TOKEN_INVALID", "The userToken is invalid."));
		}
		
        String accessTokenValue = userTokenService.getAccessTokenValue(user.getName());
        
        UserIdDto userIdDto = new UserIdDto();
		userIdDto.setName(user.getName());
		userIdDto.setRoles(user.getRoles());
		userIdDto.setAccessToken(accessTokenValue);
		userIdDto.setRefreshToken(refreshTokenValue);
        
        ResponseBuilder responseBuilder = Response.status(200);
		responseBuilder.entity(userIdDto);
		
		return responseBuilder.build();
    }
}
