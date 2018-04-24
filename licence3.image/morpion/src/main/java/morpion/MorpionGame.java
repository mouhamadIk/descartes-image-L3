package morpion;

public class MorpionGame {

	enum State {
		NOUGHT_WIN, CROSS_WIN, DRAW, ERROR, EMPTY, NOT_END;
	}

	enum Symbol {
		NOUGHT, CROSS, UNKNOWN, EMPTY;
	}

	private final int ROWS = 3, COLS = 3;

	private int noughtNumber;
	private int crossNumber;

	private Symbol[][] gameBoard = new Symbol[ROWS][COLS];

	public int getNoughtNumber() {
		return noughtNumber;
	}

	public void setNoughtNumber(int noughtNumber) {
		this.noughtNumber = noughtNumber;
	}

	public int getCrossNumber() {
		return crossNumber;
	}

	public void setCrossNumber(int crossNumber) {
		this.crossNumber = crossNumber;
	}

	public Symbol[][] getGameBoard() {
		return gameBoard;
	}

	public void setGameBoard(Symbol[][] gameBoard) {
		this.gameBoard = gameBoard;
	}

	public State getGameState() {
	
		if (noughtNumber > crossNumber + 1 || crossNumber > noughtNumber + 1) return State.ERROR;
		
		bool wonFlag = false;
		Symbol wonSymbol = Symbol.UNKNOWN, s;
		// run through rows
		for (int i = 0; i < ROWS; i++) {
			s = gameBoard[i][0];
			if (s == Symbol.EMPTY || s == Symbol.UNKNOWN)
				continue;
			if (s == gameBoard[i][1] && s == gameBoard[i][2]) {
				if (wonFlag)
					return State.ERROR;
				wonFlag = 1;
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
				wonFlag = 1;
				wonSymbol = s;
			}
		}
		// diagonals
		s = gameBoard[1][1];
		if (s != Symbol.EMPTY && s != Symbol.UNKNOWN
				&& ((s == gameBoard[0][0] && s == gameBoard[2][2]) || s == gameBoard[2][0] && s == gameBoard[0][2])) {
			if (wonFlag)
				return State.ERROR;
			wonFlag = 1;
			wonSymbol = s;
		}

		if (wonFlag == 0) {
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
	}
}
