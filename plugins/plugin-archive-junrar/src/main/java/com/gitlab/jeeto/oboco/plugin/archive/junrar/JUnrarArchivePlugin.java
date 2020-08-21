package com.gitlab.jeeto.oboco.plugin.archive.junrar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import com.gitlab.jeeto.oboco.plugin.FileType;
import com.gitlab.jeeto.oboco.plugin.FileWrapper;
import com.gitlab.jeeto.oboco.plugin.FileWrapperSorter;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReader;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReaderBase;
import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;

public class JUnrarArchivePlugin extends Plugin {
	
    public JUnrarArchivePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }
	
    @Extension
    public static class JUnrarArchiveReader extends ArchiveReaderBase implements ArchiveReader.RarArchiveReader {
    	private Archive archive = null;
    	private List<FileWrapper<FileHeader>> listFileHeaderWrapper = new ArrayList<FileWrapper<FileHeader>>();
    	
		@Override
		public void openArchive(FileWrapper<File> inputFileWrapper) throws Exception {
			List<FileType> listOutputFileType = new ArrayList<FileType>();
			listOutputFileType.add(FileType.JPG);
			listOutputFileType.add(FileType.PNG);
			
			File inputFile = inputFileWrapper.getFile();
			
			FileInputStream fileInputStream = new FileInputStream(inputFile);
			
			archive = new Archive(fileInputStream);
			
			FileHeader fileHeader = archive.nextFileHeader();
	        while (fileHeader != null) {
	            if (!fileHeader.isDirectory()) {
	            	FileType outputFileType = FileType.getFileType(archive.getInputStream(fileHeader));
	            	if(listOutputFileType.contains(outputFileType)) {
	            		FileWrapper<FileHeader> fileHeaderWrapper = new FileWrapper<FileHeader>(fileHeader, outputFileType);
	            		
	            		listFileHeaderWrapper.add(fileHeaderWrapper);
	            	}
	            }

	            fileHeader = archive.nextFileHeader();
	        }
	        
	        new FileWrapperSorter<FileHeader>() {
	        	private String getName(FileHeader fileHeader) {
	        		if(fileHeader.isUnicode()) {
	        			return fileHeader.getFileNameW();
	        		} else {
	        			return fileHeader.getFileNameString();
	        		}
	            }
	        	
				@Override
				public int compare(FileWrapper<FileHeader> fileWrapper1, FileWrapper<FileHeader> fileWrapper2) throws Exception {
					return getName(fileWrapper1.getFile()).compareTo(getName(fileWrapper2.getFile()));
				}
	        	
	        }.sort(listFileHeaderWrapper);
		}

		@Override
		public void closeArchive() throws Exception {
			if(archive != null) {
				archive.close();
			}
		}

		@Override
		public FileWrapper<File> readFile(Integer index) throws Exception {
			InputStream inputStream = null;
			OutputStream outputStream = null;
			try {
				FileWrapper<FileHeader> fileHeaderFileTypeWrapper = listFileHeaderWrapper.get(index);
				
				FileType outputFileType = fileHeaderFileTypeWrapper.getFileType();
				
				inputStream = archive.getInputStream(fileHeaderFileTypeWrapper.getFile());
				
				File outputFile = File.createTempFile("oboco-plugin-archive-junrar-", ".tmp");
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
			return listFileHeaderWrapper.size();
		}
    }
}