package amaturehour.androidtictactoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class AndroidTicTacToe extends Activity {

    // Represents the internal state of the game
    private TicTacToeGame mGame;
    private BoardView mBoardView;
    // Various text displayed
    private TextView mInfoTextView;
    private TextView mHumanText;
    private TextView mTiesText;
    private TextView mAndroidText;
    private boolean mGameOver;
    private boolean mSoundsOn;
    private boolean mHumanTurnFirst;
    private boolean mHumanTurn;
    private int mNumWins;
    private int mNumLoses;
    private int mNumTies;

    //For all sounds we play
    private SoundPool mSounds;
    private int mHumanMoveSoundID;
    private int mComputerMoveSoundID;
    private int mHumanWinSoundID;
    private int mComputerWinSoundID;
    private int mTieSoundID;

    static final int DIALOG_QUIT_ID = 1;
    static final int DIALOG_ABOUT_ID = 2;
    static final int DIALOG_RESET_ID = 3;
    static final String TAG = "AndroidTicTacToe";

    private SharedPreferences mPrefs;

    Handler handler = new Handler();

    private void playSound(int resID){
        if(mSoundsOn){
            mSounds.play(resID, 1, 1, 1, 0, 1);
        }
    }

    //Listen for touches on the board
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //Determine which cell was touched
            int col = (int)event.getX()/mBoardView.getBoardCellWidth();
            int row = (int)event.getY()/mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            if(!mGameOver && mHumanTurn && setMove(TicTacToeGame.HUMAN_PLAYER, pos)){
                mHumanTurn = false;
                int winner = mGame.checkForWinner();

                if(winner == 0){
                    mInfoTextView.setText(R.string.turn_computer);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int move = mGame.getComputerMove();
                            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                            mHumanTurn = true;
                            int winner = mGame.checkForWinner();
                            if(winner == 0){
                                mInfoTextView.setText(R.string.turn_human);
                            }
                            else if(winner == 1){
                                mGameOver = true;
                                ++mNumTies;
                                String defaultMessage = getResources().getString(R.string.result_tie);
                                mTiesText.setText(mNumTies + "");
                                mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
                                playSound(mTieSoundID);
                            }
                            else if(winner == 2){
                                mGameOver = true;
                                ++mNumWins;
                                String defaultMessage = getResources().getString(R.string.result_human_wins);
                                mHumanText.setText(mNumWins + "");
                                mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
                                playSound(mHumanWinSoundID);
                            }
                            else{
                                mGameOver = true;
                                ++mNumLoses;
                                String defaultMessage = getResources().getString(R.string.result_computer_wins);
                                mAndroidText.setText(mNumLoses + "");
                                mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
                                playSound(mComputerWinSoundID);
                            }
                        }
                    }, 750);
                }
                else if(winner == 1){
                    mGameOver = true;
                    ++mNumTies;
                    String defaultMessage = getResources().getString(R.string.result_tie);
                    mTiesText.setText(mNumTies + "");
                    mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
                    playSound(mTieSoundID);
                }
                else if(winner == 2){
                    mGameOver = true;
                    ++mNumWins;
                    String defaultMessage = getResources().getString(R.string.result_human_wins);
                    mHumanText.setText(mNumWins + "");
                    mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
                    playSound(mHumanWinSoundID);
                }
                else{
                    mGameOver = true;
                    ++mNumLoses;
                    String defaultMessage = getResources().getString(R.string.result_computer_wins);
                    mAndroidText.setText(mNumLoses + "");
                    mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
                    playSound(mComputerWinSoundID);
                }
            }
            return false;
        }
    };

    protected Dialog onCreateDialog(int id){
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (id){
            case DIALOG_RESET_ID:
                builder.setMessage(R.string.reset_question).setCancelable(false).setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mNumTies = 0;
                                mNumLoses = 0;
                                mNumWins = 0;

                                displayScores();
                            }
                        }).setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;
            case DIALOG_QUIT_ID:
                builder.setMessage(R.string.quit_question).setCancelable(false).setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AndroidTicTacToe.this.finish();
                            }
                        }).setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;
            case DIALOG_ABOUT_ID:
                Context context = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.about_dialog, null);
                builder.setView(layout);
                builder.setPositiveButton(R.string.ok, null);
                dialog = builder.create();
                break;
            default:
                break;
        }

        return dialog;
    }

    private boolean setMove(char player, int location){
        if(player == TicTacToeGame.COMPUTER_PLAYER){
            playSound(mComputerMoveSoundID);
            mBoardView.invalidate();
        }

        if(mGame.setMove(player, location)){
            if(player == TicTacToeGame.HUMAN_PLAYER){
                playSound(mHumanMoveSoundID);
            }

            mBoardView.invalidate();
            return true;
        }

        return false;
    }

    private void displayScores(){
        mHumanText.setText(Integer.toString(mNumWins));
        mAndroidText.setText(Integer.toString(mNumLoses));
        mTiesText.setText(Integer.toString(mNumTies));
    }

    private void startComputerDelay(){
        //If it's the computer's turn, the previous turn was not completed, so go again
        if(!mGameOver && !mHumanTurn){
            int move = mGame.getComputerMove();
            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
            mInfoTextView.setText(R.string.turn_human);
            mHumanTurn = true;
        }
    }

    //Set up the game board
    private void startNewGame(){
        mGame.clearBoard();
        mBoardView.invalidate();

        mHumanTurnFirst = !mHumanTurnFirst;
        mHumanTurn = mHumanTurnFirst;

        mGameOver = false;

        if(!mHumanTurnFirst){
            mInfoTextView.setText(R.string.first_computer);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    mInfoTextView.setText(R.string.turn_human);
                    mHumanTurn = true;
                }
            }, 750);
        }
        else {
            //indicating that the human goes first
            mInfoTextView.setText(R.string.first_human);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_android_tic_tac_toe);
        }
        else{
            setContentView(R.layout.activity_android_tic_tac_toe_land);
        }

        getActionBar().setDisplayShowTitleEnabled(false);

        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        mSoundsOn = mPrefs.getBoolean("sound", true);
        //////////////////

        mGame = new TicTacToeGame();
        mBoardView = (BoardView)findViewById(R.id.board);
        mBoardView.setGame(mGame);

        //Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);

        mNumLoses = mPrefs.getInt("mNumLoses", 0);
        mNumTies = mPrefs.getInt("mNumTies", 0);
        mNumWins = mPrefs.getInt("mNumWins", 0);

        mInfoTextView = (TextView)findViewById(R.id.information);
        mHumanText = (TextView)findViewById(R.id.human_score);
        mTiesText = (TextView)findViewById(R.id.ties_score);
        mAndroidText = (TextView)findViewById(R.id.android_score);

        mHumanText.setText(Integer.toString(mNumWins));
        mTiesText.setText(Integer.toString(mNumTies));
        mAndroidText.setText(Integer.toString(mNumLoses));


        if(savedInstanceState == null){
            mHumanTurnFirst = false;
            startNewGame();
        }
        else{
            mGame.setBoardState(savedInstanceState.getCharArray("mBoard"));
            mGameOver = savedInstanceState.getBoolean("mGameOver");
            mHumanTurn = savedInstanceState.getBoolean("mHumanTurn");
            mHumanTurnFirst = savedInstanceState.getBoolean("mHumanTurnFirst");
            mInfoTextView.setText(savedInstanceState.getCharSequence("mInfoTextView"));
            mGame.setDifficultyLevel((TicTacToeGame.DifficultyLevel)savedInstanceState.getSerializable("mDifficulty"));


            displayScores();
            startComputerDelay();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSounds = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);

        mHumanMoveSoundID = mSounds.load(this, R.raw.human_move, 1);

        mComputerMoveSoundID = mSounds.load(this, R.raw.computer_move, 1);

        mHumanWinSoundID = mSounds.load(this, R.raw.human_wins, 1);

        mComputerWinSoundID = mSounds.load(this, R.raw.computer_wins, 1);

        mTieSoundID = mSounds.load(this, R.raw.tie, 1);

    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "in on Pause");

        if(mSounds != null){
            mSounds.release();
            mSounds = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        //Save the current scores
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("mNumWins", mNumWins);
        ed.putInt("mNumLoses", mNumLoses);
        ed.putInt("mNumTies", mNumTies);
        ed.apply();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharArray("mBoard", mGame.getBoardState());
        outState.putBoolean("mGameOver", mGameOver);
        outState.putCharSequence("mInfoTextView", mInfoTextView.getText());
        outState.putBoolean("mHumanTurn", mHumanTurn);
        outState.putBoolean("mHumanTurnFirst", mHumanTurnFirst);
        outState.putSerializable("mDifficulty", mGame.getDifficultyLevel());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mGame.setBoardState(savedInstanceState.getCharArray("mBoard"));
        mGameOver = savedInstanceState.getBoolean("mGameOver");
        mHumanTurn = savedInstanceState.getBoolean("mHumanTurn");
        mHumanTurnFirst = savedInstanceState.getBoolean("mHumanTurnFirst");
        mInfoTextView.setText(savedInstanceState.getCharSequence("mInfoTextView"));
        mGame.setDifficultyLevel((TicTacToeGame.DifficultyLevel)savedInstanceState.getSerializable("mDifficulty"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_CANCELED){
            //Apply potentially new settings
            mSoundsOn = mPrefs.getBoolean("sound", true);
            String[] levels = getResources().getStringArray(R.array.list_difficulty_level);

            //set difficulty, or use hardest if not present
            String difficultyLevel = mPrefs.getString("difficulty_level", levels[levels.length - 1]);
            int i = 0;
            while (i < levels.length){
                if(difficultyLevel.equals(levels[i])){
                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.values()[i]);
                    i = levels.length;
                }
                ++i;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_android_tic_tac_toe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.setting:
                startActivityForResult(new Intent(this, Settings.class), 0);
                return true;
            case R.id.reset:
                showDialog(DIALOG_RESET_ID);
                return true;
            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                return true;
            case R.id.about:
                showDialog(DIALOG_ABOUT_ID);
                return true;
            default:
                return false;
        }
    }
}
