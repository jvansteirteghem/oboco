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
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveEntry;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveEntryType;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReader;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReaderBase;

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
	public static class SevenZipJBindingArchiveReader extends ArchiveReaderBase implements ArchiveReader.ZipArchiveReader, ArchiveReader.RarArchiveReader, ArchiveReader.Rar5ArchiveReader, ArchiveReader.SevenZipArchiveReader {
		private RandomAccessFile randomAccessFileIn = null;
		private Map<ArchiveEntry, ISimpleInArchiveItem> simpleInArchiveItemMap = null;
		
		@Override
		public void openArchive(TypeableFile inputFile) throws Exception {
			if(randomAccessFileIn != null) {
				throw new Exception("archive is open.");
			}
			
			randomAccessFileIn = new RandomAccessFile(inputFile, "r");
			
			RandomAccessFileInStream randomAccessFileInStream = new RandomAccessFileInStream(randomAccessFileIn);
			
			IInArchive inArchive = SevenZip.openInArchive(null, randomAccessFileInStream);
			ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();
			
			simpleInArchiveItemMap = new HashMap<ArchiveEntry, ISimpleInArchiveItem>();
			
			for(ISimpleInArchiveItem simpleInArchiveItem: simpleInArchive.getArchiveItems()) {
				String name = simpleInArchiveItem.getPath();
				
				ArchiveEntryType type;
				if(simpleInArchiveItem.isFolder()) {
					type = ArchiveEntryType.DIRECTORY;
				} else {
					type = ArchiveEntryType.FILE;
				}
				
				ArchiveEntry archiveEntry = new ArchiveEntry(name, type);
				
				simpleInArchiveItemMap.put(archiveEntry, simpleInArchiveItem);
			}
		}

		@Override
		public void closeArchive() throws Exception {
			if(randomAccessFileIn == null) {
				throw new Exception("archive is closed.");
			}
			
			randomAccessFileIn.close();
		}

		@Override
		public TypeableFile getFile(ArchiveEntry archiveEntry) throws Exception {
			if(randomAccessFileIn == null) {
				throw new Exception("archive is closed.");
			}
			
			RandomAccessFile randomAccessFileOut = null;
			try {
				ISimpleInArchiveItem simpleInArchiveItem = simpleInArchiveItemMap.get(archiveEntry);
				
				TypeableFile outputFile = new TypeableFile(File.createTempFile("oboco-plugin-archive-sevenzipjbinding-", ".tmp"));
				randomAccessFileOut = new RandomAccessFile(outputFile, "rw");
				
				RandomAccessFileOutStream randomAccessFileOutStream = new RandomAccessFileOutStream(randomAccessFileOut);
				
				ExtractOperationResult extractOperationResult = simpleInArchiveItem.extractSlow(randomAccessFileOutStream);
				
				if (extractOperationResult != ExtractOperationResult.OK) {
					throw new Exception("extractOperationResult != ExtractOperationResult.OK");
				}
				
				return outputFile;
			} catch(Exception e) {
				throw e;
			} finally {
				try {
					if(randomAccessFileOut != null) {
						randomAccessFileOut.close();
					}
				} catch(Exception e) {
					// pass
				}
			}
		}

		@Override
		public Set<ArchiveEntry> getArchiveEntrySet() throws Exception {
			if(randomAccessFileIn == null) {
				throw new Exception("archive is closed.");
			}
			
			return simpleInArchiveItemMap.keySet();
		}
	}
}