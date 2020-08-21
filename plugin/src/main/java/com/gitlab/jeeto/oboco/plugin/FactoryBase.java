package com.gitlab.jeeto.oboco.plugin;

import java.util.List;

import org.pf4j.PluginManager;

public abstract class FactoryBase {
	private PluginManager pluginManager;
	
	public PluginManager getPluginManager() {
		return pluginManager;
	}

	public void setPluginManager(PluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}

	public FactoryBase() {
		super();
	}
	
	public <T> T getExtension(Class<T> extensionClass) throws Exception {
		List<Class<? extends T>> listExtensionClass = getPluginManager().getExtensionClasses(extensionClass);
		
		T extension = null;
		
		if(listExtensionClass.size() != 0) {
			extension = getPluginManager().getExtensionFactory().create(listExtensionClass.get(0));
		}
		
		if(extension == null) {
			throw new Exception("extension not found.");
		}
		
		return extension;
	}
	
	public abstract void start();
	public abstract void stop();
}
