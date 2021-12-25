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
import com.gitlab.jeeto.oboco.common.TypeableFile;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReader;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReaderEntry;

public class JUnrarArchivePlugin extends Plugin {
	public JUnrarArchivePlugin(PluginWrapper wrapper) {
		super(wrapper);
	}
	
	@Extension
	public static class JUnrarArchiveReader implements ArchiveReader.RarArchiveReader {
		private Boolean archiveOpen = false;
		private Archive archive = null;
		private Map<ArchiveReaderEntry, FileHeader> archiveEntryMap = null;
		
		@Override
		public void openArchive(TypeableFile inputFile) throws Exception {
			if(archiveOpen) {
				throw new Exception("archive open.");
			}
			
			try {
				archive = new Archive(new FileInputStream(inputFile));
				
				archiveEntryMap = new HashMap<ArchiveReaderEntry, FileHeader>();
				
				FileHeader archiveEntry = archive.nextFileHeader();
				while(archiveEntry != null) {
					String name;
					if(archiveEntry.isUnicode()) {
						name = archiveEntry.getFileNameW();
					} else {
						name = archiveEntry.getFileNameString();
					}
					
					ArchiveReaderEntry.Type type;
					if(archiveEntry.isDirectory()) {
						type = ArchiveReaderEntry.Type.DIRECTORY;
					} else {
						type = ArchiveReaderEntry.Type.FILE;
					}
					
					ArchiveReaderEntry archiveReaderEntry = new ArchiveReaderEntry(name, type);
					
					archiveEntryMap.put(archiveReaderEntry, archiveEntry);
	
					archiveEntry = archive.nextFileHeader();
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
			
			archiveOpen = false;
		}

		@Override
		public TypeableFile getFile(ArchiveReaderEntry archiveReaderEntry) throws Exception {
			if(archiveOpen == false) {
				throw new Exception("archive not open.");
			}
			
			TypeableFile outputFile;
			
			InputStream archiveInputStream = null;
			OutputStream archiveOutputStream = null;
			try {
				FileHeader archiveEntry = archiveEntryMap.get(archiveReaderEntry);
				
				archiveInputStream = archive.getInputStream(archiveEntry);
				
				outputFile = new TypeableFile(File.createTempFile("oboco-plugin-archive-junrar-", ".tmp"));
				
				archiveOutputStream = new FileOutputStream(outputFile);
				
				byte[] buffer = new byte[8 * 1024];
				int bufferSize;
				while((bufferSize = archiveInputStream.read(buffer)) != -1) {
					archiveOutputStream.write(buffer, 0, bufferSize);
				}
			} finally {
				try {
					if(archiveOutputStream != null) {
						archiveOutputStream.close();
					}
				} catch(Exception e) {
					// pass
				}
				
				try {
					if(archiveInputStream != null) {
						archiveInputStream.close();
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