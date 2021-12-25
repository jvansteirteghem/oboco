package com.gitlab.jeeto.oboco.plugin.archive;

import com.gitlab.jeeto.oboco.common.FileType;
import com.gitlab.jeeto.oboco.plugin.FactoryBase;

public class ArchiveReaderFactory extends FactoryBase {
	public ArchiveReaderFactory() {
		super();
	}
	
	public ArchiveReader getArchiveReader(FileType inputFileType) throws Exception {
		ArchiveReader archiveReader = null;
		
		if(FileType.ZIP.equals(inputFileType)) {
			archiveReader = getExtension(ArchiveReader.ZipArchiveReader.class);
		} else if(FileType.RAR.equals(inputFileType)) {
			archiveReader = getExtension(ArchiveReader.RarArchiveReader.class);
		} else if(FileType.RAR5.equals(inputFileType)) {
			archiveReader = getExtension(ArchiveReader.Rar5ArchiveReader.class);
		} else if(FileType.SEVENZIP.equals(inputFileType)) {
			archiveReader = getExtension(ArchiveReader.SevenZipArchiveReader.class);
		} else {
			throw new Exception("fileType not supported.");
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
