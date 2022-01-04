package com.gitlab.jeeto.oboco.plugin.image;

import java.awt.image.BufferedImage;
import java.io.File;

import org.pf4j.ExtensionPoint;

public interface ImageWriter extends ExtensionPoint  {
	public void write(File outputFile, BufferedImage outputImage) throws Exception;
	
	public static interface JpegImageWriter extends ImageWriter {
		
	}
	
	public static interface PngImageWriter extends ImageWriter {
		
	}
}
