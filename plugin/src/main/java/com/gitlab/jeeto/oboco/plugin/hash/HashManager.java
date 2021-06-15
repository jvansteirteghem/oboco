package com.gitlab.jeeto.oboco.plugin.hash;

import org.pf4j.ExtensionPoint;

import com.gitlab.jeeto.oboco.plugin.TypeableFile;

public interface HashManager extends ExtensionPoint {
	public String createHash(TypeableFile inputFile, HashType outputHashType) throws Exception;
	public static interface Sha256HashManager extends HashManager {};
}
