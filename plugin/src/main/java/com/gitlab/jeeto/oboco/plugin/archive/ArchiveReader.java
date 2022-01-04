package com.gitlab.jeeto.oboco.plugin.archive;

import java.io.File;
import java.util.List;

import org.pf4j.ExtensionPoint;

public interface ArchiveReader extends ExtensionPoint {
	public void openArchive(File inputFile) throws Exception;
	public void closeArchive() throws Exception;
    public void read(ArchiveReaderEntry archiveReaderEntry, File outputFile) throws Exception;
    public List<ArchiveReaderEntry> getArchiveReaderEntries() throws Exception;
    
    public static interface ZipArchiveReader extends ArchiveReader {
    	
    }
    
    public static interface RarArchiveReader extends ArchiveReader {
    	
    }
    
    public static interface Rar5ArchiveReader extends ArchiveReader {
    	
    }
    
    public static interface SevenZipArchiveReader extends ArchiveReader {
    	
    }
}
