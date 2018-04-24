package morpion;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.command.CommandService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import net.imagej.ImgPlus;
import net.imagej.ops.OpService;
import net.imglib2.type.numeric.RealType;

@Plugin(type = Command.class, name = "morpion", menuPath = "Plugins>Morpion")
public class Morpion<T extends RealType<T>> implements Command {

	@Parameter
	CommandService cs;
	
	@Parameter
	OpService ops;
	
	@Parameter(persist = false)
	ImgPlus<T> image;

	@Parameter(required = false)
	int threshold = 80;

	@Parameter(type = ItemIO.INPUT)
	private ImagePlus img;
	
	@Parameter(type = ItemIO.OUTPUT)
	ImgPlus<T> imageConv;

	@Override
	public void run() {
		ops.convert().int8(image);
		imageConv = image.copy();
		
		//cs.run("morpion", true, "image", image, "threshold", 80);
		
		//Threshold<T> t = new Threshold<>(image);
		//imageConv = t.binarisation(threshold);
		//Binarisation b = new Binarisation(imageConv.getProcessor());
		//ImageProcessor ip = b.binariserImageAuto();
		//GFD gfd = new GFD(ip);
			
	}

}