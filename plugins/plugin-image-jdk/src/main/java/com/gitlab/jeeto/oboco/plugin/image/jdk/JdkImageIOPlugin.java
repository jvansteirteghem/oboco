package com.gitlab.jeeto.oboco.plugin.image.jdk;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;

import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdkImageIOPlugin extends Plugin {
	private static Logger logger = LoggerFactory.getLogger(JdkImageIOPlugin.class.getName());
	
	private static synchronized ImageReader getImageReader(String formatName) {
		ImageReader imageReader = ImageIO.getImageReadersByFormatName(formatName).next();
		
		logger.debug("imageReader: " + imageReader);
		
		return imageReader;
	}
	
	private static synchronized ImageWriter getImageWriter(String formatName) {
		ImageWriter imageWriter = ImageIO.getImageWritersByFormatName(formatName).next();
		
		logger.debug("imageWriter: " + imageWriter);
		
		return imageWriter;
	}
	
	public JdkImageIOPlugin(PluginWrapper wrapper) {
		super(wrapper);
	}
	
	public static abstract class JdkImageReader implements com.gitlab.jeeto.oboco.plugin.image.ImageReader {
		public abstract String getFormatName();
		
		public ImageReadParam getImageReadParameter(ImageReader imageReader) {
			return imageReader.getDefaultReadParam();
		}
		
		public BufferedImage read(File inputFile) throws Exception {
			ImageReader imageReader = null;
			try {
				imageReader = getImageReader(getFormatName());
				
				ImageReadParam imageReadParameter = getImageReadParameter(imageReader);
				
				FileImageInputStream fileImageInputStream = null;
				try {
					fileImageInputStream = new FileImageInputStream(inputFile);
					
					imageReader.setInput(fileImageInputStream);
					
					BufferedImage outputImage = imageReader.read(0, imageReadParameter);
					
					return outputImage;
				} finally {
					try {
						if(fileImageInputStream != null) {
							fileImageInputStream.close();
						}
					} catch(Exception e) {
						// pass
					}
				}
			} finally {
				try {
					if(imageReader != null) {
						imageReader.dispose();
					}
				} catch(Exception e) {
					// pass
				}
			}
		}
	}
	
	@Extension(ordinal=100)
	public static class JdkJpegImageReader extends JdkImageReader implements com.gitlab.jeeto.oboco.plugin.image.ImageReader.JpegImageReader {
		@Override
		public String getFormatName() {
			return "jpg";
		}
	}
	
	@Extension(ordinal=100)
	public static class JdkPngImageReader extends JdkImageReader implements com.gitlab.jeeto.oboco.plugin.image.ImageReader.PngImageReader {
		@Override
		public String getFormatName() {
			return "png";
		}
	}
	
	public static abstract class JdkImageWriter implements com.gitlab.jeeto.oboco.plugin.image.ImageWriter {
		public abstract String getFormatName();
		
		public ImageWriteParam getImageWriteParameter(ImageWriter imageWriter) {
			return imageWriter.getDefaultWriteParam();
		}
		
		public void write(File outputFile, BufferedImage outputImage) throws Exception {
			ImageWriter imageWriter = null;
			try {
				imageWriter = getImageWriter(getFormatName());
				
				ImageWriteParam imageWriteParameter = getImageWriteParameter(imageWriter);
				
				FileImageOutputStream fileImageOutputStream = null;
				try {
					fileImageOutputStream = new FileImageOutputStream(outputFile);
					
					imageWriter.setOutput(fileImageOutputStream);
					 
					imageWriter.write(null, new IIOImage(outputImage, null, null), imageWriteParameter);
				} finally {
					try {
						if(fileImageOutputStream != null) {
							fileImageOutputStream.close();
						}
					} catch(Exception e) {
						// pass
					}
				}
			} finally {
				try {
					if(imageWriter != null) {
						imageWriter.dispose();
					}
				} catch(Exception e) {
					// pass
				}
			}
		}
	}
	
	@Extension(ordinal=100)
	public static class JdkJpegImageWriter extends JdkImageWriter implements com.gitlab.jeeto.oboco.plugin.image.ImageWriter.JpegImageWriter {
		@Override
		public String getFormatName() {
			return "jpg";
		}
		
		@Override
		public ImageWriteParam getImageWriteParameter(ImageWriter imageWriter) {
			JPEGImageWriteParam imageWriteParameter = new JPEGImageWriteParam(null);
			imageWriteParameter.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			imageWriteParameter.setCompressionQuality(0.9f);
			
			return imageWriteParameter;
		}
	}
	
	@Extension(ordinal=100)
	public static class JdkPngImageWriter extends JdkImageWriter implements com.gitlab.jeeto.oboco.plugin.image.ImageWriter.PngImageWriter {
		@Override
		public String getFormatName() {
			return "png";
		}
	}
}