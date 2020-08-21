package com.gitlab.jeeto.oboco.plugin.image.jdk;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.gitlab.jeeto.oboco.plugin.FileType;
import com.gitlab.jeeto.oboco.plugin.FileWrapper;
import com.gitlab.jeeto.oboco.plugin.image.ScaleType;
import com.gitlab.jeeto.oboco.plugin.image.jdk.JdkImagePlugin.JdkImageManager;

import junit.framework.TestCase;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class ImageTest extends TestCase {
	@Test
	public void testFileType() throws Exception {
		//File inputFile = new File("src/test/resources/java-duke.png");
		File inputFile = new File("src/test/resources/java-duke-large.jpg");
		FileType inputFileType = FileType.getFileType(inputFile);
		FileType outputFileType = FileType.JPG;
		
		JdkImageManager imageManager = Mockito.spy(JdkImagePlugin.JdkImageManager.class);
		
		FileWrapper<File> inputFileWrapper = new FileWrapper<File>(inputFile, inputFileType);
		
		FileWrapper<File> outputFileWrapper = imageManager.createImage(inputFileWrapper, outputFileType, ScaleType.FILL, 250, 450);
		
		System.out.println(outputFileWrapper.getFile().getPath());
	}
}
