package amaturehour.androidtictactoe;

import java.util.Random;
import android.util.Log;

public class TicTacToeGame {
    //The computer difficulty level
    public enum DifficultyLevel {Easy, Medium, Hard};

    //Current difficulty level
    private DifficultyLevel mDifficultyLevel = DifficultyLevel.Medium;

    private static final String TAG = "TicTacToeGame";

    public static final int BOARD_SIZE = 9;
    private char[] mBoard = new char[BOARD_SIZE]; //This is the representation of the logic to represent the board

    //chars to represent what player occupies this place, and their turn
    public static final char HUMAN_PLAYER = 'X';
    public static final char COMPUTER_PLAYER = 'O';

    //*NEW* representation of the open spot on the board
    public static final char OPEN_SPOT = ' ';

    private Random mRand;

    public TicTacToeGame() {
        // Seed the random number generator
        mRand = new Random();
    }

    public DifficultyLevel getDifficultyLevel(){
        return mDifficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel){
        mDifficultyLevel = difficultyLevel;
    }

    /** Clear the board of all X's and O's by setting all spots to OPEN_SPOT. */
    public void clearBoard(){
        for (int i = 0; i < BOARD_SIZE; ++i){
            mBoard[i] = OPEN_SPOT;
        }
    }

    public char[] getBoardState(){
        return mBoard;
    }

    public void setBoardState(char[] charArray){
        for(int i = 0; i < BOARD_SIZE; ++i){
            mBoard[i] = charArray[i];
        }
    }

    public char getBoardOccupant(int pos){
        return mBoard[pos];
    }

    /**
     * Check for a winner and return a status value indicating who has won.
     * @return Return 0 if no winner or tie yet, 1 if it's a tie, 2 if X won,
     * or 3 if O won.
     */
    public int checkForWinner() {
        // Check horizontal wins
        for (int i = 0; i <= 6; i += 3)	{
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+1] == HUMAN_PLAYER &&
                    mBoard[i+2]== HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+1]== COMPUTER_PLAYER &&
                    mBoard[i+2] == COMPUTER_PLAYER)
                return 3;
        }

        // Check vertical wins
        for (int i = 0; i <= 2; i++) {
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+3] == HUMAN_PLAYER &&
                    mBoard[i+6]== HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+3] == COMPUTER_PLAYER &&
                    mBoard[i+6]== COMPUTER_PLAYER)
                return 3;
        }

        // Check for diagonal wins
        if ((mBoard[0] == HUMAN_PLAYER &&
                mBoard[4] == HUMAN_PLAYER &&
                mBoard[8] == HUMAN_PLAYER) ||
                (mBoard[2] == HUMAN_PLAYER &&
                        mBoard[4] == HUMAN_PLAYER &&
                        mBoard[6] == HUMAN_PLAYER))
            return 2;
        if ((mBoard[0] == COMPUTER_PLAYER &&
                mBoard[4] == COMPUTER_PLAYER &&
                mBoard[8] == COMPUTER_PLAYER) ||
                (mBoard[2] == COMPUTER_PLAYER &&
                        mBoard[4] == COMPUTER_PLAYER &&
                        mBoard[6] == COMPUTER_PLAYER))
            return 3;

        // Check for tie
        for (int i = 0; i < BOARD_SIZE; i++) {
            // If we find a number, then no one has won yet
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER)
                return 0;
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return 1;
    }

    /** Set the given player at the given location on the game board.
     * The location must be available, or the board will not be changed.
     *
     * @param player - The HUMAN_PLAYER or COMPUTER_PLAYER
     * @param location - The location (0-8) to place the move
     */
    public boolean setMove(char player, int location){
        if(mBoard[location] == OPEN_SPOT){
            mBoard[location] = player;
            return true;
        }
        return false;
    }

    public int getRandomMove(){
        int move;

        do
        {
            move = mRand.nextInt(BOARD_SIZE);
        } while (mBoard[move] == HUMAN_PLAYER || mBoard[move] == COMPUTER_PLAYER);

        Log.d(TAG, "Computer is moving to " + (move + 1));

        return move;
    }

    public int getWinningMove(){
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                char curr = mBoard[i];
                mBoard[i] = COMPUTER_PLAYER;
                if (checkForWinner() == 3) {
                    Log.d(TAG, "Computer is moving to " + (i + 1));
                    mBoard[i] = curr;
                    return i;
                }
                else
                    mBoard[i] = curr;
            }
        }
        return -1;
    }

    public int getBlockingMove(){
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                char curr = mBoard[i];   // Save the current number
                mBoard[i] = HUMAN_PLAYER;
                if (checkForWinner() == 2) {
                    Log.d(TAG, "Computer is moving to " + (i + 1));
                    mBoard[i] = curr;
                    return i;
                }
                else
                    mBoard[i] = curr;
            }
        }
        return -1;
    }

    /** Return the best move for the computer to make. You must call setMove()
     * to actually make the computer move to that location.
     * @return The best move for the computer to make (0-8).
     */
    public int getComputerMove()
    {
        int move = -1;

        if(mDifficultyLevel == DifficultyLevel.Easy){
            move = getRandomMove();
        }
        else if(mDifficultyLevel == DifficultyLevel.Medium){
            move = getWinningMove();
            if(move == -1){
                move = getRandomMove();
            }
        }
        else if(mDifficultyLevel == DifficultyLevel.Hard){
            // Try to win, but if that's not possible, block.
            // If that's not possible, move anywhere.
            move = getWinningMove();
            if(move == -1){
                move = getBlockingMove();
            }
            if(move == -1){
                move = getRandomMove();
            }
        }

        return move;
    }
}