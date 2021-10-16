package fr.isep.ii3510.assignment2__hangman_kyleliuxianqi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * @Author: LIU Xianqi 61551
 */

public class MainActivity extends AppCompatActivity {

    private String word;
    private int counter;
    private char[] wordArray;
    private Map<String,String> wordMap;
    TextView tv_word, tv_clue, tv_counter;
    ImageView iv_hangman;
    Button btn_restart;
    LinearLayout ll_keyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_word = findViewById(R.id.tv_word);
        tv_clue = findViewById(R.id.tv_clue);
        tv_counter = findViewById(R.id.tv_counter);
        btn_restart = findViewById(R.id.btn_restart);
        iv_hangman = findViewById(R.id.iv_hangman);
        iv_hangman.setImageResource(R.drawable.hangman10);

        ll_keyboard = findViewById(R.id.keyboard);

        //Use string array to get each line's values on the keyboard
        String[] kb_line1_val = getResources().getStringArray(R.array.keyboard_line1_val);
        String[] kb_line2_val = getResources().getStringArray(R.array.keyboard_line2_val);
        String[] kb_line3_val = getResources().getStringArray(R.array.keyboard_line3_val);

        createKeyboard(ll_keyboard, kb_line1_val, kb_line1_val.length);
        createKeyboard(ll_keyboard, kb_line2_val, kb_line2_val.length);
        createKeyboard(ll_keyboard, kb_line3_val, kb_line3_val.length);

        readFile();
        gameStart();
    }

    //Read the words and clues from csv file
    private void readFile() {
        Scanner sc = new Scanner(getResources().openRawResource(R.raw.words_and_clues));
        if (wordMap == null) {
            wordMap = new HashMap<>();
        }
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            //Due to the csv format as [word,clue], so use ',' as the index to get substring as word and clue
            String word = line.substring(0, line.indexOf(','));
            String clue = line.substring(line.indexOf(',')+1);
            wordMap.put(word.toUpperCase(), clue);
        }
        sc.close();
    }

    public void gameStart(){
        iv_hangman.setImageResource(R.drawable.hangman10);
        word = getRandomWord();
        wordArray = word.toCharArray();

        //Build a hidden string which has the same length as the word
        StringBuilder wordHidden = new StringBuilder();
        for(int i = 0; i < word.length(); i++){
            if(wordArray[i] == ' '){
                wordHidden.append(" "); //some word like 'horse riding' contain a blank space
            } else {
                wordHidden.append("_");
            }
        }

        tv_word.setText(wordHidden.toString());
        tv_clue.setText(String.format(getString(R.string.clue), wordMap.get(word)));
        counter = 10; //User has 10 lives at the beginning
    }

    public void createKeyboard(LinearLayout ll, String[] kb_line_val, int kb_line_val_num){
        //create line
        LinearLayout kb_line = new LinearLayout(this);
        LinearLayout.LayoutParams kb_line_lp = new LinearLayout.LayoutParams(-1,-2,0);
        kb_line.setBackgroundColor(ContextCompat.getColor(this,R.color.light_black));
        ll.addView(kb_line, kb_line_lp); //Add the line for keyboard

        //create btn
        for(int i=0; i<kb_line_val_num; i++){
            Button btn_key = new Button(this);
            LinearLayout.LayoutParams btn_key_lp = new LinearLayout.LayoutParams(-2,-2,1);
            btn_key.setText(kb_line_val[i]);
            btn_key.setOnClickListener(this::onClickKeyboard);
            kb_line.addView(btn_key,btn_key_lp);//Add the keyboard button for the line
        }
    }

    public void onClickKeyboard(View v){

        Button btn = (Button) v;
        String input = btn.getText().toString();
        StringBuilder str = new StringBuilder();
        char letter = input.charAt(0);

        if(word.contains(input)){
            char[] wordHiddenArray = tv_word.getText().toString().toCharArray();
            for(int i = 0; i < wordArray.length; i++){
                if(wordArray[i] == letter){
                    wordHiddenArray[i] = letter;
                }
                str.append(wordHiddenArray[i]);
            }

            //change background and be disabled to prevent further unintentionally clicks
            btn.setEnabled(false);
            btn.setBackgroundColor(getColor(R.color.light_black));
            tv_word.setText(str.toString());

            if(str.toString().equals(word)){
                //Use alert dialog to display successful message
                new AlertDialog.Builder(this)
                        .setTitle("🎉Congratulations!")
                        .setMessage("😊Amazing! You found the word!")
                        .create().show();
                gameOver();
            }
        }else{
            if(counter>0){
                counter--;
                tv_counter.setText(String.format(getString(R.string.count),counter));

                //when user lose one life, then exchange the img
                int imgId = getResources().getIdentifier("hangman" + counter, "drawable", getPackageName());
                iv_hangman.setImageResource(imgId);

                //change background and be disabled to prevent further unintentionally clicks
                btn.setEnabled(false);
                btn.setBackgroundColor(Color.RED);
            }
            if(counter==0){
                //Use toast to display failed message
                Toast.makeText(this, "😭Sorry! You didn't find the word! Try again!", Toast.LENGTH_LONG).show();
                gameOver();
                tv_word.setText(word);
            }
        }
    }

    //randomly choose a word with its clue in this map
    private String getRandomWord(){
        Random random = new Random();
        String[] keys = wordMap.keySet().toArray(new String[0]);
        return keys[random.nextInt(keys.length)];
    }

    private void gameOver(){
        //When user passed or failed the game, set the restart button visible
        btn_restart.setVisibility(View.VISIBLE);
    }

    public void onClickRestart(View v) {
        finish();
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

}