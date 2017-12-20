package net.cb.dcm.test;

import org.junit.Test;

import net.cb.dcm.tools.ImageProcessor;

public class ImageProcessorTest {
	
	private final static String SRC_PATH = "/Users/krum/Documents/Work/Web/Java/DCMProject/DCMProject/WebContent/TvApp/media/";
	private final static String OUT_PATH = "/Users/krum/Documents/Work/Web/Java/";

	@Test
	public void testGenerateImageFromVideo()
	{
		String srcFileName = SRC_PATH + "MTELHomePhoneTVC.mp4";
		ImageProcessor.saveThumbsForVideo(srcFileName, OUT_PATH);
	}
	
	@Test
	public void test2GenerateImageFromVideo()
	{
		String srcFileName = SRC_PATH + "MtelDTH.mp4";
		ImageProcessor.saveThumbsForVideo(srcFileName, OUT_PATH);
	}
	
	@Test
	public void test3GenerateImageFromVideo()
	{
		String srcFileName = SRC_PATH + "MtelUniveralPrima2.m4v";
		ImageProcessor.saveThumbsForVideo(srcFileName, OUT_PATH);
	}
}
