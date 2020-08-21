package com.gitlab.jeeto.oboco.api;

import org.glassfish.jersey.server.ResourceConfig;

public class ApiApplication extends ResourceConfig {
	public ApiApplication() {
		packages("com.gitlab.jeeto.oboco.api");
		packages("com.gitlab.jeeto.oboco.common");
		packages("io.swagger.v3.jaxrs2.integration.resources");
		register(new ApiApplicationBinder());
		register(new ApiApplicationEventListener());
	}
}
