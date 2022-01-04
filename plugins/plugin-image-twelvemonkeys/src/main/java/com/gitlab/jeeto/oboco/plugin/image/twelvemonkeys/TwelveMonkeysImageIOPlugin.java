package com.gitlab.jeeto.oboco.plugin.image.twelvemonkeys;

import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;

import org.pf4j.Plugin;
import org.pf4j.PluginRuntimeException;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReaderSpi;
import com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageWriterSpi;

public class TwelveMonkeysImageIOPlugin extends Plugin {
	private static Logger logger = LoggerFactory.getLogger(TwelveMonkeysImageIOPlugin.class.getName());
	
	public TwelveMonkeysImageIOPlugin(PluginWrapper wrapper) {
		super(wrapper);
	}
	
	@Override
	public void start() {
		try {
			IIORegistry registry = IIORegistry.getDefaultInstance();
			
			ImageReaderSpi imageReaderSpi = registry.getServiceProviderByClass(JPEGImageReaderSpi.class);
			
			if(imageReaderSpi == null) {
				imageReaderSpi = new JPEGImageReaderSpi();
				
				registry.registerServiceProvider(imageReaderSpi);
			}
			
			Iterator<ImageReader> imageReaderIterator = ImageIO.getImageReadersByFormatName("jpg");
			while(imageReaderIterator.hasNext()) {
				ImageReader imageReader = imageReaderIterator.next();
				
				logger.debug("imageReader: " + imageReader);
			}
			
			ImageWriterSpi imageWriterSpi = registry.getServiceProviderByClass(JPEGImageWriterSpi.class);
			
			if(imageWriterSpi == null) {
				imageWriterSpi = new JPEGImageWriterSpi();
				
				registry.registerServiceProvider(imageWriterSpi);
			}
			
			Iterator<ImageWriter> imageWriterIterator = ImageIO.getImageWritersByFormatName("jpg");
			while(imageWriterIterator.hasNext()) {
				ImageWriter imageWriter = imageWriterIterator.next();
				
				logger.debug("imageWriter: " + imageWriter);
			}
		} catch(Exception e) {
			throw new PluginRuntimeException(e);
		}
	}
	
	@Override
	public void stop() {
		try {
			IIORegistry registry = IIORegistry.getDefaultInstance();
			
			ImageReaderSpi imageReaderSpi = registry.getServiceProviderByClass(JPEGImageReaderSpi.class);
			
			if(imageReaderSpi != null) {
				registry.deregisterServiceProvider(imageReaderSpi);
			}
			
			Iterator<ImageReader> imageReaderIterator = ImageIO.getImageReadersByFormatName("jpg");
			while(imageReaderIterator.hasNext()) {
				ImageReader imageReader = imageReaderIterator.next();
				
				logger.debug("imageReader: " + imageReader);
			}
			
			ImageWriterSpi imageWriterSpi = registry.getServiceProviderByClass(JPEGImageWriterSpi.class);
			
			if(imageWriterSpi != null) {
				registry.deregisterServiceProvider(imageWriterSpi);
			}
			
			Iterator<ImageWriter> imageWriterIterator = ImageIO.getImageWritersByFormatName("jpg");
			while(imageWriterIterator.hasNext()) {
				ImageWriter imageWriter = imageWriterIterator.next();
				
				logger.debug("imageWriter: " + imageWriter);
			}
		} catch(Exception e) {
			throw new PluginRuntimeException(e);
		}
	}
}