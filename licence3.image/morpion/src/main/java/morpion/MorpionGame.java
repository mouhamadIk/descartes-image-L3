package morpion;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import ij.ImagePlus;
import ij.blob.Blob;
import scala.xml.dtd.EMPTY;

public class MorpionGame {

	public enum State {
		NOUGHT_WIN, CROSS_WIN, DRAW, ERROR, EMPTY, NOT_END;
	}

	public enum Symbol {
		NOUGHT, CROSS, UNKNOWN, EMPTY;
	}

	ImagePlus img;

	private static final int ROWS = 3, COLS = 3;

	private int noughtNumber = 0;
	private int crossNumber = 0;
	private State gameState;
	private Symbol[][] gameBoard = new Symbol[ROWS][COLS];

	MorpionGame(ArrayList<Blob> player1, ArrayList<Blob> player2, ArrayList<Blob> unknown, ImagePlus img) {
		this.img = img;
		System.out.println(img.getHeight() + " " + img.getWidth());
		noughtNumber = player1.size();
		crossNumber = player2.size();
		if (player1 != null) {
			for (Blob b : player1) {
				int position = getPositionInMorpion(b.getCenterOfGravity());
				gameBoard[position / 3][position % 3] = Symbol.NOUGHT;
				System.out.print("coord : " + b.getCenterOfGravity() + ", position = " + position + " ~ ["
						+ position % 3 + "][" + position / 3 + "], circ=" + b.getThinnesRatio());
				printCell(Symbol.NOUGHT);
				System.out.println();
			}
		}
		if (player2 != null) {
			for (Blob b : player2) {
				int position = getPositionInMorpion(b.getCenterOfGravity());
				System.out.print("coord : " + b.getCenterOfGravity() + ", position = " + position + " ~ ["
						+ position % 3 + "][" + position / 3 + "], circ=" + b.getThinnesRatio());
				printCell(Symbol.CROSS);
				System.out.println();
				gameBoard[position / 3][position % 3] = Symbol.CROSS;
			}
		}
		if (unknown != null) {
			for (Blob b : unknown) {
				int position = getPositionInMorpion(b.getCenterOfGravity());
				System.out.print("coord : " + b.getCenterOfGravity() + ", position = " + position + " ~ ["
						+ position % 3 + "][" + position / 3 + "], circ=" + b.getThinnesRatio());
				printCell(Symbol.UNKNOWN);
				System.out.println();
				gameBoard[position / 3][position % 3] = Symbol.UNKNOWN;
			}
		}

		for (int i = 0; i < 3; i++) {
			Symbol[] symbols = gameBoard[i];
			for (int j = 0; j < 3; j++) {
				Symbol symbol = symbols[j];
				if (symbol == null)
					gameBoard[i][j] = Symbol.EMPTY;
			}
		}

		gameState = getGameState();
	}

	public void setGameBoard(Symbol[][] gameBoard) {
		this.gameBoard = gameBoard;
	}

	public State getGameState() {

		if (noughtNumber > crossNumber + 1 || crossNumber > noughtNumber + 1)
			return State.ERROR;

		boolean wonFlag = false;
		Symbol wonSymbol = Symbol.UNKNOWN, s;
		// run through rows
		for (int i = 0; i < ROWS; i++) {
			s = gameBoard[i][0];
			if (s == Symbol.EMPTY || s == Symbol.UNKNOWN)
				continue;
			if (s == gameBoard[i][1] && s == gameBoard[i][2]) {
				if (wonFlag)
					return State.ERROR;
				wonFlag = true;
				wonSymbol = s;
			}
		}
		// run through cols
		for (int j = 0; j < COLS; j++) {
			s = gameBoard[0][j];
			if (s == Symbol.EMPTY || s == Symbol.UNKNOWN)
				continue;
			if (s == gameBoard[1][j] && s == gameBoard[2][j]) {
				if (wonFlag)
					return State.ERROR;
				wonFlag = true;
				wonSymbol = s;
			}
		}
		// diagonals
		s = gameBoard[1][1];
		if (s != Symbol.EMPTY && s != Symbol.UNKNOWN
				&& ((s == gameBoard[0][0] && s == gameBoard[2][2]) || s == gameBoard[2][0] && s == gameBoard[0][2])) {
			if (wonFlag)
				return State.ERROR;
			wonFlag = true;
			wonSymbol = s;
		}

		if (wonFlag == false) {
			if (noughtNumber + crossNumber == COLS * ROWS) {
				return State.DRAW;
			}
			return State.NOT_END;
		} else {
			if (wonSymbol == Symbol.NOUGHT)
				return State.NOUGHT_WIN;
			else if (wonSymbol == Symbol.CROSS)
				return State.CROSS_WIN;
		}
		return null;
	}

	/**
	 * 
	 * 0 | 1 | 2 ----------- 3 | 4 | 5 ----------- 6 | 7 | 8
	 * 
	 */

	private int getPositionInMorpion(Point2D p) {
		int h = img.getHeight();
		int w = img.getWidth();

		return (int) (3 * p.getX() / w) + 3 * (int) (p.getY() / (h / 3));
	}

	public void printBoard() {
		System.out.println("Image : " + img.getTitle());
		for (int row = 0; row < ROWS; ++row) {
			for (int col = 0; col < COLS; ++col) {
				printCell(gameBoard[row][col]); // print each of the cells
				if (col != COLS - 1) {
					System.out.print("│"); // print vertical partition
				}
			}
			System.out.println();
			if (row != ROWS - 1) {
				System.out.println("╌╌╌┼╌╌╌┼╌╌╌"); // print horizontal partition
			}
		}
		System.out.println();
		State state = this.gameState;
		switch (state) {
		case NOUGHT_WIN:
			System.out.println("Joueur 1 won.");
			break;
		case CROSS_WIN:
			System.out.println("Joueur 2 won.");
			break;
		case DRAW:
			System.out.println("Draw.");
			break;
		case EMPTY:
			System.out.println("Board is empty.");
			break;
		case ERROR:
			System.out.println("Game is not coherent.");
			break;
		case NOT_END:
			System.out.println("Game not end");
			break;
		default:
			System.out.println("Game is not coherent.");
			break;
		}
	}

	/** Print a cell with the specified "content" */
	public static void printCell(Symbol content) {
		switch (content) {
		case EMPTY:
			System.out.print("   ");
			break;
		case NOUGHT:
			System.out.print(" O ");
			break;
		case CROSS:
			System.out.print(" X ");
			break;
		case UNKNOWN:
			System.out.print(" * ");
			break;
		default:
			System.out.print("   ");
			break;
		}
	}

}
