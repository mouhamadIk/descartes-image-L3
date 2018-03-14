package convolution;

import java.util.Arrays;

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

@Plugin(type = Command.class, menuPath = "Plugins>TD 6>Convolve (Not normalized)")
public class Convolve2 implements Command {

	@Parameter
	OpService ops;
	@Parameter
	DatasetService dss;

	@Parameter(label = "Kernel", persist = false)
	String kernelString = "0 1 0;1 -4 1;0 1 0";

	@Parameter
	Dataset img;

	@Parameter(type = ItemIO.OUTPUT)
	ImgPlus<RealType<?>> outImgP;

	@Override
	public void run() {
		// Take img dataset as random accessible interval
		@SuppressWarnings("rawtypes")
		RandomAccessibleInterval image = img;

		// Split kernel string values and convert them to double values
		double[][] kValues = Arrays.stream(kernelString.split(";"))
				.map(s -> Arrays.stream(s.split(" +")).mapToDouble(Double::parseDouble).toArray()).toArray(double[][]::new);

		System.out.println(Arrays.deepToString(kValues));

		// Create kernel random accessible interval
		RandomAccessibleInterval<DoubleType> kernel = ops.create().kernel(kValues, new DoubleType(0d));
		// To apply gaussian use: kernel = ops.create().kernelGauss(sigma);

		// Apply filter
		@SuppressWarnings({ "unchecked" })
		RandomAccessibleInterval<FloatType> result = ops.convert()
				.float32((IterableInterval<DoubleType>) ops.filter().convolve(image, kernel));

		// Save result as output
		Img<RealType<?>> outImg = dss.create(result);
		outImgP = new ImgPlus<RealType<?>>(outImg, "img");
	}

}
