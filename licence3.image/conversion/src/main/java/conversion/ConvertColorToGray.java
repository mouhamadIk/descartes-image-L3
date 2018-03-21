package conversion;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.convert.ConvertService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.ImagePlus;
import ij.process.ImageConverter;
import net.imagej.Dataset;
import net.imagej.ImgPlus;
import net.imagej.ops.OpService;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.type.numeric.integer.UnsignedByteType;

@Plugin(type = Command.class, name = "convertToGray", menuPath = "Plugins>TD 7>Convert to gray")
public class ConvertColorToGray implements Command {

	@Parameter
	ConvertService cs;

	@Parameter
	OpService ops;

	@Parameter
	Dataset colorImage;

	@Parameter(type = ItemIO.OUTPUT)
	ImgPlus<UnsignedByteType> grayImage;

	@Override
	public void run() {
		ImagePlus colorImagePlus = cs.convert(colorImage, ImagePlus.class);
		ImageConverter converter = new ImageConverter(colorImagePlus);
		converter.convertToGray8();
		grayImage = new ImgPlus<UnsignedByteType>(ImagePlusAdapter.wrapByte(colorImagePlus),
				colorImage.getName() + "_gray");
	}

}
