package com.gitlab.jeeto.oboco.plugin.image;

import com.gitlab.jeeto.oboco.plugin.FactoryBase;
import com.gitlab.jeeto.oboco.plugin.FileType;
import com.gitlab.jeeto.oboco.plugin.image.ImageManager.Jpg2JpgImageManager;
import com.gitlab.jeeto.oboco.plugin.image.ImageManager.Png2JpgImageManager;

public class ImageManagerFactory extends FactoryBase {
	public ImageManagerFactory() {
		super();
	}
	
	public ImageManager getImageManager(FileType inputFileType, FileType outputFileType) throws Exception {
		ImageManager imageManager = null;
		
		if(FileType.JPG.equals(inputFileType) && FileType.JPG.equals(outputFileType)) {
			imageManager = getExtension(Jpg2JpgImageManager.class);
		} else if(FileType.PNG.equals(inputFileType) && FileType.JPG.equals(outputFileType)) {
			imageManager = getExtension(Png2JpgImageManager.class);
		}
		
		return imageManager;
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}
}
