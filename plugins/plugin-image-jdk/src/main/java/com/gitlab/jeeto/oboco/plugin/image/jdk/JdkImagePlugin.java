package com.gitlab.jeeto.oboco.plugin.image.jdk;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;

import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginRuntimeException;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gitlab.jeeto.oboco.plugin.FileType;
import com.gitlab.jeeto.oboco.plugin.TypeableFile;
import com.gitlab.jeeto.oboco.plugin.image.ImageManager;
import com.gitlab.jeeto.oboco.plugin.image.ImageManagerBase;
import com.gitlab.jeeto.oboco.plugin.image.ScaleType;
import com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReaderSpi;
import com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageWriterSpi;

public class JdkImagePlugin extends Plugin {
	private static Logger logger = LoggerFactory.getLogger(JdkImagePlugin.class.getName());
	
	static {
		try {
			IIORegistry registry = IIORegistry.getDefaultInstance();
			
			ImageReaderSpi imageReaderSpi = new JPEGImageReaderSpi();
			
			registry.registerServiceProvider(imageReaderSpi);
			
			Iterator<ImageReader> imageReaders = ImageIO.getImageReadersByFormatName("jpg");
			while(imageReaders.hasNext()) {
				logger.debug("imageReader: " + imageReaders.next());
			}
			
			ImageWriterSpi imageWriterSpi = new JPEGImageWriterSpi();
			
			registry.registerServiceProvider(imageWriterSpi);
			
			Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName("jpg");
			while(imageWriters.hasNext()) {
				logger.debug("imageWriter: " + imageWriters.next());
			}
		} catch(Exception e) {
			throw new PluginRuntimeException(e);
		}
	}
	
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
	
	public JdkImagePlugin(PluginWrapper wrapper) {
		super(wrapper);
	}
	
	@Extension
	public static class JdkImageManager extends ImageManagerBase implements ImageManager.Jpg2JpgImageManager, ImageManager.Png2JpgImageManager {
		// https://stackoverflow.com/questions/11959758/java-maintaining-aspect-ratio-of-jpanel-background-image/11959928#11959928
		
		private double calculateScaleFactor(int originalSize, int targetSize) {
			double scaleFactor = 1;
			
			if(originalSize > targetSize) {
				scaleFactor = (double) targetSize / (double) originalSize;
			}
			
			return scaleFactor;

		}
		
		private double getScaleFactor(Dimension originalSize, Dimension targetSize, ScaleType scaleType) {
			double scaleFactor = 1d;
			
			if(originalSize != null && targetSize != null) {
				double scaleWidth = calculateScaleFactor(originalSize.width, targetSize.width);
				double scaleHeight = calculateScaleFactor(originalSize.height, targetSize.height);
				
				if(ScaleType.FIT.equals(scaleType)) {
					scaleFactor = Math.min(scaleWidth, scaleHeight);
				} else if(ScaleType.FILL.equals(scaleType)) {
					scaleFactor = Math.max(scaleWidth, scaleHeight);
				}
			}

			return scaleFactor;
		}
		
