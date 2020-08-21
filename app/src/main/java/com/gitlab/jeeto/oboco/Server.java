package com.gitlab.jeeto.oboco;

import static org.eclipse.jetty.servlet.ServletContextHandler.SESSIONS;

import java.io.File;

import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gitlab.jeeto.oboco.api.ApiApplication;
import com.gitlab.jeeto.oboco.common.configuration.Configuration;
import com.gitlab.jeeto.oboco.common.configuration.ConfigurationManager;
import com.gitlab.jeeto.oboco.opds.OpdsApplication;
import com.gitlab.jeeto.oboco.plugin.PluginManager;

public class Server {
	private static Logger logger;
	private PluginManager pluginManager;
	private org.eclipse.jetty.server.Server server;
	private Configuration configuration;
	
	private Configuration getConfiguration() {
		if(configuration == null) {
			ConfigurationManager configurationManager = ConfigurationManager.getInstance();
			configuration = configurationManager.getConfiguration();
		}
		return configuration;
	}
	
	public static void main(String[] args) throws Exception {
		LoggerConfigurationFactory loggerConfigurationFactory = new LoggerConfigurationFactory();
		
	    ConfigurationFactory.setConfigurationFactory(loggerConfigurationFactory);
	    
		logger = LoggerFactory.getLogger(Server.class.getName());
		
    	Server server = new Server();
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
	        public void run() {
	        	try {
					server.stop();
				} catch(Exception e) {
					logger.error("Error.", e);
				}
	        }
	    });
        
        try {
    		server.start();
    		server.join();
    	} catch(Exception e) {
			logger.error("Error.", e);
		}
    }
	
	public Server() {
		super();
	}
	
	public void start() throws Exception {
		logger.info("start server");
		
		if(server == null) {
			pluginManager = PluginManager.getInstance();
			pluginManager.start();
			
			server = new org.eclipse.jetty.server.Server();
			
			Integer port = getConfiguration().getAsInteger("application.server.port", "8080");
			
			logger.info("start server: http://127.0.0.1:" + port);
			
	    	Integer sslPort = getConfiguration().getAsInteger("application.server.ssl.port", null);
	    	String sslKeyStorePath = getConfiguration().getAsString("application.server.ssl.keyStore.path", null);
	    	String sslKeyStorePassword = getConfiguration().getAsString("application.server.ssl.keyStore.password", null);
			
			HttpConfiguration httpConfiguration = new HttpConfiguration();
			if(sslPort != null) {
				logger.info("start server: https://127.0.0.1:" + sslPort);
				
				httpConfiguration.setSecureScheme("https");
				httpConfiguration.setSecurePort(sslPort);
			}
			
			HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpConfiguration);
			
			ServerConnector httpConnector = new ServerConnector(server, httpConnectionFactory);
			httpConnector.setPort(port);
			
			server.addConnector(httpConnector);
			
			if(sslPort != null) {
				SslContextFactory sslContextFactory = new SslContextFactory.Server();
				sslContextFactory.setKeyStorePath(sslKeyStorePath);
				sslContextFactory.setKeyStorePassword(sslKeyStorePassword);
		        
		        HttpConfiguration httpsConfiguration = new HttpConfiguration(httpConfiguration);
				httpsConfiguration.addCustomizer(new SecureRequestCustomizer());
				
				SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString());
				
				HttpConnectionFactory httpsConnectionFactory = new HttpConnectionFactory(httpsConfiguration);
				
				ServerConnector httpsConnector = new ServerConnector(server, sslConnectionFactory, httpsConnectionFactory);
				httpsConnector.setPort(sslPort);
				
				server.addConnector(httpsConnector);
			}
			
			ContextHandlerCollection contextHandlerCollection = new ContextHandlerCollection();
			
	        ServletContextHandler servletContextHandler = new ServletContextHandler(SESSIONS);
	        servletContextHandler.setContextPath("/");
	        servletContextHandler.addServlet(ServerServlet.class, "/*");
	        
	        ServletContainer apiServletContainer = new ServletContainer(new ApiApplication());
	        ServletHolder apiServletHolder = new ServletHolder(apiServletContainer);
	        servletContextHandler.addServlet(apiServletHolder, "/api/*");
	        
	        WebAppContext apiWebAppContext = new WebAppContext(Server.class.getClassLoader().getResource("api-web").toExternalForm(), "/api-web");
	        
	        contextHandlerCollection.addHandler(apiWebAppContext);
	        
	        ServletContainer opdsServletContainer = new ServletContainer(new OpdsApplication());
	        ServletHolder opdsServletHolder = new ServletHolder(opdsServletContainer);
	        servletContextHandler.addServlet(opdsServletHolder, "/opds/*");
	        
	        contextHandlerCollection.addHandler(servletContextHandler);
	        
	        File directory = new File("./web/");
	        for(File file: directory.listFiles()) {
	        	if(file.getPath().endsWith(".war")) {
	        		WebAppContext webAppContext = new WebAppContext(file.getPath(), "/web");
	    	        
	    	        contextHandlerCollection.addHandler(webAppContext);
	        	}
	        }
	        
	        server.setHandler(contextHandlerCollection);
	        server.start();
		}
	}
	
	public void join() throws Exception {
		server.join();
	}
	
	public void stop() throws Exception {
		logger.info("stop server");
		
		if(server != null && (server.isStarted() || server.isStarting())) {
            server.stop();
            server = null;
            
            pluginManager.stop();
            pluginManager = null;
		}
	}
}
