package se.iteda.hangman;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MenuFragment menuFragment = new MenuFragment();
        FragmentManager fmStart = getSupportFragmentManager();
        FragmentTransaction fmTrans = fmStart.beginTransaction();
        fmTrans.add(R.id.fragmentContainerID, menuFragment);
        fmTrans.commitAllowingStateLoss();


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
}
