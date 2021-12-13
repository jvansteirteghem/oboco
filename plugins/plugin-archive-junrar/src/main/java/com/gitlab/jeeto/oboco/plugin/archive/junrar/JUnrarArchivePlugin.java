package com.gitlab.jeeto.oboco.plugin.archive.junrar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import com.gitlab.jeeto.oboco.plugin.TypeableFile;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveEntry;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveEntryType;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReader;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReaderBase;

public class JUnrarArchivePlugin extends Plugin {
	public JUnrarArchivePlugin(PluginWrapper wrapper) {
		super(wrapper);
	}
	
	@Extension
	public static class JUnrarArchiveReader extends ArchiveReaderBase implements ArchiveReader.RarArchiveReader {
		private Archive archive = null;
		private Map<ArchiveEntry, FileHeader> fileHeaderMap = null;
		
		@Override
		public void openArchive(TypeableFile inputFile) throws Exception {
			if(archive != null) {
				throw new Exception("archive is open.");
			}
			
			FileInputStream fileInputStream = new FileInputStream(inputFile);
			
			archive = new Archive(fileInputStream);
			
			fileHeaderMap = new HashMap<ArchiveEntry, FileHeader>();
			
			FileHeader fileHeader = archive.nextFileHeader();
			while(fileHeader != null) {
				String name;
				if(fileHeader.isUnicode()) {
					name = fileHeader.getFileNameW();
				} else {
					name = fileHeader.getFileNameString();
				}
				
				ArchiveEntryType type;
				if(fileHeader.isDirectory()) {
					type = ArchiveEntryType.DIRECTORY;
				} else {
					type = ArchiveEntryType.FILE;
				}
				
				ArchiveEntry archiveEntry = new ArchiveEntry(name, type);
				
				fileHeaderMap.put(archiveEntry, fileHeader);

				fileHeader = archive.nextFileHeader();
			}
		}

		@Override
		public void closeArchive() throws Exception {
			if(archive == null) {
				throw new Exception("archive is closed.");
			}
			
			archive.close();
		}

		@Override
		public TypeableFile getFile(ArchiveEntry archiveEntry) throws Exception {
			if(archive == null) {
				throw new Exception("archive is closed.");
			}
			
			InputStream inputStream = null;
			OutputStream outputStream = null;
			try {
				FileHeader fileHeader = fileHeaderMap.get(archiveEntry);
				
				inputStream = archive.getInputStream(fileHeader);
				
				TypeableFile outputFile = new TypeableFile(File.createTempFile("oboco-plugin-archive-junrar-", ".tmp"));
				outputStream = new FileOutputStream(outputFile);
				
				byte[] buffer = new byte[8 * 1024];
				int bufferSize;
				while((bufferSize = inputStream.read(buffer)) != -1) {
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
			if(archive == null) {
				throw new Exception("archive is closed.");
			}
			
			return fileHeaderMap.keySet();
		}
	}
}