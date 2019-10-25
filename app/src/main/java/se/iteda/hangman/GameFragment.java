package se.iteda.hangman;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Scanner;

public class GameFragment extends Fragment {

    private TextView tvWord;
    private String word;
    private String wordDisplay;
    private char[] wordDisplayArray;

    private ArrayList<String> wordsList;
    private EditText edtTxtInput;
    private TextView lettersTriedTv;
    private String lettersTried;
    private final String MESSAGE_WITH_LETTERS_TRIED = "Letters tried: ";
    private TextView tvTriesLeft;
    private int triesLeft;
    private final String WINNING_MESSAGE = "You won";
    private final String LOSING_MESSAGE = "You Lost";

    public GameFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Toolbar mToolbar = view.findViewById(R.id.toolbarID);
        ((MainActivity)getActivity()).setSupportActionBar(mToolbar);
        mToolbar.setTitle("Playing le game");
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        wordsList = new ArrayList<>();
        Button resetButton = view.findViewById(R.id.btnResetID);
        tvWord = view.findViewById(R.id.tvWordID);
        edtTxtInput = view.findViewById(R.id.playerInputID);
        lettersTriedTv = view.findViewById(R.id.tvLettersGuessedID);
        tvTriesLeft = view.findViewById(R.id.tvTriesLeftID);

        final String IMAGE_START = "app/src/main/res/drawable-v24/tom.png";
        final String IMAGE_FIRST = "";
        final String IMAGE_SECOND = "";
        final String IMAGE_THIRD = "";
        final String IMAGE_FOURTH = "";
        final String IMAGE_FIFTH = "";
        final String IMAGE_DEAD = "";

        ImageView imageStart = view.findViewById(R.id.imgVHangManID);
        ImageView imageFirst = view.findViewById(R.id.imgFirsErrorID);
        ImageView imageSecond = view.findViewById(R.id.imgSecondErrorID);
        ImageView imageThird = view.findViewById(R.id.imgThirdErrorID);
        ImageView imageFouth = view.findViewById(R.id.imgFourthErrorID);
        ImageView imageFifth = view.findViewById(R.id.imgFifthErrorID);
        ImageView imageDead = view.findViewById(R.id.imgDeadErrorID);

        downloadAndSetImage(IMAGE_START, imageStart);
        readWords();
        initializeGame();

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame(v);
            }
        });

        edtTxtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //If the field is ! null
                if (s.length() != 0) {
                    checkIfLetterIsInWord(s.charAt(0));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void resetGame(View v) {
        initializeGame();
        edtTxtInput.setEnabled(true);
    }

    private void checkIfLetterIsInWord(char letter) {
        if (word.indexOf(letter) >= 0) {
            if (wordDisplay.indexOf(letter) < 0) {
                revealLetterInWord(letter);

                displayWordOnScreen();

                //Player won
                if (!wordDisplay.contains("_")) {
                    tvTriesLeft.setText(WINNING_MESSAGE);
                }
            }
        }
        else {
            decreaseAndDisplayTriesLeft();

            if (triesLeft == 0) {
                tvTriesLeft.setText(LOSING_MESSAGE);
                tvWord.setText(word);
                edtTxtInput.setEnabled(false);
            }
        }

        //Repeated letter input
        if (lettersTried.indexOf(letter) < 0) {
            lettersTried += letter + ", ";
            String messageToBeDisplayed = MESSAGE_WITH_LETTERS_TRIED + lettersTried;
            lettersTriedTv.setText(messageToBeDisplayed);
        }
    }

    private void decreaseAndDisplayTriesLeft() {
        if (triesLeft > 0) {
            triesLeft--;
            tvTriesLeft.setText(String.valueOf(triesLeft));
            //TODO add images in switch to set visuals

        }
    }

    private void readWords() {
        InputStream mInputStream = null;
        Scanner in = null;
        String aWord;

        try {
            if (Locale.getDefault().getLanguage().equals("sv")) {
                mInputStream = getContext().getAssets().open("words_SE.txt");
            }
            else {
                mInputStream = getContext().getAssets().open("words_EN.txt");
            }

            in = new Scanner(mInputStream);
            while(in.hasNext()) {
                aWord = in.next();
                wordsList.add(aWord);
            }
        } catch (IOException e) {
            Toast.makeText(getActivity(), e.getClass().getSimpleName() + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        finally {
            if (in != null) {
                in.close();
            }
            try {
                if (mInputStream != null) {
                    mInputStream.close();
                }
            } catch (IOException e) {
                Toast.makeText(getActivity(), e.getClass().getSimpleName() + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initializeGame() {
        //Shuffle, get a word, set the word and remove the word from the list
        Collections.shuffle(wordsList);
        word = wordsList.get(0);

        //Set the chars in the word to the charArray
        wordDisplayArray = word.toCharArray();

        //Add the underscores before letter is guessed
        for (int i = 0; i < wordDisplayArray.length; i++) {
            wordDisplayArray[i] = '_';
        }

        //Initialize a string from the chars, used to search for letters
        wordDisplay = String.valueOf(wordDisplayArray);

        //Display word
        displayWordOnScreen();

        //Clear the edit text field
        edtTxtInput.setText("");

        //Letters tried
        lettersTried = " ";
        lettersTriedTv.setText(MESSAGE_WITH_LETTERS_TRIED);

        triesLeft = 6;
        tvTriesLeft.setText(String.valueOf(triesLeft));
    }

    //Find the letter and replace the _ with the letter
    private void revealLetterInWord(char letter) {
        int index = word.indexOf(letter);

        while(index >= 0) {
            wordDisplayArray[index] = word.charAt(index);
            index = word.indexOf(letter, index + 1);
        }

        wordDisplay = String.valueOf(wordDisplayArray);
    }

    //Show the word
    private void displayWordOnScreen() {
        String formattedString = "";
        for (char character : wordDisplayArray) {
            formattedString += character + " ";
        }
        tvWord.setText(formattedString);
    }

    private void downloadAndSetImage(String imageUrl, ImageView image) {
        Picasso.get().load(imageUrl).into(image);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu != null) {
            menu.setGroupVisible(R.id.playButtonGroupID, false);
        }
    }
}
