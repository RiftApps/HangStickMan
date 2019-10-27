package se.iteda.hangman;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
    public static final String MY_PREFS_THEME = "sharedPrefTheme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getTheTheme());
        setContentView(R.layout.activity_main);

        Toolbar mToolbar = findViewById(R.id.toolbarID);
        setSupportActionBar(mToolbar);
        Button btnNewGame = findViewById(R.id.btnMenuNewGameID);
        Button btnAbout = findViewById(R.id.btnMenuAboutID);
        Button themeButton = findViewById(R.id.darkmodeButtonID);

        themeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetTheTheme();
            }
        });

        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragment(new GameFragment(), true, "Game");
            }
        });
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragment(new AboutFragment(), true, "About");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_play:
                addFragment(new GameFragment(), true, "Game");
                return true;

            case R.id.action_about:
                addFragment(new AboutFragment(), true, "About");
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void SetTheTheme() {
        int theme = getTheTheme();

        if (theme == R.style.AppTheme) {
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_THEME, MODE_PRIVATE).edit();
            editor.putInt("current_theme", R.style.HalloweenTheme);
            editor.commit();
            recreate();
        }
        else if (theme == R.style.HalloweenTheme) {
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_THEME, MODE_PRIVATE).edit();
            editor.putInt("current_theme", R.style.AppTheme);
            editor.commit();
            recreate();
        }
    }

    public int getTheTheme() {
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_THEME, MODE_PRIVATE);
        int theme = prefs.getInt("current_theme", R.style.AppTheme);
        return theme;
    }

    public void addFragment(Fragment fragment, boolean addToBackStack, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        if (addToBackStack) {
            ft.addToBackStack(tag);
        }
        ft.replace(R.id.fragmentContainerID, fragment, tag);
        ft.commitAllowingStateLoss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
