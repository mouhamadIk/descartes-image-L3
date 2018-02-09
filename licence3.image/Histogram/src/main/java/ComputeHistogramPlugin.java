import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ImgPlus;
import net.imagej.ops.OpService;
import net.imglib2.type.numeric.RealType;

@Plugin(type = Command.class, menuPath = "Plugins>TD 2>Compute Histogram")
public class ComputeHistogramPlugin<T extends RealType<T>> implements Command {

	@Parameter
	ImgPlus<T> image;

	@Parameter(required = false)
	private int histogramBins = 256;

	@Parameter(required = false)
	private int histogramHeight = 256;

	@Parameter(type = ItemIO.OUTPUT)
	private ImgPlus<T> result;

	@Parameter
	private OpService ops;

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		result = (ImgPlus<T>) ops.run("computeHistogram", image, histogramBins, histogramHeight);

	}

}
