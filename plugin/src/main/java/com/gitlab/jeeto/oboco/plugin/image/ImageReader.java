package com.gitlab.jeeto.oboco.plugin.image;

import java.awt.image.BufferedImage;
import java.io.File;

import org.pf4j.ExtensionPoint;

public interface ImageReader extends ExtensionPoint {
	public BufferedImage read(File inputFile) throws Exception;
	
	public static interface JpegImageReader extends ImageReader {
		
	}
	
	public static interface PngImageReader extends ImageReader {
		
	}
}
