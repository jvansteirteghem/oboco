package com.gitlab.jeeto.oboco.plugin.image;

import org.pf4j.ExtensionPoint;

import com.gitlab.jeeto.oboco.plugin.FileType;
import com.gitlab.jeeto.oboco.plugin.TypeableFile;

public interface ImageManager extends ExtensionPoint  {
	public TypeableFile createImage(TypeableFile inputFile, FileType outputFileType) throws Exception;
	public TypeableFile createImage(TypeableFile inputFile, FileType outputFileType, ScaleType outputScaleType, Integer outputScaleWidth, Integer outputScaleHeight) throws Exception;
	public static interface Jpg2JpgImageManager extends ImageManager {};
	public static interface Png2JpgImageManager extends ImageManager {};
}
