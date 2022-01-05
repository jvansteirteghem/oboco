package com.gitlab.jeeto.oboco.plugin.archive;

import com.gitlab.jeeto.oboco.plugin.Factory;
import com.gitlab.jeeto.oboco.plugin.FactoryManager;

public class ArchiveIOFactory extends Factory {
	private static ArchiveIOFactory instance;
	
	public static ArchiveIOFactory getInstance() {
		if(instance == null) {
			synchronized(ArchiveIOFactory.class) {
				if(instance == null) {
					instance = new ArchiveIOFactory();
					
					FactoryManager factoryManager = FactoryManager.getInstance();
					factoryManager.addFactory(instance);
				}
			}
		}
		return instance;
	}
	
	private ArchiveIOFactory() {
		super();
	}
	
	public ArchiveReader getArchiveReader(ArchiveType archiveType) throws Exception {
		ArchiveReader archiveReader = null;
		
		if(ArchiveType.ZIP.equals(archiveType)) {
			archiveReader = getExtension(ArchiveReader.ZipArchiveReader.class);
		} else if(ArchiveType.RAR.equals(archiveType)) {
			archiveReader = getExtension(ArchiveReader.RarArchiveReader.class);
		} else if(ArchiveType.RAR5.equals(archiveType)) {
			archiveReader = getExtension(ArchiveReader.Rar5ArchiveReader.class);
		} else if(ArchiveType.SEVENZIP.equals(archiveType)) {
			archiveReader = getExtension(ArchiveReader.SevenZipArchiveReader.class);
		} else {
			throw new Exception("archiveType not supported.");
		}
		
        return archiveReader;
	}
}
