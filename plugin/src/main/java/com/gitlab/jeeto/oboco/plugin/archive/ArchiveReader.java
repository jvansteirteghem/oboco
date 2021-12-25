package com.gitlab.jeeto.oboco.plugin.archive;

import java.util.Set;

import org.pf4j.ExtensionPoint;

import com.gitlab.jeeto.oboco.common.TypeableFile;

public interface ArchiveReader extends ExtensionPoint {
	public void openArchive(TypeableFile inputFile) throws Exception;
	public void closeArchive() throws Exception;
    public TypeableFile getFile(ArchiveReaderEntry archiveReaderEntry) throws Exception;
    public Set<ArchiveReaderEntry> getArchiveReaderEntrySet() throws Exception;
    public static interface ZipArchiveReader extends ArchiveReader {};
    public static interface RarArchiveReader extends ArchiveReader {};
    public static interface Rar5ArchiveReader extends ArchiveReader {};
    public static interface SevenZipArchiveReader extends ArchiveReader {};
}
