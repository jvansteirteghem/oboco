package com.gitlab.jeeto.oboco.plugin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginManager {
	private static Logger logger = LoggerFactory.getLogger(PluginManager.class.getName());
	private static PluginManager instance;
	private org.pf4j.PluginManager pluginManager;
	private Map<Class<? extends FactoryBase>, FactoryBase> mapFactory;
	
	public static PluginManager getInstance() {
		if(instance == null) {
			synchronized(PluginManager.class) {
				if(instance == null) {
					instance = new PluginManager();
				}
			}
		}
		return instance;
	}
	
	private PluginManager() {
		super();
		pluginManager = new org.pf4j.DefaultPluginManager();
		pluginManager.loadPlugins();
	}
	
	public synchronized void start() {
		logger.info("start pluginManager");
		
		pluginManager.startPlugins();
		
		mapFactory = new HashMap<Class<? extends FactoryBase>, FactoryBase>();
	}
	
	public synchronized void stop() {
		logger.info("stop pluginManager");
		
		Iterator<Map.Entry<Class<? extends FactoryBase>, FactoryBase>> iterator = mapFactory.entrySet().iterator();
		while(iterator.hasNext()) {
		    Map.Entry<Class<? extends FactoryBase>, FactoryBase> nextMapEntry = iterator.next();
		    FactoryBase factoryBase = nextMapEntry.getValue();
		    factoryBase.stop();
		}
		
		pluginManager.stopPlugins();
	}
	
	@SuppressWarnings("unchecked")
	public synchronized <T extends FactoryBase> T getFactory(Class<T> factoryClass) throws Exception {
		T factory = (T) mapFactory.get(factoryClass);
		
		if(factory == null) {
			factory = factoryClass.newInstance();
			factory.setPluginManager(pluginManager);
			factory.start();
			
			mapFactory.put(factoryClass, factory);
		}
		
		return factory;
	}
}
