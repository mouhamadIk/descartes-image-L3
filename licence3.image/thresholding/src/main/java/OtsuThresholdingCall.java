import java.util.concurrent.ExecutionException;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.command.CommandService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ImgPlus;
import net.imagej.ops.OpService;
import net.imglib2.histogram.Histogram1d;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

@Plugin(type = Command.class, menuPath = "Plugins>TD 3>Otsu thresholding")
public class OtsuThresholdingCall<T extends RealType<T>> implements Command {

	@Parameter
	OpService os;

	@Parameter
	CommandService cs;

	@Parameter(persist = false)
	ImgPlus<T> image;

	@Parameter(type = ItemIO.OUTPUT)
	ImgPlus<UnsignedByteType> result;

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		Histogram1d<T> histogram = os.image().histogram(image);
		T threshold = os.threshold().otsu(histogram);
		try {
			result = (ImgPlus<UnsignedByteType>) cs
					.run(ThresholdImage.class, false, "image", image, "threshold", (long) threshold.getRealDouble()).get()
					.getOutput("imageConv");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
