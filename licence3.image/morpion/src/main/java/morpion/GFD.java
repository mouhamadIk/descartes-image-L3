package morpion;

import ij.process.ImageProcessor;

public class GFD {
	private ImageProcessor im;
	private float FI[][];
	private float FR[][];
	// private float GFD;
	private float GFD[][];
	private int lg;
	private final int t = 9;
	private final int r = 4;
	private int ht;

	/**
	 * Création d'un GFD
	 * 
	 * @param imageprocessor
	 */
	public GFD(ImageProcessor imageProcessor) {
		this.im = imageProcessor;
		lg = imageProcessor.getHeight();
		ht = imageProcessor.getWidth();
		this.GFD = new float[r][t];
		this.histo_GFD();

	}

	/**
	 * Obtenir le vecteur du GFD
	 * 
	 * @return
	 */
	public float[][] getGFD() {
		return GFD;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (int col1 = 0; col1 < GFD.length; col1++) {
			sb.append("[ ");
			for (int col2 = 0; col2 < GFD[col1].length; col2++) {
				sb.append(" " + GFD[col1][col2]);
			}
			sb.append(" ]");
		}
		return sb.toString();

	}

	/**
	 * cette fonction calcule la force des segments pour n niveaux de gris
	 * 
	 */
	// donnes ***
	public void histo_GFD() {
		FI = new float[50][50];
		FR = new float[50][50];

		// Calcul du barycentre (2)
		// System.out.println("Barycentre");
		int bx = 0;
		int by = 0;
		int nb_pt = 0;
		int i, j;

		// System.out.println( "taille : " + ht + " x " + lg);
		for (i = 0; i < ht; i++)
			for (j = 0; j < lg; j++) {
				// System.out.println("img " + i + " " + j + " " + im.get(i,j));
				if (im.get(i, j) < 20) {
					bx += j;
					by += i;
					nb_pt++;
				}
			}
		bx /= nb_pt; // image non vide
		by /= nb_pt;
		// System.out.println("MaxRad");
		// "plus rapide en creant une image tampon recentre sur le barycentre" (3)
		// Angle_max/centroid (MaxRad); (4)
		float MaxRad = -9999;
		// MaxRad=0;
		float RadTMP;
		for (i = 0; i < ht; i++)
			for (j = 0; j < lg; j++)
				if ((im.get(i, j)) < 50) {
					RadTMP = (float) Math.sqrt((j - bx) * (j - bx) + (i - by) * (i - by));
					if (RadTMP > MaxRad)
						MaxRad = RadTMP;

				}
		// MaxRad=sqrt(lg*ht)/2.;
		// System.out.println("MaxRad => " + MaxRad);
		// Polar Fourier Transform (5)
		int rad, ang;
		// int x, y;
		float radius, theta;
		// float DC;

		// Initialisation
		for (rad = 0; rad < r; rad++)
			for (ang = 0; ang < t; ang++)
				FR[rad][ang] = FI[rad][ang] = 0;
		// System.out.println("x => " + lg + ", y=> " + ht);
		// Pas dans les radians!
		for (rad = 0; rad < r; rad++) { // radial frequency - +1 d'apres alg
			for (ang = 0; ang < t; ang++) // angular frequency - idem
			{
				for (i = 0; i < ht; i++)
					for (j = 0; j < lg; j++)
						if (im.get(i, j) != 255) {
							// radius=sqrt((j-bx-MaxRad)*(j-bx-MaxRad)+(i-by-MaxRad)*(i-by-MaxRad));
							// theta = atan2f(i-by-MaxRad,j-bx-MaxRad);
							radius = (float) Math.sqrt((j - bx) * (j - bx) + (i - by) * (i - by));
							theta = (float) Math.atan2(i - by, j - bx);
							if (theta < 0)
								theta += 2 * Math.PI;
							FR[rad][ang] += Math.cos((2 * Math.PI * rad * radius / MaxRad) + ang * theta);
							FI[rad][ang] += Math.sin((2 * Math.PI * rad * radius / MaxRad) + ang * theta);
						}
			}
		}
		// Calcul de GFD
		// System.out.println("GFD\n");
		float Tval = 0;
		for (rad = 0; rad < r; rad++)
			for (ang = 0; ang < t; ang++) {
				if ((rad == 0) && (ang == 0)) {
					Tval = (float) Math.sqrt((FR[0][0] * FR[0][0] + FI[0][0] * FI[0][0]));
					GFD[0][0] = (float) (Math.sqrt((FR[0][0] * FR[0][0] + FI[0][0] * FI[0][0]))
							/ (Math.PI * MaxRad * MaxRad));
					// GFD = (float) (Math.sqrt((FR[0][0] * FR[0][0] + FI[0][0] * FI[0][0]))/
					// (Math.PI * MaxRad * MaxRad));
				} else {
					GFD[rad][ang] = (float) (Math.sqrt((FR[rad][ang] * FR[rad][ang] + FI[rad][ang] * FI[rad][ang]))
							/ Tval);
					// GFD += rad * t + ang;
					// GFD = (float) (Math.sqrt((FR[rad][ang] * FR[rad][ang] + FI[rad][ang] *
					// FI[rad][ang])) / Tval);
				}
			}
		// System.out.println("Fin calcul");
		// float cpt =0;
		// for(int col1 = 0; col1 < GFD.length; col1++) {
		// for (int col2 = 0; col2 < GFD[col1].length; col2++) {
		// System.out.println("GFD["+col1+"]["+col2+"] = " + GFD[col1][col2]);
		// //cpt+=GFD[col1][col2];
		// }
		// }
		// System.out.println(cpt);
		// for (i = 0; i < r * t; i++)
		// System.out.println("%d:%f " + i + " " + (GFD + i));

	}

	/**
	 * Calcule la distance euclidienne entre deux GFD ( float[][]) Comme la taille
	 * du vecteur GFD est fixe, les deux vecteurs sont de meme taille.
	 * 
	 * @param gfd
	 * @return la distance euclidienne entre les deux GFD
	 * 
	 */
	public float distance(GFD gfd) {
		float res = 0;
		for (int i = 0; i < this.GFD.length; i++) {
			for (int j = 0; j < this.GFD[i].length; j++) {
				res += (Math.pow(this.GFD[i][j] - gfd.getGFD()[i][j], 2));
			}
		}
		return (float) Math.sqrt(res);
	}

	public float mesureSimilarite(GFD gfd) {
		float min = 0, max = 0;
		for (int i = 0; i < this.GFD.length; i++) {
			for (int j = 0; j < this.GFD[i].length; j++) {
				min += Math.min(this.GFD[i][j], gfd.getGFD()[i][j]);
				max += Math.max(this.GFD[i][j], gfd.getGFD()[i][j]);
			}
		}
		return (min / max);
	}

}
