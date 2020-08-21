package com.gitlab.jeeto.oboco.plugin.hash;

import java.io.File;

import org.pf4j.ExtensionPoint;

import com.gitlab.jeeto.oboco.plugin.FileWrapper;

public interface HashManager extends ExtensionPoint {
	public String createHash(FileWrapper<File> inputFileWrapper, HashType outputHashType) throws Exception;
	public static interface Sha256HashManager extends HashManager {};
}
