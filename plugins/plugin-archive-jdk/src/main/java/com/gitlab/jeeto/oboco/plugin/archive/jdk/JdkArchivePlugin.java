package com.gitlab.jeeto.oboco.plugin.archive.jdk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import com.gitlab.jeeto.oboco.plugin.FileType;
import com.gitlab.jeeto.oboco.plugin.FileWrapper;
import com.gitlab.jeeto.oboco.plugin.FileWrapperSorter;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReader;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReaderBase;

public class JdkArchivePlugin extends Plugin {
	
    public JdkArchivePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }
	
    @Extension
    public static class JdkArchiveReader extends ArchiveReaderBase implements ArchiveReader.ZipArchiveReader {
		private ZipFile zipFile = null;
        private ArrayList<FileWrapper<ZipEntry>> listZipEntryWrapper;
        
		@Override
		public void openArchive(FileWrapper<File> inputFileWrapper) throws Exception {
			List<FileType> listOutputFileType = new ArrayList<FileType>();
			listOutputFileType.add(FileType.JPG);
			listOutputFileType.add(FileType.PNG);
	    	
			File inputFile = inputFileWrapper.getFile();
			
			zipFile = new ZipFile(inputFile);
			listZipEntryWrapper = new ArrayList<FileWrapper<ZipEntry>>();

	        Enumeration<? extends ZipEntry> e = zipFile.entries();
	        while (e.hasMoreElements()) {
	            ZipEntry zipEntry = e.nextElement();
	            if (zipEntry.isDirectory() == false) {
	            	FileType outputFileType = FileType.getFileType(zipFile.getInputStream(zipEntry));
	            	if(listOutputFileType.contains(outputFileType)) {
	            		FileWrapper<ZipEntry> zipEntryWrapper = new FileWrapper<ZipEntry>(zipEntry, outputFileType);
	            		
	            		listZipEntryWrapper.add(zipEntryWrapper);
	            	}
	            }
	        }
	        
	        new FileWrapperSorter<ZipEntry>() {
				@Override
				public int compare(FileWrapper<ZipEntry> fileWrapper1, FileWrapper<ZipEntry> fileWrapper2) throws Exception {
					return fileWrapper1.getFile().getName().compareTo(fileWrapper2.getFile().getName());
				}
	        	
	        }.sort(listZipEntryWrapper);
		}

		@Override
		public void closeArchive() throws Exception {
			if(zipFile != null) {
				zipFile.close();
			}
		}
		
		@Override
		public FileWrapper<File> readFile(Integer index) throws Exception {
			InputStream inputStream = null;
			OutputStream outputStream = null;
			try {
				FileWrapper<ZipEntry> zipEntryWrapper = listZipEntryWrapper.get(index);
				
				FileType outputFileType = zipEntryWrapper.getFileType();
				
				inputStream = zipFile.getInputStream(zipEntryWrapper.getFile());
				
				File outputFile = File.createTempFile("oboco-plugin-archive-jdk-", ".tmp");
				outputStream = new FileOutputStream(outputFile);
				
			    byte[] buffer = new byte[8 * 1024];
			    int bufferSize;
			    while ((bufferSize = inputStream.read(buffer)) != -1) {
			    	outputStream.write(buffer, 0, bufferSize);
			    }
			    
			    FileWrapper<File> outputFileWrapper = new FileWrapper<File>(outputFile, outputFileType);
			    
			    return outputFileWrapper;
			} catch(Exception e) {
				throw e;
			} finally {
				try {
					if(inputStream != null) {
						inputStream.close();
					}
				} catch(Exception e) {
					// pass
				}
				
				try {
					if(outputStream != null) {
						outputStream.close();
					}
				} catch(Exception e) {
					// pass
				}
			}
		}

		@Override
		public Integer readSize() throws Exception {
			return listZipEntryWrapper.size();
		}
    }
}