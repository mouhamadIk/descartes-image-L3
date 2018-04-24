
package morpion;

import ij.ImagePlus;

public class HoughTransform {

	private ImagePlus img;

	public HoughTransform(ImagePlus img) {
		super();
		this.img = img;
	}

	public ArrayData houghTransform(int thetaAxisSize, int rAxisSize, int minContrast) {
		int width = img.getWidth();
		int height = img.getHeight();
		int maxRadius = (int) Math.ceil(Math.hypot(width, height));
		int halfRAxisSize = rAxisSize >>> 1;
		ArrayData outputData = new ArrayData(thetaAxisSize, rAxisSize);
		// x output ranges from 0 to pi
		// y output ranges from -maxRadius to maxRadius
		double[] sinTable = new double[thetaAxisSize];
		double[] cosTable = new double[thetaAxisSize];
		for (int theta = thetaAxisSize - 1; theta >= 0; theta--) {
			double thetaRadians = theta * Math.PI / thetaAxisSize;
			sinTable[theta] = Math.sin(thetaRadians);
			cosTable[theta] = Math.cos(thetaRadians);
		}

		for (int y = height - 1; y >= 0; y--) {
			for (int x = width - 1; x >= 0; x--) {
				if (contrast(x, y, minContrast)) {
					for (int theta = thetaAxisSize - 1; theta >= 0; theta--) {
						double r = cosTable[theta] * x + sinTable[theta] * y;
						int rScaled = (int) Math.round(r * halfRAxisSize / maxRadius) + halfRAxisSize;
						outputData.accumulate(theta, rScaled, 1);
					}
				}
			}
		}
		return outputData;
	}

	public boolean contrast(int x, int y, int minContrast) {
		int[] centerValue2 = img.getPixel(x, y);
		int centerValue = centerValue2[0];
		for (int i = 8; i >= 0; i--) {
			if (i == 4)
				continue;
			int newx = x + (i % 3) - 1;
			int newy = y + (i / 3) - 1;
			if ((newx < 0) || (newx >= img.getWidth()) || (newy < 0) || (newy >= img.getHeight()))
				continue;

			int[] newPixel2 = img.getPixel(newx, newy);
			int newPixel = newPixel2[0];

			if (Math.abs(newPixel - centerValue) >= minContrast)
				return true;
		}
		return false;
	}

	public static class ArrayData {
		public final int[] dataArray;
		public final int width;
		public final int height;

		public ArrayData(int width, int height) {
			this(new int[width * height], width, height);
		}

		public ArrayData(int[] dataArray, int width, int height) {
			this.dataArray = dataArray;
			this.width = width;
			this.height = height;
		}

		public int get(int x, int y) {
			return dataArray[y * width + x];
		}

		public void set(int x, int y, int value) {
			dataArray[y * width + x] = value;
		}

		public void accumulate(int x, int y, int delta) {
			set(x, y, get(x, y) + delta);
		}

		public int getMax() {
			int max = dataArray[0];
			for (int i = width * height - 1; i > 0; i--)
				if (dataArray[i] > max)
					max = dataArray[i];
			return max;
		}
	}

}