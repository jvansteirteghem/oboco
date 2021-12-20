package com.gitlab.jeeto.oboco.plugin.archive.sevenzipjbinding;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginRuntimeException;
import org.pf4j.PluginWrapper;

import com.gitlab.jeeto.oboco.plugin.TypeableFile;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReader;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReaderEntry;

import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipNativeInitializationException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

public class SevenZipJBindingArchivePlugin extends Plugin {
	static {
		try {
			SevenZip.initSevenZipFromPlatformJAR();
		} catch(SevenZipNativeInitializationException e) {
			throw new PluginRuntimeException(e);
		}
	}
	
	public SevenZipJBindingArchivePlugin(PluginWrapper wrapper) {
		super(wrapper);
	}
	
	@Extension
	public static class SevenZipJBindingArchiveReader implements ArchiveReader.ZipArchiveReader, ArchiveReader.RarArchiveReader, ArchiveReader.Rar5ArchiveReader, ArchiveReader.SevenZipArchiveReader {
		private Boolean archiveOpen = false;
		private RandomAccessFile archiveInputFile = null;
		private IInArchive archive = null;
		private Map<ArchiveReaderEntry, ISimpleInArchiveItem> archiveEntryMap = null;
		
		@Override
		public void openArchive(TypeableFile inputFile) throws Exception {
			if(archiveOpen) {
				throw new Exception("archive open.");
			}
			
			try {
				archiveInputFile = new RandomAccessFile(inputFile, "r");
				
				archive = SevenZip.openInArchive(null, new RandomAccessFileInStream(archiveInputFile));
				
				ISimpleInArchive s = archive.getSimpleInterface();
				
				archiveEntryMap = new HashMap<ArchiveReaderEntry, ISimpleInArchiveItem>();
				
				for(ISimpleInArchiveItem archiveEntry: s.getArchiveItems()) {
					String name = archiveEntry.getPath();
					
					ArchiveReaderEntry.Type type;
					if(archiveEntry.isFolder()) {
						type = ArchiveReaderEntry.Type.DIRECTORY;
					} else {
						type = ArchiveReaderEntry.Type.FILE;
					}
					
					ArchiveReaderEntry archiveReaderEntry = new ArchiveReaderEntry(name, type);
					
					archiveEntryMap.put(archiveReaderEntry, archiveEntry);
				}
				
				archiveOpen = true;
			} finally {
				if(archiveOpen == false) {
					archiveEntryMap = null;
					
					try {
						if(archive != null) {
							archive.close();
							archive = null;
						}
					} catch(Exception e) {
						// pass
					}
					
					try {
						if(archiveInputFile != null) {
							archiveInputFile.close();
							archiveInputFile = null;
						}
					} catch(Exception e) {
						// pass
					}
				}
			}
		}

		@Override
		public void closeArchive() throws Exception {
			if(archiveOpen == false) {
				throw new Exception("archive not open.");
			}
			
			archiveEntryMap = null;
			
			try {
				if(archive != null) {
					archive.close();
					archive = null;
				}
			} catch(Exception e) {
				// pass
			}
			
			try {
				if(archiveInputFile != null) {
					archiveInputFile.close();
					archiveInputFile = null;
				}
			} catch(Exception e) {
				// pass
			}
			
			archiveOpen = false;
		}

		@Override
		public TypeableFile getFile(ArchiveReaderEntry archiveReaderEntry) throws Exception {
			if(archiveOpen == false) {
				throw new Exception("archive not open.");
			}
			
			TypeableFile outputFile;
			
			RandomAccessFile archiveOutputFile = null;
			try {
				ISimpleInArchiveItem archiveEntry = archiveEntryMap.get(archiveReaderEntry);
				
				outputFile = new TypeableFile(File.createTempFile("oboco-plugin-archive-sevenzipjbinding-", ".tmp"));
				
				archiveOutputFile = new RandomAccessFile(outputFile, "rw");
				
				ExtractOperationResult result = archiveEntry.extractSlow(new RandomAccessFileOutStream(archiveOutputFile));
				
				if(result != ExtractOperationResult.OK) {
					throw new Exception("archiveOutputFile not ok.");
				}
			} finally {
				try {
					if(archiveOutputFile != null) {
						archiveOutputFile.close();
					}
				} catch(Exception e) {
					// pass
				}
			}
			
			return outputFile;
		}

		@Override
		public Set<ArchiveReaderEntry> getArchiveReaderEntrySet() throws Exception {
			if(archiveOpen == false) {
				throw new Exception("archive not open.");
			}
			
			return archiveEntryMap.keySet();
		}
	}
}