package com.gitlab.jeeto.oboco.plugin.archive;

import java.io.File;

import org.pf4j.ExtensionPoint;

import com.gitlab.jeeto.oboco.plugin.FileWrapper;

public interface ArchiveReader extends ExtensionPoint {
	public void openArchive(FileWrapper<File> inputFileWrapper) throws Exception;
	public void closeArchive() throws Exception;
    public FileWrapper<File> readFile(Integer index) throws Exception;
    public Integer readSize() throws Exception;
    public static interface ZipArchiveReader extends ArchiveReader {};
    public static interface RarArchiveReader extends ArchiveReader {};
    public static interface Rar5ArchiveReader extends ArchiveReader {};
    public static interface SevenZipArchiveReader extends ArchiveReader {};
}
