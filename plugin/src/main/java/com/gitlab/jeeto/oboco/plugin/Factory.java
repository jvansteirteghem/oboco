package com.gitlab.jeeto.oboco.plugin;

import java.util.List;

import org.pf4j.PluginManager;

public abstract class Factory {
	private PluginManager pluginManager;

	public Factory() {
		super();
	}
	
	public PluginManager getPluginManager() {
		return pluginManager;
	}

	public void setPluginManager(PluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}
	
	public <T> T getExtension(Class<T> extensionClass) throws Exception {
		List<Class<? extends T>> extensionClassList = pluginManager.getExtensionClasses(extensionClass);
		
		T extension = null;
		
		if(extensionClassList.size() != 0) {
			extension = pluginManager.getExtensionFactory().create(extensionClassList.get(0));
		}
		
		if(extension == null) {
			throw new Exception("extension not supported.");
		}
		
		return extension;
	}
	
	public void start() {
		
	}
	
	public void stop() {
		
	}
}
