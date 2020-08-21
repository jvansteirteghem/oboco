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
import com.gitlab.jeeto.oboco.plugin.FileWrapper;
import com.gitlab.jeeto.oboco.plugin.FileWrapperSorter;
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
    	private static Logger logger = LoggerFactory.getLogger(SevenZipJBindingArchiveReader.class.getName());
    	private RandomAccessFile randomAccessFileIn = null;
    	private List<FileWrapper<ISimpleInArchiveItem>> listSimpleInArchiveItemWrapper = new ArrayList<FileWrapper<ISimpleInArchiveItem>>();
    	
		@Override
		public void openArchive(FileWrapper<File> inputFileWrapper) throws Exception {
			List<FileType> listOutputFileType = new ArrayList<FileType>();
			listOutputFileType.add(FileType.JPG);
			listOutputFileType.add(FileType.PNG);
			
			File inputFile = inputFileWrapper.getFile();
			
			randomAccessFileIn = new RandomAccessFile(inputFile, "r");
			
			RandomAccessFileInStream randomAccessFileInStream = new RandomAccessFileInStream(randomAccessFileIn);
			
			IInArchive inArchive = SevenZip.openInArchive(null, randomAccessFileInStream);
			ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();
			
			// start readListFile
			Instant durationStart = Instant.now();
			
			listSimpleInArchiveItemWrapper = new ArrayList<FileWrapper<ISimpleInArchiveItem>>();
            for (ISimpleInArchiveItem simpleInArchiveItem : simpleInArchive.getArchiveItems()) {
                if (!simpleInArchiveItem.isFolder()) {
                	FileTypeOutStream fileTypeOutStream = new FileTypeOutStream();
                	try {
                		simpleInArchiveItem.extractSlow(fileTypeOutStream);
                	} catch(Exception e) {
                		// pass
                	}
            		FileType outputFileType = fileTypeOutStream.getFileType();
            		if(listOutputFileType.contains(outputFileType)) {
            			FileWrapper<ISimpleInArchiveItem> simpleInArchiveItemWrapper = new FileWrapper<ISimpleInArchiveItem>(simpleInArchiveItem, outputFileType);
            			
            			listSimpleInArchiveItemWrapper.add(simpleInArchiveItemWrapper);
            		}
                }
            }
            
            Instant durationStop = Instant.now();
	        
	        long duration = Duration.between(durationStart, durationStop).toMillis();
	        
	        logger.debug("readListFile: " + duration + " ms");
	        // stop readListFile
            
            new FileWrapperSorter<ISimpleInArchiveItem>() {
				@Override
				public int compare(FileWrapper<ISimpleInArchiveItem> fileWrapper1, FileWrapper<ISimpleInArchiveItem> fileWrapper2) throws Exception {
					return fileWrapper1.getFile().getPath().compareTo(fileWrapper2.getFile().getPath());
				}
	        	
	        }.sort(listSimpleInArchiveItemWrapper);
		}

		@Override
		public void closeArchive() throws Exception {
			if(randomAccessFileIn != null) {
				randomAccessFileIn.close();
			}
		}

		@Override
		public FileWrapper<File> readFile(Integer index) throws Exception {
			RandomAccessFile randomAccessFileOut = null;
			try {
				FileWrapper<ISimpleInArchiveItem> simpleInArchiveItemFileTypeWrapper = listSimpleInArchiveItemWrapper.get(index);
				
				FileType outputFileType = simpleInArchiveItemFileTypeWrapper.getFileType();
				File outputFile = File.createTempFile("oboco-plugin-archive-sevenzipjbinding-", ".tmp");
				randomAccessFileOut = new RandomAccessFile(outputFile, "rw");
				
				RandomAccessFileOutStream randomAccessFileOutStream = new RandomAccessFileOutStream(randomAccessFileOut);
				
				ISimpleInArchiveItem simpleInArchiveItem = simpleInArchiveItemFileTypeWrapper.getFile();
				
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
	            
	            FileWrapper<File> outputFileWrapper = new FileWrapper<File>(outputFile, outputFileType);
			    
			    return outputFileWrapper;
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
			return listSimpleInArchiveItemWrapper.size();
		}
    }
}