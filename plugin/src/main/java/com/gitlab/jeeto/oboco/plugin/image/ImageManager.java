package com.gitlab.jeeto.oboco.plugin.image;

import java.io.File;

import org.pf4j.ExtensionPoint;

import com.gitlab.jeeto.oboco.plugin.FileType;
import com.gitlab.jeeto.oboco.plugin.FileWrapper;

public interface ImageManager extends ExtensionPoint  {
	public FileWrapper<File> createImage(FileWrapper<File> inputFileWrapper, FileType outputFileType) throws Exception;
	public FileWrapper<File> createImage(FileWrapper<File> inputFileWrapper, FileType outputFileType, ScaleType outputScaleType, Integer outputScaleWidth, Integer outputScaleHeight) throws Exception;
	public static interface Jpg2JpgImageManager extends ImageManager {};
	public static interface Png2JpgImageManager extends ImageManager {};
}
