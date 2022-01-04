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
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReader;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReader.RarArchiveReader;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReaderEntry;

public class JUnrarArchiveIOPlugin extends Plugin {
	public JUnrarArchiveIOPlugin(PluginWrapper wrapper) {
		super(wrapper);
	}
	
	public static class JUnrarArchiveReaderEntry implements ArchiveReaderEntry {
		private FileHeader archiveEntry;
		
		public JUnrarArchiveReaderEntry(FileHeader archiveEntry) {
			super();
			
			this.archiveEntry = archiveEntry;
		}
		
		public FileHeader getArchiveEntry() {
			return archiveEntry;
		}
		
		@Override
		public String getName() {
			String name;
			
			if(archiveEntry.isUnicode()) {
				name = archiveEntry.getFileNameW();
			} else {
				name = archiveEntry.getFileNameString();
			}
			
			return name;
		}

		@Override
		public boolean isDirectory() {
			return archiveEntry.isDirectory();
		}
	}
	
	public static class JUnrarArchiveReader implements ArchiveReader {
		private Boolean archiveOpened = false;
		private Archive archive = null;
		private List<ArchiveReaderEntry> archiveReaderEntryList = null;
		
		@Override
		public void openArchive(File inputFile) throws Exception {
			if(archiveOpened) {
				throw new Exception("archive opened.");
			}
			
			try {
				archive = new Archive(new FileInputStream(inputFile));
				
				archiveReaderEntryList = new ArrayList<ArchiveReaderEntry>();
				
				FileHeader archiveEntry = archive.nextFileHeader();
				while(archiveEntry != null) {
					ArchiveReaderEntry archiveReaderEntry = new JUnrarArchiveReaderEntry(archiveEntry);
					
					archiveReaderEntryList.add(archiveReaderEntry);
	
					archiveEntry = archive.nextFileHeader();
				}
				
				archiveOpened = true;
			} finally {
				if(archiveOpened == false) {
					archiveReaderEntryList = null;
					
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
			if(archiveOpened == false) {
				throw new Exception("archive not opened.");
			}
			
			archiveReaderEntryList = null;
			
			try {
				if(archive != null) {
					archive.close();
					archive = null;
				}
			} catch(Exception e) {
				// pass
			}
			
			archiveOpened = false;
		}

		@Override
		public void read(ArchiveReaderEntry archiveReaderEntry, File outputFile) throws Exception {
			if(archiveOpened == false) {
				throw new Exception("archive not opened.");
			}
			
			InputStream archiveInputStream = null;
			OutputStream archiveOutputStream = null;
			try {
				FileHeader archiveEntry = ((JUnrarArchiveReaderEntry) archiveReaderEntry).getArchiveEntry();
				
				archiveInputStream = archive.getInputStream(archiveEntry);
				
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
		}

		@Override
		public List<ArchiveReaderEntry> getArchiveReaderEntries() throws Exception {
			if(archiveOpened == false) {
				throw new Exception("archive not opened.");
			}
			
			return archiveReaderEntryList;
		}
	}
	
	@Extension(ordinal=80)
	public static class JUnrarRarArchiveReader extends JUnrarArchiveReader implements RarArchiveReader {
		
	}
}