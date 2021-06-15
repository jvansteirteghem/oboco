package com.gitlab.jeeto.oboco.plugin.image;

import com.gitlab.jeeto.oboco.plugin.FileType;
import com.gitlab.jeeto.oboco.plugin.TypeableFile;

public abstract class ImageManagerBase implements ImageManager {
	public TypeableFile createImage(TypeableFile inputFile, FileType outputFileType) throws Exception {
		return createImage(inputFile, outputFileType, null, null, null);
	}
}
