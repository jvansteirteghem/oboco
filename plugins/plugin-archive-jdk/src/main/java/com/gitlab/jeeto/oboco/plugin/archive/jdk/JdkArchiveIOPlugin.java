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

import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReader;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReader.ZipArchiveReader;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReaderEntry;

public class JdkArchiveIOPlugin extends Plugin {
	public JdkArchiveIOPlugin(PluginWrapper wrapper) {
		super(wrapper);
	}
	
	public static class JdkArchiveReaderEntry implements ArchiveReaderEntry {
		private ZipEntry archiveEntry;
		
		public JdkArchiveReaderEntry(ZipEntry archiveEntry) {
			super();
			
			this.archiveEntry = archiveEntry;
		}
		
		public ZipEntry getArchiveEntry() {
			return archiveEntry;
		}
		
		@Override
		public String getName() {
			return archiveEntry.getName();
		}

		@Override
		public boolean isDirectory() {
			return archiveEntry.isDirectory();
		}
	}
	
	public static class JdkArchiveReader implements ArchiveReader {
		private Boolean archiveOpened = false;
		private ZipFile archive = null;
		private List<ArchiveReaderEntry> archiveReaderEntryList = null;
		
		@Override
		public void openArchive(File inputFile) throws Exception {
			if(archiveOpened) {
				throw new Exception("archive opened.");
			}
			
			try {
				archive = new ZipFile(inputFile);
				
				archiveReaderEntryList = new ArrayList<ArchiveReaderEntry>();
	
				Enumeration<? extends ZipEntry> e = archive.entries();
				while(e.hasMoreElements()) {
					ZipEntry archiveEntry = e.nextElement();
					
					ArchiveReaderEntry archiveReaderEntry = new JdkArchiveReaderEntry(archiveEntry);
					
					archiveReaderEntryList.add(archiveReaderEntry);
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
				ZipEntry archiveEntry = ((JdkArchiveReaderEntry) archiveReaderEntry).getArchiveEntry();
				
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
	
	@Extension(ordinal=100)
	public static class JdkZipArchiveReader extends JdkArchiveReader implements ZipArchiveReader {
		
	}
}