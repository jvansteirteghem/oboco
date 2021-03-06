package com.gitlab.jeeto.oboco.common.security.authorization;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.Provider;

import com.gitlab.jeeto.oboco.common.exception.ProblemDto;

@Authorization
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationRequestFilter implements ContainerRequestFilter {
    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
    	Authorization authorization = null;
    	
    	authorization = resourceInfo.getResourceMethod().getAnnotation(Authorization.class);
        if (authorization != null) {
            doAuthorization(authorization, requestContext);
            return;
        }
        
        authorization = resourceInfo.getResourceClass().getAnnotation(Authorization.class);
        if (authorization != null) {
            doAuthorization(authorization, requestContext);
            return;
        }
    }

    private void doAuthorization(Authorization authorization, ContainerRequestContext requestContext) {
    	SecurityContext securityContext = requestContext.getSecurityContext();
    	
    	if(securityContext.getUserPrincipal() == null) {
    		ResponseBuilder responseBuilder = Response.status(401);
    		responseBuilder.entity(new ProblemDto(401, "PROBLEM_USER_NOT_AUTHENTICATED", "The user is not authenticated."));
        	
    		requestContext.abortWith(responseBuilder.build());
        	
        	return;
    	}
    	
        for(String role: authorization.roles()) {
            if(securityContext.isUserInRole(role)) {
                return;
            }
        }
        
        ResponseBuilder responseBuilder = Response.status(403);
        responseBuilder.entity(new ProblemDto(403, "PROBLEM_USER_NOT_AUTHORIZED", "The user is not authorized."));
    	
        requestContext.abortWith(responseBuilder.build());
    	
    	return;
    }
}