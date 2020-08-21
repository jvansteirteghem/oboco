package com.gitlab.jeeto.oboco.opds;

import org.glassfish.jersey.server.ResourceConfig;

public class OpdsApplication extends ResourceConfig {
	public OpdsApplication() {
		packages("com.gitlab.jeeto.oboco.opds");
		packages("com.gitlab.jeeto.oboco.common");
		register(new OpdsApplicationBinder());
	}
}
