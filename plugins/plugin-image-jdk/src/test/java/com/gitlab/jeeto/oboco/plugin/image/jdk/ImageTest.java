package com.gitlab.jeeto.oboco.plugin.image.jdk;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.gitlab.jeeto.oboco.plugin.FileType;
import com.gitlab.jeeto.oboco.plugin.TypeableFile;
import com.gitlab.jeeto.oboco.plugin.image.ScaleType;
import com.gitlab.jeeto.oboco.plugin.image.jdk.JdkImagePlugin.JdkImageManager;

import junit.framework.TestCase;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class ImageTest extends TestCase {
	@Test
	public void testFileType() throws Exception {
		//TypeableFile inputFile = new TypeableFile("src/test/resources/java-duke.png");
		TypeableFile inputFile = new TypeableFile("src/test/resources/java-duke-large.jpg");
		FileType outputFileType = FileType.JPG;
		
		JdkImageManager imageManager = Mockito.spy(JdkImagePlugin.JdkImageManager.class);
		
		TypeableFile outputFile = imageManager.createImage(inputFile, outputFileType, ScaleType.FILL, 250, 450);
		
		System.out.println(outputFile.getPath());
	}
}
