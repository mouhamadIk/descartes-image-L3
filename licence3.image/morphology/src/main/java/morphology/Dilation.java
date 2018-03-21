package morphology;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ImgPlus;
import net.imagej.ops.OpService;
import net.imglib2.IterableInterval;
import net.imglib2.algorithm.morphology.StructuringElements;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.integer.UnsignedByteType;

@Plugin(type = Command.class, name = "dilation", menuPath = "Plugins>TD 7>Dilation")
public class Dilation implements Command {

	@Parameter
	OpService ops;
	@Parameter
	DatasetService ds;

	@Parameter
	ImgPlus<UnsignedByteType> image;

	@Parameter(type = ItemIO.OUTPUT)
	Dataset dilatedImage;

	@Override
	public void run() {
		Img<UnsignedByteType> binImg = ops.convert().uint8(image);
		IterableInterval<UnsignedByteType> binDilation = ops.morphology().dilate(binImg, StructuringElements.disk(1, 2));
		dilatedImage = ds
				.create(new ImgPlus<UnsignedByteType>(ops.convert().uint8(binDilation), image.getName() + "_dilation"));
	}

}
