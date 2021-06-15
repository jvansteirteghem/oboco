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

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import com.gitlab.jeeto.oboco.plugin.FileType;
import com.gitlab.jeeto.oboco.plugin.NaturalOrderComparator;
import com.gitlab.jeeto.oboco.plugin.TypeableFile;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReader;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReaderBase;

public class JUnrarArchivePlugin extends Plugin {
	
    public JUnrarArchivePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }
	
    @Extension
    public static class JUnrarArchiveReader extends ArchiveReaderBase implements ArchiveReader.RarArchiveReader {
    	private Archive archive = null;
    	private List<FileHeader> fileHeaderList = new ArrayList<FileHeader>();
    	
		@Override
		public void openArchive(TypeableFile inputFile) throws Exception {
			List<FileType> outputFileTypeList = new ArrayList<FileType>();
			outputFileTypeList.add(FileType.JPG);
			outputFileTypeList.add(FileType.PNG);
			
			FileInputStream fileInputStream = new FileInputStream(inputFile);
			
			archive = new Archive(fileInputStream);
			
			FileHeader fileHeader = archive.nextFileHeader();
	        while (fileHeader != null) {
	            if (!fileHeader.isDirectory()) {
	            	FileType outputFileType = FileType.getFileType(getName(fileHeader));
	            	if(outputFileTypeList.contains(outputFileType)) {
	            		fileHeaderList.add(fileHeader);
	            	}
	            }

	            fileHeader = archive.nextFileHeader();
	        }
	        
	        fileHeaderList.sort(new NaturalOrderComparator<FileHeader>() {
	        	@Override
	    		public String toString(FileHeader o) {
					return getName(o);
	        	}
			});
		}
		
		private String getName(FileHeader fileHeader) {
    		if(fileHeader.isUnicode()) {
    			return fileHeader.getFileNameW();
    		} else {
    			return fileHeader.getFileNameString();
    		}
        }

		@Override
		public void closeArchive() throws Exception {
			if(archive != null) {
				archive.close();
			}
		}

		@Override
		public TypeableFile readFile(Integer index) throws Exception {
			InputStream inputStream = null;
			OutputStream outputStream = null;
			try {
				FileHeader fileHeader = fileHeaderList.get(index);
				
				inputStream = archive.getInputStream(fileHeader);
				
				TypeableFile outputFile = new TypeableFile(File.createTempFile("oboco-plugin-archive-junrar-", ".tmp"));
				outputStream = new FileOutputStream(outputFile);
				
			    byte[] buffer = new byte[8 * 1024];
			    int bufferSize;
			    while ((bufferSize = inputStream.read(buffer)) != -1) {
			    	outputStream.write(buffer, 0, bufferSize);
			    }
			    
			    return outputFile;
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
			return fileHeaderList.size();
		}
    }
}