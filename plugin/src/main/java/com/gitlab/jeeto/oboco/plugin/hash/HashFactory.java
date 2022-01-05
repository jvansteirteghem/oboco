package com.gitlab.jeeto.oboco.plugin.hash;

import com.gitlab.jeeto.oboco.plugin.Factory;
import com.gitlab.jeeto.oboco.plugin.FactoryManager;
import com.gitlab.jeeto.oboco.plugin.hash.Hash.Sha256Hash;

public class HashFactory extends Factory {
	private static HashFactory instance;
	
	public static HashFactory getInstance() {
		if(instance == null) {
			synchronized(HashFactory.class) {
				if(instance == null) {
					instance = new HashFactory();
					
					FactoryManager factoryManager = FactoryManager.getInstance();
					factoryManager.addFactory(instance);
				}
			}
		}
		return instance;
	}
	
	private HashFactory() {
		super();
	}
	
	public Hash getHash(HashType hashType) throws Exception {
		Hash hash = null;
		
		if(HashType.SHA256.equals(hashType)) {
			hash = getExtension(Sha256Hash.class);
		} else {
			throw new Exception("hashType not supported.");
		}
		
		return hash;
	}
}
