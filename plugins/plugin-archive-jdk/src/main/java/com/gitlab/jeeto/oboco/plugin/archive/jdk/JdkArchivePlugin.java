package com.gitlab.jeeto.oboco.plugin.archive.jdk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import com.gitlab.jeeto.oboco.plugin.TypeableFile;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveEntry;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveEntryType;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReader;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReaderBase;

public class JdkArchivePlugin extends Plugin {
	public JdkArchivePlugin(PluginWrapper wrapper) {
		super(wrapper);
	}
	
	@Extension
	public static class JdkArchiveReader extends ArchiveReaderBase implements ArchiveReader.ZipArchiveReader {
		private ZipFile zipFile = null;
		private Map<ArchiveEntry, ZipEntry> zipEntryMap = null;
		
		@Override
		public void openArchive(TypeableFile inputFile) throws Exception {
			if(zipFile != null) {
				throw new Exception("archive is open.");
			}
			
			zipFile = new ZipFile(inputFile);
			
			zipEntryMap = new HashMap<ArchiveEntry, ZipEntry>();

			Enumeration<? extends ZipEntry> e = zipFile.entries();
			while(e.hasMoreElements()) {
				ZipEntry zipEntry = e.nextElement();
				
				String name = zipEntry.getName();
				
				ArchiveEntryType type;
				if(zipEntry.isDirectory()) {
					type = ArchiveEntryType.DIRECTORY;
				} else {
					type = ArchiveEntryType.FILE;
				}
				
				ArchiveEntry archiveEntry = new ArchiveEntry(name, type);
				
				zipEntryMap.put(archiveEntry, zipEntry);
			}
		}

		@Override
		public void closeArchive() throws Exception {
			if(zipFile == null) {
				throw new Exception("archive is closed.");
			}
			
			zipFile.close();
		}
		
		@Override
		public TypeableFile getFile(ArchiveEntry archiveEntry) throws Exception {
			if(zipFile == null) {
				throw new Exception("archive is closed.");
			}
			
			InputStream inputStream = null;
			OutputStream outputStream = null;
			try {
				ZipEntry zipEntry = zipEntryMap.get(archiveEntry);
				
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
		public Set<ArchiveEntry> getArchiveEntrySet() throws Exception {
			if(zipFile == null) {
				throw new Exception("archive is closed.");
			}
			
			return zipEntryMap.keySet();
		}
	}
}