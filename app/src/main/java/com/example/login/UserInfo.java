package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.auth.User;

public class UserInfo extends AppCompatActivity {

private FloatingActionButton addPostBtn;
private BottomNavigationView mainBottomNav;
private HomeFragment homeFragment;
private NotificationFragment notificationFragment;
private AccountFragment accountFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Photo Blog");

homeFragment=new HomeFragment();
notificationFragment=new NotificationFragment();
accountFragment=new AccountFragment();
        replaceFragment(homeFragment);
        mainBottomNav=findViewById(R.id.mainBottomNav);
mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()){
            case R.id.bottomHomeItem:
                replaceFragment(homeFragment);
                return true;
            case R.id.bottomNotificationItem:
                replaceFragment(notificationFragment);
                return true;
            case R.id.bottomAccountItem:
                replaceFragment(accountFragment);
                return true;
                default:
                    return false;
        }

    }
});
        addPostBtn=findViewById(R.id.addPostBtn);
        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newPostintent=new Intent(UserInfo.this,NewPostActivity.class);
                startActivity(newPostintent);
            }
        });
    }
    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainContainer,fragment);
        fragmentTransaction.commit();

    }
}
