package com.example.easymovefront.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easymovefront.R;
import com.example.easymovefront.data.model.LoggedUser;
import com.example.easymovefront.network.CreateImageFromUrlTask;
import com.example.easymovefront.network.CreateMarkerTask;
import com.example.easymovefront.network.GetSingleUserTask;
import com.example.easymovefront.ui.maps.MapsActivity;
import com.example.easymovefront.ui.ranking.RankingActivity;
import com.example.easymovefront.ui.settings.SettingsActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class ProfileActivity extends AppCompatActivity {
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;
    private ProgressBar mloadingBar;
    DrawerLayout dLayout;
    private ProgressBar loadingDrawer;
    private ImageView drawerHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mToolbar = (Toolbar) findViewById(R.id.toolbarProfile);

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setNavigationDrawer();
        initializeProfileUser();
        final Button SignOut = findViewById(R.id.signOut_button);
        findViewById(R.id.signOut_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.signOut_button:
                        signOut(LoggedUser.getInstance().getmGoogleSignInClient());
                        break;
                    // ...
                }
            }
        });
    }

    private void signOut(GoogleSignInClient mGoogleSignInClient) {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
        revokeAccess(mGoogleSignInClient);
        finish();
    }

    private void revokeAccess(GoogleSignInClient mGoogleSignInClient) {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    private void initializeProfileUser() {
        ImageView pic = findViewById(R.id.pictureProfile);
        TextView name = findViewById(R.id.nameProfile);
        TextView punts = findViewById(R.id.pointsUser);
        name.setText(LoggedUser.getInstance().getmUserAccount().getDisplayName());
        Drawable d;
        CreateImageFromUrlTask pictask = new CreateImageFromUrlTask(getApplicationContext());
        pictask.execute(LoggedUser.getInstance().getmUserAccount().getPhotoUrl().toString());
        try {
            d = pictask.get();
            pic.setImageDrawable(d);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        GetSingleUserTask puntstask = new GetSingleUserTask((getApplicationContext()));
        puntstask.execute(LoggedUser.getInstance().getId());
        try {
            String s = puntstask.get();
            JSONObject json = new JSONObject(s);
            punts.setText("Score: " + json.getString("puntuacio"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setNavigationDrawer() {
        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout); // initiate a DrawerLayout
        mDrawerToggle = new ActionBarDrawerToggle(this, dLayout, mToolbar,R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                invalidateOptionsMenu();
            }
        };
        dLayout.addDrawerListener(mDrawerToggle);
        NavigationView navView = (NavigationView) findViewById(R.id.navigationProfile); // initiate a Navigation View
        // implement setNavigationItemSelectedListener event on NavigationView
        navView.getMenu().getItem(1).setChecked(true);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                loadingDrawer = findViewById(R.id.loadingDrawer);
                drawerHeader = findViewById(R.id.imageHeader);
                CharSequence text;
                int duration;
                Toast toast;
                // check selected menu item's id and replace a Fragment Accordingly
                switch (menuItem.getItemId()) {
                    case R.id.mapActivity:
                        drawerHeader.setVisibility(View.GONE);
                        loadingDrawer.setVisibility(View.VISIBLE);
                        Intent mapIntent = new Intent(getApplicationContext(), MapsActivity.class);
                        startActivity(mapIntent);
                        finish();
                        return true;
                    case R.id.profile:
                        return true;
                    case R.id.ranking:
                        drawerHeader.setVisibility(View.GONE);
                        loadingDrawer.setVisibility(View.VISIBLE);
                        Intent rankingIntent = new Intent(getApplicationContext(), RankingActivity.class);
                        startActivity(rankingIntent);
                        finish();
                        return true;
                    case R.id.settings:
                        text = "SETTINGS PLACEHOLDER";
                        duration = Toast.LENGTH_LONG;
                        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(intent);

                        toast = Toast.makeText(getApplicationContext(), text, duration);
                        toast.show();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }
}
