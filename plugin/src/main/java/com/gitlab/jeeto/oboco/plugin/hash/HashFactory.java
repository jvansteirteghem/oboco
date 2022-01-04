package com.gitlab.jeeto.oboco.plugin.hash;

import com.gitlab.jeeto.oboco.plugin.FactoryBase;
import com.gitlab.jeeto.oboco.plugin.hash.Hash.Sha256Hash;

public class HashFactory extends FactoryBase {
	public HashFactory() {
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
	
	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}
}
