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
import com.gitlab.jeeto.oboco.plugin.NaturalOrderComparator;
import com.gitlab.jeeto.oboco.plugin.TypeableFile;
import com.gitlab.jeeto.oboco.plugin.FileType.Type;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReader;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReaderBase;

public class JdkArchivePlugin extends Plugin {
	
    public JdkArchivePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }
	
    @Extension
    public static class JdkArchiveReader extends ArchiveReaderBase implements ArchiveReader.ZipArchiveReader {
		private ZipFile zipFile = null;
        private ArrayList<ZipEntry> zipEntryList;
        
		@Override
		public void openArchive(TypeableFile inputFile) throws Exception {
			List<FileType> outputFileTypeList = FileType.getFileTypeList(Type.IMAGE);
			
			zipFile = new ZipFile(inputFile);
			zipEntryList = new ArrayList<ZipEntry>();

	        Enumeration<? extends ZipEntry> e = zipFile.entries();
	        while (e.hasMoreElements()) {
	            ZipEntry zipEntry = e.nextElement();
	            if (zipEntry.isDirectory() == false) {
	            	FileType outputFileType = FileType.getFileType(zipEntry.getName());
	            	if(outputFileTypeList.contains(outputFileType)) {
	            		zipEntryList.add(zipEntry);
	            	}
	            }
	        }
	        
	        zipEntryList.sort(new NaturalOrderComparator<ZipEntry>() {
	        	@Override
	    		public String toString(ZipEntry o) {
					return o.getName();
	        	}
			});
		}

		@Override
		public void closeArchive() throws Exception {
			if(zipFile != null) {
				zipFile.close();
			}
		}
		
		@Override
		public TypeableFile readFile(Integer index) throws Exception {
			InputStream inputStream = null;
			OutputStream outputStream = null;
			try {
				ZipEntry zipEntry = zipEntryList.get(index);
				
				inputStream = zipFile.getInputStream(zipEntry);
				
				TypeableFile outputFile = new TypeableFile(File.createTempFile("oboco-plugin-archive-jdk-", ".tmp"));
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
			return zipEntryList.size();
		}
    }
}