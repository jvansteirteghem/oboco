package com.gitlab.jeeto.oboco.plugin;

import java.util.ArrayList;
import java.util.List;

import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FactoryManager {
	private static Logger logger = LoggerFactory.getLogger(FactoryManager.class.getName());
	private static FactoryManager instance;
	private List<Factory> factoryList;
	private PluginManager pluginManager;
	
	public static FactoryManager getInstance() {
		if(instance == null) {
			synchronized(FactoryManager.class) {
				if(instance == null) {
					instance = new FactoryManager();
				}
			}
		}
		return instance;
	}
	
	private FactoryManager() {
		super();
		
		factoryList = new ArrayList<Factory>();
		
		pluginManager = new DefaultPluginManager();
		pluginManager.loadPlugins();
	}
	
	public synchronized void addFactory(Factory factory) {
		factory.setPluginManager(pluginManager);
		factory.start();
		
		factoryList.add(factory);
	}
	
	public synchronized void start() {
		logger.info("start factoryManager");
		
		pluginManager.startPlugins();
		
		for(Factory factory: factoryList) {
			factory.start();
		}
	}
	
	public synchronized void stop() {
		logger.info("stop factoryManager");
		
		for(Factory factory: factoryList) {
			factory.stop();
		}
		
		pluginManager.stopPlugins();
	}
}
