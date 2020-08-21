package com.gitlab.jeeto.oboco.api;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

import com.gitlab.jeeto.oboco.common.configuration.Configuration;
import com.gitlab.jeeto.oboco.common.configuration.ConfigurationManager;

public class ApiApplicationEventListener implements ApplicationEventListener {
	private Configuration configuration;
	
	private Configuration getConfiguration() {
		if(configuration == null) {
			ConfigurationManager configurationManager = ConfigurationManager.getInstance();
			configuration = configurationManager.getConfiguration();
		}
		return configuration;
	}
	
	@Override
	public void onEvent(ApplicationEvent event) {
		if(event.getType().equals(ApplicationEvent.Type.INITIALIZATION_FINISHED)) {
			File ddlFile = new File("database.ddl");
			
			if(ddlFile.isFile()) {
				Map<String, String> propertyMap = new HashMap<String, String>();
				
				String dialectName = getConfiguration().getAsString("application.database.name", "H2");
				
				Map<String, String> dialectMap = new HashMap<String, String>();
				dialectMap.put("DB2", "org.hibernate.dialect.DB2Dialect");
				dialectMap.put("DB2400", "org.hibernate.dialect.DB2400Dialect");
				dialectMap.put("DB2390", "org.hibernate.dialect.DB2390Dialect");
				dialectMap.put("PostgreSQL", "org.hibernate.dialect.PostgreSQLDialect");
				dialectMap.put("MySQL5", "org.hibernate.dialect.MySQL5Dialect");
				dialectMap.put("MySQL5InnoDB", "org.hibernate.dialect.MySQL5InnoDBDialect");
				dialectMap.put("MySQLMyISAM", "org.hibernate.dialect.MySQLMyISAMDialect");
				dialectMap.put("Oracle", "org.hibernate.dialect.OracleDialect");
				dialectMap.put("Oracle9i", "org.hibernate.dialect.Oracle9iDialect");
				dialectMap.put("Oracle10g", "org.hibernate.dialect.Oracle10gDialect");
				dialectMap.put("Oracle11g", "org.hibernate.dialect.Oracle10gDialect");
				dialectMap.put("SybaseASE15", "org.hibernate.dialect.SybaseASE15Dialect");
				dialectMap.put("SybaseAnywhere", "org.hibernate.dialect.SybaseAnywhereDialect");
				dialectMap.put("SQLServer", "org.hibernate.dialect.SQLServerDialect");
				dialectMap.put("SQLServer2005", "org.hibernate.dialect.SQLServer2005Dialect");
				dialectMap.put("SQLServer2008", "org.hibernate.dialect.SQLServer2008Dialect");
				dialectMap.put("SAPDB", "org.hibernate.dialect.SAPDBDialect");
				dialectMap.put("Informix", "org.hibernate.dialect.InformixDialect");
				dialectMap.put("HSQL", "org.hibernate.dialect.HSQLDialect");
				dialectMap.put("H2", "org.hibernate.dialect.H2Dialect");
				dialectMap.put("Ingres", "org.hibernate.dialect.IngresDialect");
				dialectMap.put("Progress", "org.hibernate.dialect.ProgressDialect");
				dialectMap.put("Mckoi", "org.hibernate.dialect.MckoiDialect");
				dialectMap.put("Interbase", "org.hibernate.dialect.InterbaseDialect");
				dialectMap.put("Pointbase", "org.hibernate.dialect.PointbaseDialect");
				dialectMap.put("Frontbase", "org.hibernate.dialect.FrontbaseDialect");
				dialectMap.put("Firebird", "org.hibernate.dialect.FirebirdDialect");
				
				String dialect = dialectMap.get(dialectName);
				
				propertyMap.put("hibernate.dialect", dialect);
				
				String driver = getConfiguration().getAsString("application.database.driver", "org.h2.Driver");
				String url = getConfiguration().getAsString("application.database.url", "jdbc:h2:file:./database");
				String userName = getConfiguration().getAsString("application.database.user.name", "");
				String userPassword = getConfiguration().getAsString("application.database.user.password", "");
				
				propertyMap.put("javax.persistence.jdbc.driver", driver);
				propertyMap.put("javax.persistence.jdbc.url", url);
				propertyMap.put("javax.persistence.jdbc.user", userName);
				propertyMap.put("javax.persistence.jdbc.password", userPassword);
				propertyMap.put("javax.persistence.schema-generation.database.action", "create");
				propertyMap.put("javax.persistence.schema-generation.create-source", "script");
				propertyMap.put("javax.persistence.schema-generation.create-script-source", ddlFile.getPath());
				
				File sqlFile = new File("database.sql");
				
				if(sqlFile.isFile()) {
					propertyMap.put("javax.persistence.sql-load-script-source", sqlFile.getPath());
				}
				
				EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default", propertyMap);
		        
				EntityManager entityManager = entityManagerFactory.createEntityManager();
				entityManager.close();
				
				if(sqlFile.isFile()) {
					sqlFile.delete();
				}
				
				ddlFile.delete();
			}
	    }
	}

	@Override
	public RequestEventListener onRequest(RequestEvent event) {
		return null;
	}
}
