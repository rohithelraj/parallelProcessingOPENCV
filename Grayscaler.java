import java.util.concurrent.RecursiveAction;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.*;
import static java.util.Arrays.asList;
/**
 * Bluescaler computes the Bluescaled version of the image feeded by ImageLoaderMain.
 * Computation is performed using multiple cores of the processor.
 *
 * @author rohith.raju & meghadoot.gardi
 * @version 02.01.2017
 */
public class Grayscaler extends RecursiveAction {
	private static final long serialVersionUID = 1L;
	private Mat frame;
	final int firstheight;
	final int height;
	final int splitSize = 160;
	/**
	 * Constructor for Bluescaler.
	 *
	 * @param Mat The image.
	 * @param int The first pixel of the image.
	 * @param int The last pixel of the image.
	 *
	 */
	public Grayscaler(Mat frame, int firstheight, int height) {
		this.frame = frame;
		this.firstheight = firstheight;
		this.height = height;

	}
	/**
	 * Compute method of RecursiveAction. Parallel processing by recursive calls is done here.
	 */
	@Override
	protected void compute() {
		if (height - firstheight > splitSize) {
			 int mid = (firstheight + height) >>> 1;
			 //recursive calls for parallel processing.
			 invokeAll(asList(new Grayscaler(frame, firstheight, mid), new Grayscaler(frame, mid, height)));
			}
		 else{
			 	for (int i = firstheight; i < height; i++) {
						for (int j = 0; j < frame.cols(); j++) {
							double[] data = frame.get(i, j);
							int x = (int) data[0];
							int y = (int) data[1];
							int r = (int) data[2];
							int gr = (x + y + r) / 3;
							data[0] = gr;
							data[1] = 0;
							data[2] = 0;
							frame.put(i, j, data);
						}
				}
			 	Highgui.imwrite("blueFinal.jpg", frame);

		}

	}

}

