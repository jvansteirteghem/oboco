package com.gitlab.jeeto.oboco.plugin.hash;

import com.gitlab.jeeto.oboco.plugin.FactoryBase;

public class HashManagerFactory extends FactoryBase {
	public HashManagerFactory() {
		super();
	}
	
	public HashManager getHashManager(HashType outputHashType) throws Exception {
		HashManager hashManager = null;
		
		if(HashType.SHA256.equals(outputHashType)) {
			hashManager = getExtension(HashManager.Sha256HashManager.class);
		}
		
		return hashManager;
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}
}
