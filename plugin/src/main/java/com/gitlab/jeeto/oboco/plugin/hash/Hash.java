package com.gitlab.jeeto.oboco.plugin.hash;

import java.io.File;

import org.pf4j.ExtensionPoint;

public interface Hash extends ExtensionPoint {
	public String calculate(File inputFile) throws Exception;
	
	public static interface Sha256Hash extends Hash {
		
	}
}
