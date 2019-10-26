package se.iteda.hangman;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private String MESSAGE_WITH_LETTERS_TRIED;
    private TextView tvTriesLeft;
    private int triesLeft;
    private String WINNING_MESSAGE;
    private String LOSING_MESSAGE;

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
        mToolbar.setTitle(R.string.title_game);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MESSAGE_WITH_LETTERS_TRIED = getString(R.string.game_letters_tried);
        WINNING_MESSAGE = getString(R.string.game_win);
        LOSING_MESSAGE = getString(R.string.game_lose);

        wordsList = new ArrayList<>();
        Button resetButton = view.findViewById(R.id.btnResetID);
        Button guessButton = view.findViewById(R.id.btnGuessID);
        tvWord = view.findViewById(R.id.tvWordID);
        edtTxtInput = view.findViewById(R.id.playerInputID);
        lettersTriedTv = view.findViewById(R.id.tvLettersGuessedID);
        tvTriesLeft = view.findViewById(R.id.tvTriesLeftID);

        readWords();
        initGame();

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame(v);
            }
        });

        guessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence charSequence = edtTxtInput.getText();
                //If the field is ! null
                if (charSequence.length() != 0) {
                    checkIfLetterIsInWord(charSequence.charAt(0));
                }
                else {
                    Toast.makeText(getActivity(), getString(R.string.toast_game_input_empty), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void resetGame(View v) {
        TextView winText = getView().findViewById(R.id.tvWinMessageID);
        ImageView imageFirst = getView().findViewById(R.id.imgFirsErrorID);
        ImageView imageSecond = getView().findViewById(R.id.imgSecondErrorID);
        ImageView imageThird = getView().findViewById(R.id.imgThirdErrorID);
        ImageView imageFourth = getView().findViewById(R.id.imgFourthErrorID);
        ImageView imageFifth = getView().findViewById(R.id.imgFifthErrorID);
        ImageView imageDead = getView().findViewById(R.id.imgDeadErrorID);

        winText.setVisibility(View.INVISIBLE);
        imageFirst.setVisibility(View.INVISIBLE);
        imageSecond.setVisibility(View.INVISIBLE);
        imageThird.setVisibility(View.INVISIBLE);
        imageFourth.setVisibility(View.INVISIBLE);
        imageFifth.setVisibility(View.INVISIBLE);
        imageDead.setVisibility(View.INVISIBLE);
        initGame();
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
                    edtTxtInput.setEnabled(false);
                    setStateText(getString(R.string.game_win));
                }
            }
        }
        else {
            decreaseAndDisplayTriesLeft();

            if (triesLeft == 0) {
                tvTriesLeft.setText(LOSING_MESSAGE);
                tvWord.setText(word);
                edtTxtInput.setEnabled(false);
                setStateText(getString(R.string.game_lose));
            }
        }

        //Repeated letter input
        if (lettersTried.indexOf(letter) < 0) {
            lettersTried += letter + ", ";
            String messageToBeDisplayed = MESSAGE_WITH_LETTERS_TRIED + lettersTried;
            lettersTriedTv.setText(messageToBeDisplayed);
        }

    }

    private void setStateText(String text) {
        TextView winText = getView().findViewById(R.id.tvWinMessageID);
        //Set text to win or lose text
        winText.setText(text);

        //Set the color after win or lose
        if (text.equals(getString(R.string.game_lose))) {
            winText.setTextColor(getResources().getColor(R.color.colorTextLose));
        }
        else {
            winText.setTextColor(getResources().getColor(R.color.colorTextWin));
        }

        //Set the text visible and start scaleanimation
        winText.setVisibility(View.VISIBLE);
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                winText,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        scaleDown.setDuration(310);

        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);

        scaleDown.start();
    }

    private void decreaseAndDisplayTriesLeft() {
        final String IMAGE_FIRST = "https://raw.githubusercontent.com/RiftApps/HangStickMan/master/app/src/main/res/drawable-v24/img1.png";
        final String IMAGE_SECOND = "https://raw.githubusercontent.com/RiftApps/HangStickMan/master/app/src/main/res/drawable-v24/img2.png";
        final String IMAGE_THIRD = "https://raw.githubusercontent.com/RiftApps/HangStickMan/master/app/src/main/res/drawable-v24/img3.png";
        final String IMAGE_FOURTH = "https://raw.githubusercontent.com/RiftApps/HangStickMan/master/app/src/main/res/drawable-v24/img4.png";
        final String IMAGE_FIFTH = "https://raw.githubusercontent.com/RiftApps/HangStickMan/master/app/src/main/res/drawable-v24/img5.png";
        final String IMAGE_DEAD = "https://raw.githubusercontent.com/RiftApps/HangStickMan/master/app/src/main/res/drawable-v24/img6.png";

        ImageView imageFirst = getView().findViewById(R.id.imgFirsErrorID);
        ImageView imageSecond = getView().findViewById(R.id.imgSecondErrorID);
        ImageView imageThird = getView().findViewById(R.id.imgThirdErrorID);
        ImageView imageFourth = getView().findViewById(R.id.imgFourthErrorID);
        ImageView imageFifth = getView().findViewById(R.id.imgFifthErrorID);
        ImageView imageDead = getView().findViewById(R.id.imgDeadErrorID);

        if (triesLeft > 0) {
            triesLeft--;
            tvTriesLeft.setText(String.valueOf(triesLeft));
            switch (triesLeft) {
                case 5:
                    imageFirst.setVisibility(View.VISIBLE);
                    downloadAndSetImage(IMAGE_FIRST, imageFirst);
                    break;
                case 4:
                    imageSecond.setVisibility(View.VISIBLE);
                    downloadAndSetImage(IMAGE_SECOND, imageSecond);
                    break;
                case 3:
                    imageThird.setVisibility(View.VISIBLE);
                    downloadAndSetImage(IMAGE_THIRD, imageThird);
                    break;
                case 2:
                    imageFourth.setVisibility(View.VISIBLE);
                    downloadAndSetImage(IMAGE_FOURTH, imageFourth);
                    break;
                case 1:
                    imageFifth.setVisibility(View.VISIBLE);
                    downloadAndSetImage(IMAGE_FIFTH, imageFifth);
                    break;
                case 0:
                    imageDead.setVisibility(View.VISIBLE);
                    downloadAndSetImage(IMAGE_DEAD, imageDead);
                    break;
            }
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

    private void initGame() {
        //Download and set emtpy hanger image
        final String IMAGE_START = "https://raw.githubusercontent.com/RiftApps/HangStickMan/master/app/src/main/res/drawable-v24/tom.png";
        ImageView imageStart = getView().findViewById(R.id.imgVHangManID);
        imageStart.setVisibility(View.VISIBLE);
        downloadAndSetImage(IMAGE_START, imageStart);

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

    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.setGroupVisible(R.id.playButtonGroupID, false);
    }

    private void downloadAndSetImage(String imageUrl, ImageView image) {
        Picasso.get().load(imageUrl).fit().into(image);
    }


}
