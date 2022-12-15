package com.nigoote.ernestinecashpower;

import static com.nigoote.ernestinecashpower.MainActivity.SHARED_PREFERENCES_NAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nigoote.ernestinecashpower.fragments.Payragment;
import com.nigoote.ernestinecashpower.fragments.RequestFragment;

public class HomeActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new RequestFragment()).addToBackStack(null).commit();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        shared pref data
                    sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                    String str_phone = sharedPreferences.getString("phone", "");
                    String str_userid = sharedPreferences.getString("id", "");
                    switch (item.getItemId()){
                        case R.id.nav_request:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new RequestFragment().newInstance(str_phone,str_userid)).addToBackStack(null).commit();
                            break;

                        case R.id.nav_pay:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new Payragment().newInstance(str_phone,str_userid)).addToBackStack(null).commit();
                            break;
                    }
                    return true;
                }
            };

    //    optionMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu,menu);
        return true;
    }
//    onOptionClicks

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_logout:
                SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //    end of OptionMenu

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0){
            super.onBackPressed();
        }else {
            getSupportFragmentManager().popBackStack();
        }
    }
}