package com.gitlab.jeeto.oboco.plugin.image;

import java.io.File;

import com.gitlab.jeeto.oboco.plugin.FileType;
import com.gitlab.jeeto.oboco.plugin.FileWrapper;

public abstract class ImageManagerBase implements ImageManager {
	public FileWrapper<File> createImage(FileWrapper<File> inputFileWrapper, FileType outputFileType) throws Exception {
		return createImage(inputFileWrapper, outputFileType, null, null, null);
	}
}
