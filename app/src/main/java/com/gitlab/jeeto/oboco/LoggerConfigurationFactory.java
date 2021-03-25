package com.gitlab.jeeto.oboco;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.LoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import com.gitlab.jeeto.oboco.common.configuration.ConfigurationManager;

public class LoggerConfigurationFactory extends ConfigurationFactory {
	private com.gitlab.jeeto.oboco.common.configuration.Configuration configuration;
	
	private com.gitlab.jeeto.oboco.common.configuration.Configuration getConfiguration() {
		if(configuration == null) {
			ConfigurationManager configurationManager = ConfigurationManager.getInstance();
			configuration = configurationManager.getConfiguration();
		}
		return configuration;
	}

    private Configuration createConfiguration(final String name, ConfigurationBuilder<BuiltConfiguration> builder) {
    	builder.setStatusLevel(Level.WARN);
		
		// rollingFileAppender
		
		String rollingFilePath = getConfiguration().getAsString("logger.path", "./logs");
		
		AppenderComponentBuilder rollingFileAppenderComponentBuilder = builder.newAppender("rollingFile", "RollingFile")
				.addAttribute("fileName", new File(rollingFilePath, "application.log").getPath())
				.addAttribute("filePattern", new File(rollingFilePath, "application-%i.log.gz").getPath())
				.add(builder.newLayout("PatternLayout")
						.addAttribute("pattern", "%d{DEFAULT} %-5level %logger{36} - %msg%n"))
				.addComponent(builder.newComponent("Policies")
						.addComponent(builder.newComponent("SizeBasedTriggeringPolicy")
								.addAttribute("size", "10M")))
				.addComponent(builder.newComponent("DefaultRolloverStrategy")
						.addAttribute("max", 10));
		
		builder.add(rollingFileAppenderComponentBuilder);
		
		// consoleAppender
		
		AppenderComponentBuilder consoleAppenderComponentBuilder = builder.newAppender("console", "Console")
				.addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT)
				.add(builder.newLayout("PatternLayout")
						.addAttribute("pattern", "%d{DEFAULT} %-5level %logger{36} - %msg%n"));
        
        builder.add(consoleAppenderComponentBuilder);
        
        // rootLogger
        
        Map<String, Level> levelMap = new HashMap<String, Level>();
    	levelMap.put("FATAL", Level.FATAL);
    	levelMap.put("ERROR", Level.ERROR);
    	levelMap.put("WARN", Level.WARN);
    	levelMap.put("INFO", Level.INFO);
    	levelMap.put("DEBUG", Level.DEBUG);
    	levelMap.put("TRACE", Level.TRACE);
        
    	String rootLoggerLevelName = getConfiguration().getAsString("logger.rootLevel", "ERROR");
    	
    	Level rootLoggerLevel = levelMap.get(rootLoggerLevelName);
        
        RootLoggerComponentBuilder rootLoggerComponentBuilder = builder.newRootLogger(rootLoggerLevel)
        		.add(builder.newAppenderRef("rollingFile"))
        		.add(builder.newAppenderRef("console"));
        
        builder.add(rootLoggerComponentBuilder);
        
        // logger
        
        String loggerLevelName = getConfiguration().getAsString("logger.level", "INFO");
    	
    	Level loggerLevel = levelMap.get(loggerLevelName);
        
        LoggerComponentBuilder loggerComponentBuilder = builder.newLogger("com.gitlab.jeeto.oboco", loggerLevel)
        		.addAttribute("additivity", true);
        
        builder.add(loggerComponentBuilder);
        
        /*
        try {
        	builder.writeXmlConfiguration(System.out);
        } catch(Exception e) {
        	
        }
        */
        
        return builder.build();
    }

    @Override
    public Configuration getConfiguration(final LoggerContext loggerContext, final ConfigurationSource source) {
        return getConfiguration(loggerContext, source.toString(), null);
    }

    @Override
    public Configuration getConfiguration(final LoggerContext loggerContext, final String name, final URI configLocation) {
        ConfigurationBuilder<BuiltConfiguration> builder = newConfigurationBuilder();
        return createConfiguration(name, builder);
    }

    @Override
    protected String[] getSupportedTypes() {
        return new String[] {"*"};
    }
}
