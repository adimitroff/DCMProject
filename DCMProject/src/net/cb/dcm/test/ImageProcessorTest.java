package net.cb.dcm.test;

import org.junit.Assert;
import org.junit.Test;

import net.cb.dcm.tools.ImageProcessor;

public class ImageProcessorTest {
	
	private final static String SRC_PATH = "/Users/krum/Documents/Work/Web/Java/DCMProject/DCMProject/WebContent/TvApp/media/";


	@Test
	public void test1GenerateAndSaveThumbnails()
	{
		String srcFilePath = SRC_PATH + "girl_1.jpg";
		Assert.assertTrue(ImageProcessor.generateAndSaveThumbnails(srcFilePath));
	}
	
	@Test
	public void test2GenerateAndSaveThumbnails()
	{
		String srcFilePath = SRC_PATH + "girl_2.jpg";
		Assert.assertTrue(ImageProcessor.generateAndSaveThumbnails(srcFilePath));
	}
	
	
	@Test
	public void test3GenerateAndSaveThumbnails()
	{
		String srcFilePath = SRC_PATH + "girl_3.jpg";
		Assert.assertTrue(ImageProcessor.generateAndSaveThumbnails(srcFilePath));
	}
	
	@Test
	public void tes6GenerateImageFromVideo()
	{
		String srcFilePath = SRC_PATH + "MTELHomePhoneTVC.mp4";
		Assert.assertTrue(ImageProcessor.generateAndSaveThumbnails(srcFilePath));
	}
	
	@Test
	public void test7GenerateImageFromVideo()
	{
		String srcFilePath = SRC_PATH + "MtelDTH.mp4";
		Assert.assertTrue(ImageProcessor.generateAndSaveThumbnails(srcFilePath));
	}
	
	@Test
	public void test8GenerateImageFromVideo()
	{
		String srcFilePath = SRC_PATH + "MtelUniveralPrima2.m4v";
		Assert.assertTrue(ImageProcessor.generateAndSaveThumbnails(srcFilePath));
	}
	
}
