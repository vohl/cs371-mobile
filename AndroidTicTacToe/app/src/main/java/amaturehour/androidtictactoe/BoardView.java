package amaturehour.androidtictactoe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by vohl on 12/21/14.
 */
public class BoardView extends View{
    //Width of the board grid lines
    public static final int GRID_LINE_WIDTH = 6;

    private Bitmap mHumanBitmap;
    private Bitmap mComputerBitmap;

    private Paint mPaint;

    private TicTacToeGame mGame;

    public void setGame(TicTacToeGame game){
        mGame = game;
    }

    public int getBoardCellWidth(){
        return getWidth()/3;
    }

    public int getBoardCellHeight(){
        return getHeight()/3;
    }

    public void initialize(){
        mHumanBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.red_x);
        mComputerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.red_o);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public BoardView(Context context){
        super(context);
        initialize();
    }

    public BoardView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        initialize();
    }

    public BoardView(Context context, AttributeSet attrs){
        super(context, attrs);
        initialize();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Determine the width and height of the View
        int boardWidth = getWidth();
        int boardHeight = getHeight();

        //Make thick, light gray lines
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(GRID_LINE_WIDTH);

        //Draw the vertical and horizontal board lines
        canvas.drawLine(getBoardCellWidth(), 0, getBoardCellWidth(), boardHeight, mPaint);
        canvas.drawLine(2*getBoardCellWidth(), 0, 2*getBoardCellWidth(), boardHeight, mPaint);
        canvas.drawLine(0, getBoardCellHeight(), boardWidth, getBoardCellHeight(), mPaint);
        canvas.drawLine(0, 2*getBoardCellHeight(), boardWidth, 2*getBoardCellHeight(), mPaint);

        //Draw all the X and O images
        for(int i = 0; i < TicTacToeGame.BOARD_SIZE; ++i){
            int col = i%3;
            int row = i/3;

            //Define the boundaries
            int xTopLeft = col*getBoardCellWidth() + 3;
            int yTopLeft = row*getBoardCellHeight() + 3;
            int xBottomRight = (col+1)*getBoardCellWidth() - 3;
            int yBottomRight = (row+1)*getBoardCellHeight() - 3;

            if(mGame != null && mGame.getBoardOccupant(i) == TicTacToeGame.HUMAN_PLAYER){
                canvas.drawBitmap(mHumanBitmap, null, new Rect(xTopLeft, yTopLeft, xBottomRight, yBottomRight), null);
            }
            else if(mGame != null && mGame.getBoardOccupant(i) == TicTacToeGame.COMPUTER_PLAYER){
                canvas.drawBitmap(mComputerBitmap, null, new Rect(xTopLeft, yTopLeft, xBottomRight, yBottomRight), null);
            }
        }
    }
}
