package com.gitlab.jeeto.oboco.plugin.image;

import com.gitlab.jeeto.oboco.plugin.Factory;
import com.gitlab.jeeto.oboco.plugin.FactoryManager;
import com.gitlab.jeeto.oboco.plugin.image.ImageReader.JpegImageReader;
import com.gitlab.jeeto.oboco.plugin.image.ImageReader.PngImageReader;
import com.gitlab.jeeto.oboco.plugin.image.ImageWriter.JpegImageWriter;
import com.gitlab.jeeto.oboco.plugin.image.ImageWriter.PngImageWriter;

public class ImageIOFactory extends Factory {
	private static ImageIOFactory instance;
	
	public static ImageIOFactory getInstance() {
		if(instance == null) {
			synchronized(ImageIOFactory.class) {
				if(instance == null) {
					instance = new ImageIOFactory();
					
					FactoryManager factoryManager = FactoryManager.getInstance();
					factoryManager.addFactory(instance);
				}
			}
		}
		return instance;
	}
	
	private ImageIOFactory() {
		super();
	}
	
	public ImageReader getImageReader(ImageType imageType) throws Exception {
		ImageReader imageReader = null;
		
		if(ImageType.JPEG.equals(imageType)) {
			imageReader = getExtension(JpegImageReader.class);
		} else if(ImageType.PNG.equals(imageType)) {
			imageReader = getExtension(PngImageReader.class);
		} else {
			throw new Exception("imageType not supported.");
		}
		
		return imageReader;
	}
	
	public ImageWriter getImageWriter(ImageType imageType) throws Exception {
		ImageWriter imageWriter = null;
		
		if(ImageType.JPEG.equals(imageType)) {
			imageWriter = getExtension(JpegImageWriter.class);
		} else if(ImageType.PNG.equals(imageType)) {
			imageWriter = getExtension(PngImageWriter.class);
		} else {
			throw new Exception("imageType not supported.");
		}
		
		return imageWriter;
	}
}
