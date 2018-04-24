package morpion;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.command.CommandService;
import org.scijava.convert.ConvertService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.ImagePlus;
import ij.plugin.Duplicator;
import ij.process.BinaryProcessor;
import ij.process.ByteProcessor;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import net.imagej.Dataset;
import net.imagej.ImgPlus;
import net.imagej.ops.OpService;
import net.imagej.ops.Ops.Image;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import projetL3.traitement.Blob;
import projetL3.traitement.ManyBlobs;

@Plugin(type = Command.class, name = "morpion", menuPath = "Plugins>Morpion")
public class Morpion<T extends RealType<T>> implements Command {

	@Parameter
	CommandService cs;
	
	@Parameter
	OpService ops;
	
//	@Parameter(persist = false)
//	ImgPlus<T> image;

//	@Parameter(required = false)
//	int threshold = 80;

//	@Parameter(type = ItemIO.INPUT)
//	private ImgPlus<T> img;
	
//	@Parameter(type = ItemIO.OUTPUT)
//	ImgPlus<T> imageConv;
	
	@Parameter
	ConvertService conv;

	@Parameter
	Dataset inputImage;

	@Parameter(type = ItemIO.OUTPUT)
	ImagePlus output;

	@Override
	public void run() {
		ImagePlus image = convertInputToImagePlus();
		Duplicator dupli = new Duplicator();
		
		ImagePlus image_thresholded = dupli.run(image);
		image_thresholded.getProcessor().autoThreshold();
		
		
		//ImagePlus image_skeletonized = skeletonize(dupli.run(image_thresholded));
		
		//ImagePlus image_convolved = convolve(dupli.run(image_skeletonized));
		
		//image_convolved.getProcessor().threshold(127);
		
		ImagePlus image_grille = getLargestConnectedComponants(dupli.run(image_thresholded));
		
		output = image_grille;
	}

	private ImagePlus convertInputToImagePlus() {
		ImagePlus imp = conv.convert(inputImage, ImagePlus.class);
		ImageConverter c = new ImageConverter(imp);
		c.convertToGray8();
		imp = imp.duplicate();
		return imp;
	}
	
	private ImagePlus skeletonize(ImagePlus imp) {
		ByteProcessor pr = (ByteProcessor) imp.getProcessor().convertToByte(true);
		BinaryProcessor binPr = new BinaryProcessor(pr);
		binPr.skeletonize();
		
		return imp;
	}
	
	private ImagePlus convolve(ImagePlus imp) {
		ByteProcessor pr = (ByteProcessor) imp.getProcessor().convertToByte(true);
		int[] kernel = {1,1,1,1,1,1,1,1,1};
		BinaryProcessor binPr = new BinaryProcessor(pr);
		binPr.convolve3x3(kernel);
		
		return imp;
	}
	
	private ImagePlus getLargestConnectedComponants(ImagePlus imp) {
		ManyBlobs manyBlobs = new ManyBlobs(imp);
		manyBlobs.findConnectedComponents();
		System.out.println(manyBlobs.size());
		Blob greaterBlob = manyBlobs.get(0); 
		for (Blob blob : manyBlobs) {
			if (greaterBlob.getPerimeter() < blob.getPerimeter())
				greaterBlob = blob;
		}
		imp = Blob.generateBlobImage(greaterBlob);
		
		return imp;
	}
	
	private ImagePlus getLines(ImagePlus imp) {
		ByteProcessor pr = (ByteProcessor) imp.getProcessor().convertToByte(true);
		int[] kernel = {1,0,-1,2,0,-2,1,0,-1};
		BinaryProcessor binPr = new BinaryProcessor(pr);
		binPr.convolve3x3(kernel);
		
		return imp;
	}

//	@Override
//	public void run() {
//		ImagePlus imp = convertInputToImagePlus();
//		ops.convert().int8(image);
//		ops.threshold().huang(image);
//		imageConv = image.copy();
//		
//		//cs.run("morpion", true, "image", image, "threshold", 80);
//		
//		//Threshold<T> t = new Threshold<>(image);
//		//imageConv = t.binarisation(threshold);
//		//Binarisation b = new Binarisation(imageConv.getProcessor());
//		//ImageProcessor ip = b.binariserImageAuto();
//		//GFD gfd = new GFD(ip);
//			
//	}

}