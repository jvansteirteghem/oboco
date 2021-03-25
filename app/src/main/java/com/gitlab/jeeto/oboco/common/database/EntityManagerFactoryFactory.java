package com.gitlab.jeeto.oboco.common.database;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Persistence;

import org.glassfish.hk2.api.Factory;

import com.gitlab.jeeto.oboco.common.configuration.Configuration;
import com.gitlab.jeeto.oboco.common.configuration.ConfigurationManager;

public class EntityManagerFactoryFactory implements Factory<javax.persistence.EntityManagerFactory> {
	private Configuration configuration;
	
	private Configuration getConfiguration() {
		if(configuration == null) {
			ConfigurationManager configurationManager = ConfigurationManager.getInstance();
			configuration = configurationManager.getConfiguration();
		}
		return configuration;
	}
	
	@Override
	public javax.persistence.EntityManagerFactory provide() {
		Map<String, String> propertyMap = new HashMap<String, String>();
		
		String dialectName = getConfiguration().getAsString("database.name", "H2");
		
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
		
		String driver = getConfiguration().getAsString("database.driver", "org.h2.Driver");
		String url = getConfiguration().getAsString("database.url", "jdbc:h2:file:./application");
		String userName = getConfiguration().getAsString("database.user.name", "");
		String userPassword = getConfiguration().getAsString("database.user.password", "");
		
		propertyMap.put("javax.persistence.jdbc.driver", driver);
		propertyMap.put("javax.persistence.jdbc.url", url);
		propertyMap.put("javax.persistence.jdbc.user", userName);
		propertyMap.put("javax.persistence.jdbc.password", userPassword);
        
        String size = getConfiguration().getAsString("database.connectionPool.size", "50");
		String age = getConfiguration().getAsString("database.connectionPool.age", "1800");
		
        propertyMap.put("hibernate.c3p0.min_size", "5");
        propertyMap.put("hibernate.c3p0.max_size", size);
        propertyMap.put("hibernate.c3p0.acquire_increment", "5");
        propertyMap.put("hibernate.c3p0.timeout", age);
        
        javax.persistence.EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default", propertyMap);
		
		return entityManagerFactory;
	}
	
	@Override
	public void dispose(javax.persistence.EntityManagerFactory entityManagerFactory) {
		if(entityManagerFactory.isOpen()) {
			entityManagerFactory.close();
        }
	}
}
