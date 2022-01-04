package com.gitlab.jeeto.oboco.plugin.archive;

import com.gitlab.jeeto.oboco.plugin.FactoryBase;

public class ArchiveIOFactory extends FactoryBase {
	public ArchiveIOFactory() {
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
	
	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}
}