		private BufferedImage scale(BufferedImage inputImage, ScaleType outputScaleType, Integer outputScaleWidth, Integer outputScaleHeight) throws Exception {
			BufferedImage outputBufferedImage = null;
			
			if(ScaleType.DEFAULT.equals(outputScaleType)) {
				double scaleFactor = 1d;
				
				if(outputScaleWidth != null) {
					scaleFactor = calculateScaleFactor(inputImage.getWidth(), outputScaleWidth);
				} else if(outputScaleHeight != null) {
					scaleFactor = calculateScaleFactor(inputImage.getHeight(), outputScaleHeight);
				}
				
				int scaleWidth = (int) Math.round(inputImage.getWidth() * scaleFactor);
				int scaleHeight = (int) Math.round(inputImage.getHeight() * scaleFactor);
				
				Image scaledInputImage = inputImage.getScaledInstance(scaleWidth, scaleHeight, Image.SCALE_SMOOTH);
				
				outputBufferedImage = new BufferedImage(scaleWidth, scaleHeight, BufferedImage.TYPE_INT_RGB);
				
				Graphics2D graphics2D = null;
				try {
					graphics2D = outputBufferedImage.createGraphics();
					graphics2D.drawImage(scaledInputImage, 0, 0, null);
				} finally {
					try {
						if(graphics2D != null) {
							graphics2D.dispose();
						}
					} catch(Exception e) {
						// pass
					}
				}
				
				scaledInputImage.flush();
			} else if(ScaleType.FIT.equals(outputScaleType) || ScaleType.FILL.equals(outputScaleType)) {
				if(outputScaleWidth == null) {
					outputScaleWidth = inputImage.getWidth();
				}
				
				if(outputScaleHeight == null) {
					outputScaleHeight = inputImage.getHeight();
				}
				
				double scaleFactor = Math.min(1d, getScaleFactor(new Dimension(inputImage.getWidth(), inputImage.getHeight()), new Dimension(outputScaleWidth, outputScaleHeight), outputScaleType));
				
				int scaleWidth = (int) Math.round(inputImage.getWidth() * scaleFactor);
				int scaleHeight = (int) Math.round(inputImage.getHeight() * scaleFactor);

				Image scaledInputImage = inputImage.getScaledInstance(scaleWidth, scaleHeight, Image.SCALE_SMOOTH);

				int x = (outputScaleWidth - 1 - scaledInputImage.getWidth(null)) / 2;
				int y = (outputScaleHeight - 1 - scaledInputImage.getHeight(null)) / 2;
				  
				outputBufferedImage = new BufferedImage(outputScaleWidth, outputScaleHeight, BufferedImage.TYPE_INT_RGB);
				
				Graphics2D graphics2D = null;
				try {
					graphics2D = outputBufferedImage.createGraphics();
					graphics2D.drawImage(scaledInputImage, x, y, null);
				} finally {
					try {
						if(graphics2D != null) {
							graphics2D.dispose();
						}
					} catch(Exception e) {
						// pass
					}
				}
				
				scaledInputImage.flush();
			} else {
				outputBufferedImage = inputImage;
			}
			
			return outputBufferedImage;
		}
		
		private BufferedImage read(TypeableFile inputFile) throws Exception {
			BufferedImage bufferedImage = null;
			
			ImageReader imageReader = null;
			ImageReadParam imageReadParam = null;
			try {
				FileType inputFileType = inputFile.getFileType();
				
				if(FileType.JPG.equals(inputFileType)) {
					imageReader = getImageReader("jpg");
				} else if(FileType.PNG.equals(inputFileType)) {
					imageReader = getImageReader("png");
				}
				
				if(imageReader == null) {
					throw new Exception("inputFileType not supported.");
				}
				
				if(imageReadParam == null) {
					imageReadParam = imageReader.getDefaultReadParam();
				}
				
				FileImageInputStream fileImageInputStream = null;
				try {
					fileImageInputStream = new FileImageInputStream(inputFile);
					
					imageReader.setInput(fileImageInputStream);
					
					bufferedImage = imageReader.read(0, imageReadParam);
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
			
			return bufferedImage;
		}
		
		private void write(TypeableFile outputFile, BufferedImage outputImage) throws Exception {
			ImageWriter imageWriter = null;
			ImageWriteParam imageWriteParam = null;
			try {
				FileType outputFileType = outputFile.getFileType();
				
				if(FileType.JPG.equals(outputFileType)) {
					imageWriter = getImageWriter("jpg");
					
					JPEGImageWriteParam jpegImageWriteParam = new JPEGImageWriteParam(null);
					jpegImageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
					jpegImageWriteParam.setCompressionQuality(0.9f);
					
					imageWriteParam = jpegImageWriteParam;
				}
				
				if(imageWriter == null) {
					throw new Exception("outputFileType not supported.");
				}
				
				if(imageWriteParam == null) {
					imageWriteParam = imageWriter.getDefaultWriteParam();
				}
				
				FileImageOutputStream fileImageOutputStream = null;
				try {
					fileImageOutputStream = new FileImageOutputStream(outputFile);
					
					imageWriter.setOutput(fileImageOutputStream);
					 
					imageWriter.write(null, new IIOImage(outputImage, null, null), imageWriteParam);
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
		
		@Override
		public TypeableFile createImage(TypeableFile inputFile, FileType outputFileType, ScaleType outputScaleType, Integer outputScaleWidth, Integer outputScaleHeight) throws Exception {
			BufferedImage inputBufferedImage = read(inputFile);
			
			BufferedImage outputBufferedImage = scale(inputBufferedImage, outputScaleType, outputScaleWidth, outputScaleHeight);
			
			inputBufferedImage.flush();
			
			TypeableFile outputFile = new TypeableFile(File.createTempFile("oboco-plugin-image-jdk-", ".tmp"), outputFileType);
			
			write(outputFile, outputBufferedImage);
			
			outputBufferedImage.flush();
			
			return outputFile;
		}
	}
}