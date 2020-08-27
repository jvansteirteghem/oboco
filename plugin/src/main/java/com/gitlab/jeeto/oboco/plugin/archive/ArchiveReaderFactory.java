package com.gitlab.jeeto.oboco.plugin.archive;

import com.gitlab.jeeto.oboco.common.configuration.Configuration;
import com.gitlab.jeeto.oboco.common.configuration.ConfigurationManager;
import com.gitlab.jeeto.oboco.plugin.FactoryBase;
import com.gitlab.jeeto.oboco.plugin.FileType;

public class ArchiveReaderFactory extends FactoryBase {
	private Configuration configuration;
	
	private Configuration getConfiguration() {
		if(configuration == null) {
			ConfigurationManager configurationManager = ConfigurationManager.getInstance();
			configuration = configurationManager.getConfiguration();
		}
		return configuration;
	}
	
	private ArchiveReaderPool archiveReaderPool;
	
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
		}
		
		if(archiveReader != null) {
			archiveReader = new ArchiveReaderPoolDelegator(archiveReaderPool, archiveReader);
		}
		
        return archiveReader;
	}

	@Override
	public void start() {
		Integer size = getConfiguration().getAsInteger("application.plugin.archive.archiveReaderPool.size", "25");
		Long interval = getConfiguration().getAsLong("application.plugin.archive.archiveReaderPool.interval", "60") * 1000L;
		Long age = getConfiguration().getAsLong("application.plugin.archive.archiveReaderPool.age", "600") * 1000L;
		
		archiveReaderPool = new ArchiveReaderPool(size, interval, age);
		archiveReaderPool.start();
	}

	@Override
	public void stop() {
		archiveReaderPool.stop();
	}
}
