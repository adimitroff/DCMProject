package net.cb.dcm.tools;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

/**
 * Class with static methods for operations with images(scaling ...)
 * @author krum
 *
 */
public class ImageProcessor {
	
	public final static int DEFAULT_THUMB_SIZE = 200;
	public final static int LARGE_THUMB_SIZE = 800;
	public final static int MEDIUM_THUMB_SIZE = 400;
	
	public final static String THUMB_DIR = "thumb";
	public final static String TEMP_DIR = "temp";

	/**
	 * 
	 * @param image Source image to create thumbnail from
	 * @return
	 */
	public static BufferedImage generateThumbnail(BufferedImage image) {
		return scaleImage(image, DEFAULT_THUMB_SIZE);
	}
	
	/**
	 * Scales down an image to given size keeping aspect ratio. 
	 * If scaled size is less than given image size it will generate new but not scaled image.
	 * @param image
	 * @param scaledSize - Maximum size for width and height
	 * @return Scaled image or new image with same size as the given image.
	 */
	public static BufferedImage scaleImage(BufferedImage image, int scaledSize) {
		Dimension scaledDimesion = getScaledDimension(new Dimension(image.getWidth(), image.getHeight()),
				new Dimension(scaledSize, scaledSize));
		
		int scaledWidth = (int) scaledDimesion.getWidth();
		int scaledHeight = (int) scaledDimesion.getHeight();
		BufferedImage scaledImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = scaledImage.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);
	    g.dispose();
		
		return scaledImage;
	}
	
	/**
	 * This method generates 3 scaled thumbnail images with better quality of smallest using Progressive scaling. 
	 * result[0] is scaled to {@link ImageProcessor#LARGE_THUMB_SIZE}px, 
	 * result[1] is {@link ImageProcessor#MEDIUM_THUMB_SIZE}px 
	 * and result[2] is {@link ImageProcessor#DEFAULT_THUMB_SIZE}px.
	 * Every scaled image size can be less or equals to the given image size.
	 *
	 * @param image Source image to scale
	 * @return Array with 3 scaled images
	 */
	public static BufferedImage[] generateThumbnails(BufferedImage image) {
		BufferedImage[] scaledImages = new BufferedImage[3];
		scaledImages[0] = scaleImage(image, LARGE_THUMB_SIZE);
		scaledImages[1] = scaleImage(scaledImages[0],MEDIUM_THUMB_SIZE);
		scaledImages[2] = scaleImage(scaledImages[1], DEFAULT_THUMB_SIZE);		
		
		return scaledImages;
	}
	
	
	public static boolean generateAndSaveThumbnails(BufferedImage image, String fileName, String destDirPath) {
		BufferedImage[] thumbnails = generateThumbnails(image);
//		String fileName = srcFile.getName();
		int indexOf = fileName.lastIndexOf('.');
		if(indexOf > 0) {
			fileName = fileName.substring(0, indexOf);
		}
		try {
			String[] fileNames = new String[] {
					fileName + "_" + LARGE_THUMB_SIZE + ".jpg",
					fileName + "_" + MEDIUM_THUMB_SIZE + ".jpg", 
					fileName + "_" + DEFAULT_THUMB_SIZE + ".jpg"
					};
			ImageIO.write(thumbnails[0], "jpg", new File(destDirPath + fileNames[0]));
			ImageIO.write(thumbnails[1], "jpg", new File(destDirPath + fileNames[1]));
			ImageIO.write(thumbnails[2], "jpg", new File(destDirPath + fileNames[2]));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Calculates new dimension with maximum width and height of given boundary dimension. 
	 * If boundary is greater it will return same dimension as given image dimension.
	 * @param imgSize Base dimension
	 * @param boundary Maximum boundary dimension 
	 * @return
	 */
	private static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {
	    int newWidth = imgSize.width;
	    int newHeight = imgSize.height;

	    // first check if we need to scale width
	    if (imgSize.width > boundary.width) {
	        //scale width to fit
	        newWidth = boundary.width;
	        //scale height to maintain aspect ratio
	        newHeight = (newWidth * imgSize.height) / imgSize.width;
	    }

	    // then check if we need to scale even with the new height
	    if (newHeight > boundary.height) {
	        //scale height to fit instead
	        newHeight = boundary.height;
	        //scale width to maintain aspect ratio
	        newWidth = (newHeight * imgSize.width) / imgSize.height;
	    }

	    return new Dimension(newWidth, newHeight);
	}
	
	public static boolean generateAndSaveThumbnails(String srcFilePath)
	{
		File srcFile = new File(srcFilePath);
		if(!srcFile.exists()) {
			return false;
		}
		int extIndex = srcFile.getName().lastIndexOf('.');
		if(extIndex == -1 || extIndex == (srcFile.getName().length() -1)) {
			return false;
		}
		
		String fileExt = srcFile.getName().substring(extIndex + 1).toLowerCase();
		try {
			BufferedImage image;
			if (fileExt.equals("jpg") || fileExt.equals("jpeg") || fileExt.equals("png")) {
				// Image
				image = ImageIO.read(srcFile);
			} else {
				// Video
				image = ImageProcessor.genImageFromVideo(srcFilePath, 1);
				if (image == null) {
					return false;
				}
			}
			String thumbsDir = srcFile.getParent() + File.separatorChar + THUMB_DIR  + File.separatorChar;
			File fileDir = new File(thumbsDir);
			if(!fileDir.exists()) {
				fileDir.mkdirs();
			}
			return ImageProcessor.generateAndSaveThumbnails(image, srcFile.getName(), thumbsDir);
		} catch (Exception e) {
			return false;
		}
	}
	
	
	public static BufferedImage genImageFromVideo(String srcFilePath, int durationSeconds) {
		FFmpegFrameGrabber frameGrabber = null;
		try {
			frameGrabber = new FFmpegFrameGrabber(srcFilePath);
			frameGrabber.start();

			// Not sure start frame 0 or 1, so going to use first as 1
			int frameNumber = Math.max(1, (int) frameGrabber.getFrameRate() * durationSeconds);
			frameNumber = Math.min(frameNumber, frameGrabber.getLengthInFrames());
			frameGrabber.setFrameNumber(frameNumber);
			Java2DFrameConverter converter = new Java2DFrameConverter();
			BufferedImage image = converter.convert(frameGrabber.grab());
			frameGrabber.stop();
			return image;
		} catch (Exception e) {
			return null;
		} finally {
			if(frameGrabber!= null) {
				try {
					frameGrabber.close();
				} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
}
