import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ImgPlus;
import net.imagej.ops.OpService;
import net.imglib2.type.numeric.RealType;

@Plugin(type = Command.class, menuPath = "Plugins>TD 2>Equalize histogram")
public class EqualizeHistogramPlugin<T extends RealType<T>> implements Command {

	@Parameter
	private OpService ops;

	@Parameter
	private ImgPlus<T> image;

	@Parameter(required = false)
	private int numBins = 256;

	@Parameter(type = ItemIO.OUTPUT)
	private ImgPlus<T> result;

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		result = (ImgPlus<T>) ops.run("equalizeHistogram", image, numBins);
	}

}
