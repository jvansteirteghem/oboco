package com.gitlab.jeeto.oboco.plugin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FactoryManager {
	private static Logger logger = LoggerFactory.getLogger(FactoryManager.class.getName());
	private static FactoryManager instance;
	private org.pf4j.PluginManager pluginManager;
	private Map<Class<? extends FactoryBase>, FactoryBase> factoryMap;
	
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
		pluginManager = new org.pf4j.DefaultPluginManager();
		pluginManager.loadPlugins();
	}
	
	public synchronized void start() {
		logger.info("start factoryManager");
		
		pluginManager.startPlugins();
		
		factoryMap = new HashMap<Class<? extends FactoryBase>, FactoryBase>();
	}
	
	public synchronized void stop() {
		logger.info("stop factoryManager");
		
		Iterator<Map.Entry<Class<? extends FactoryBase>, FactoryBase>> iterator = factoryMap.entrySet().iterator();
		while(iterator.hasNext()) {
		    Map.Entry<Class<? extends FactoryBase>, FactoryBase> nextMapEntry = iterator.next();
		    FactoryBase factoryBase = nextMapEntry.getValue();
		    factoryBase.stop();
		}
		
		pluginManager.stopPlugins();
	}
	
	@SuppressWarnings("unchecked")
	public synchronized <T extends FactoryBase> T getFactory(Class<T> factoryClass) throws Exception {
		T factory = (T) factoryMap.get(factoryClass);
		
		if(factory == null) {
			factory = factoryClass.newInstance();
			factory.setPluginManager(pluginManager);
			factory.start();
			
			factoryMap.put(factoryClass, factory);
		}
		
		return factory;
	}
}
