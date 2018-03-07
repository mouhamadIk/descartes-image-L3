package convolution;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ImgPlus;
import net.imagej.ops.OpService;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

@Plugin(type = Command.class, menuPath = "Plugins>TD 5>Gaussian")
public class Gaussian implements Command {

	@Parameter
	OpService ops;
	@Parameter
	DatasetService dss;

	@Parameter(persist = false)
	double sigma = 1d;

	@Parameter
	Dataset img;

	@Parameter(type = ItemIO.OUTPUT)
	ImgPlus<RealType<?>> outImgP;

	@Override
	public void run() {
		// Take img dataset as random accessible interval
		@SuppressWarnings("rawtypes")
		RandomAccessibleInterval image = img;

		// Create gauss kernel
		@SuppressWarnings("unchecked")
		RandomAccessibleInterval<FloatType> kernel = ops.convert()
				.float32((IterableInterval<DoubleType>) ops.create().kernelGauss(sigma, image.numDimensions()));

		// Apply filter
		@SuppressWarnings({ "unchecked" })
		RandomAccessibleInterval<FloatType> result = ops.filter().convolve(image, kernel);

		// Save result as output
		Img<RealType<?>> outImg = dss.create(result);
		outImgP = new ImgPlus<RealType<?>>(outImg, "img");
	}

}
