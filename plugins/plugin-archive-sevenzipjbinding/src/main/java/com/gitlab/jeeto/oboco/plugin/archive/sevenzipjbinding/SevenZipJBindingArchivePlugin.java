package com.gitlab.jeeto.oboco.plugin.archive.sevenzipjbinding;

import java.io.File;
import java.io.RandomAccessFile;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginRuntimeException;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gitlab.jeeto.oboco.plugin.FileType;
import com.gitlab.jeeto.oboco.plugin.NaturalOrderComparator;
import com.gitlab.jeeto.oboco.plugin.TypeableFile;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReader;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReaderBase;

import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
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
    	private static Logger logger = LoggerFactory.getLogger(SevenZipJBindingArchiveReader.class.getName());
    	private RandomAccessFile randomAccessFileIn = null;
    	private List<ISimpleInArchiveItem> simpleInArchiveItemList = new ArrayList<ISimpleInArchiveItem>();
    	
		@Override
		public void openArchive(TypeableFile inputFile) throws Exception {
			List<FileType> outputFileTypeList = new ArrayList<FileType>();
			outputFileTypeList.add(FileType.JPG);
			outputFileTypeList.add(FileType.PNG);
			
			randomAccessFileIn = new RandomAccessFile(inputFile, "r");
			
			RandomAccessFileInStream randomAccessFileInStream = new RandomAccessFileInStream(randomAccessFileIn);
			
			IInArchive inArchive = SevenZip.openInArchive(null, randomAccessFileInStream);
			ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();
			
			// start readListFile
			Instant durationStart = Instant.now();
			
			simpleInArchiveItemList = new ArrayList<ISimpleInArchiveItem>();
            for (ISimpleInArchiveItem simpleInArchiveItem : simpleInArchive.getArchiveItems()) {
                if (!simpleInArchiveItem.isFolder()) {
                	FileType outputFileType = FileType.getFileType(simpleInArchiveItem.getPath());
            		if(outputFileTypeList.contains(outputFileType)) {
            			simpleInArchiveItemList.add(simpleInArchiveItem);
            		}
                }
            }
            
            Instant durationStop = Instant.now();
	        
	        long duration = Duration.between(durationStart, durationStop).toMillis();
	        
	        logger.debug("readListFile: " + duration + " ms");
	        // stop readListFile
            
	        simpleInArchiveItemList.sort(new NaturalOrderComparator<ISimpleInArchiveItem>() {
	        	@Override
	    		public String toString(ISimpleInArchiveItem o) {
	        		try {
						return o.getPath();
					} catch (SevenZipException e) {
						return "";
					}
	        	}
			});
		}

		@Override
		public void closeArchive() throws Exception {
			if(randomAccessFileIn != null) {
				randomAccessFileIn.close();
			}
		}

		@Override
		public TypeableFile readFile(Integer index) throws Exception {
			RandomAccessFile randomAccessFileOut = null;
			try {
				ISimpleInArchiveItem simpleInArchiveItem = simpleInArchiveItemList.get(index);
				
				TypeableFile outputFile = new TypeableFile(File.createTempFile("oboco-plugin-archive-sevenzipjbinding-", ".tmp"));
				randomAccessFileOut = new RandomAccessFile(outputFile, "rw");
				
				RandomAccessFileOutStream randomAccessFileOutStream = new RandomAccessFileOutStream(randomAccessFileOut);
				
				// start readFile
				Instant durationStart = Instant.now();
				
	        	ExtractOperationResult extractOperationResult = simpleInArchiveItem.extractSlow(randomAccessFileOutStream);
	        	
	        	Instant durationStop = Instant.now();
		        
		        long duration = Duration.between(durationStart, durationStop).toMillis();
		        
		        logger.debug("readFile: " + duration + " ms");
		        // stop readFile
	            
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
		public Integer readSize() throws Exception {
			return simpleInArchiveItemList.size();
		}
    }
}