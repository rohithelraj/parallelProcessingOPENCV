import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Vector;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
/**
 * ImageLoaderMain loads an image from the camera and computes the Bluescaled
 * version of it. Computation is performed using single core of the processor.
 * Parallelism implemented with the help of Fork and Join Pool.
 *
 * @author rohith.raju & meghadoot.gardi
 * @version 12.01.2017
 */
public class ImageLoaderMain {
/**
 * Main method for ImageLoaderMain.
 *
 * @param String[] main method String array arguments.
 *
 */
	public static void main(String args[]) throws InterruptedException {
		int low_threshold  = 50; // lower threshold for canny edge detection
		int high_threshold = 150; // higher threshold for canny edge detection
		System.out.println("Initiating Canny Edge Detection");
		// Load the native library.
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		//Capturing image.
		VideoCapture camera = new VideoCapture(0);
		Thread.sleep(1000);
		camera.open(0);
		//checking if camera is working fine.
		if (!camera.isOpened()) {
			System.out.println("Camera Error");
		} else {
			System.out.println("Camera OK?");
		}
    //reading image frame.
		Mat frame = new Mat();
		camera.read(frame);
		

		System.out.println("Frame Obtained");
		System.out.println("Captured Frame Width " + frame.width());
		Mat frame1 = frame.clone();
		Mat grayImage = frame.clone();
		Mat detectedEdges = frame.clone();
		System.out.println("Initiating normal processing time t = 0 ms ");
		//single core processing beginning.
		long start = System.currentTimeMillis();
		
		Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY);
		Imgproc.blur(grayImage, detectedEdges, new Size(3, 3));
		Imgproc.Canny(detectedEdges, detectedEdges, low_threshold, high_threshold, 3, false);
		Highgui.imwrite("EdgeConcurrent.jpg", detectedEdges);
		int timing = (int) (System.currentTimeMillis() - start);
		System.out.println("Normal processing completion time t = " + (System.currentTimeMillis() - start) + " ms");
		
		BufferedImage fr = Mat2BufferedImage(detectedEdges);
		displayImage(fr, timing);
		//parallel processing end.

	}
    public static BufferedImage Mat2BufferedImage(Mat m){

// The output can be assigned either to a BufferedImage or to an Image

    int type = BufferedImage.TYPE_BYTE_GRAY;
    if ( m.channels() > 1 ) {
        type = BufferedImage.TYPE_3BYTE_BGR;
    }
    int bufferSize = m.channels()*m.cols()*m.rows();
    byte [] b = new byte[bufferSize];
    m.get(0,0,b); // get all the pixels
    BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
    final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    System.arraycopy(b, 0, targetPixels, 0, b.length);  
    return image;

}
    public static void displayImage(Image img2, int timing)
    {   
        
        ImageIcon icon=new ImageIcon(img2);
        JFrame frame=new JFrame();
        frame.setLayout(new FlowLayout());        
        frame.setSize(img2.getWidth(null)+50, img2.getHeight(null)+50);     
        JLabel lbl=new JLabel();
        lbl.setName("Normal Processing Time : "+timing+"ms");
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setTitle("Normal Processing Time : "+timing+"ms");
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}

