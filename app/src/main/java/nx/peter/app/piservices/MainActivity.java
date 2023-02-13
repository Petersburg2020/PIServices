package nx.peter.app.piservices;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import nx.peter.app.piservices.databinding.ActivityMainBinding;
import nx.peter.app.piservices.databinding.NavHeaderMainBinding;
import nx.peter.app.piservices.ui.ScreenFragment;

public class MainActivity extends BaseScreen {
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;


    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        /*
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
         */
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        View header = navigationView.getHeaderView(0);
        NavHeaderMainBinding nav = NavHeaderMainBinding.bind(header);

        nav.navTitle.addSubColors(Color.parseColor("#FFFF7500"), "PI");
        nav.navTitle.setTextSize(16);

        nav.navWebsite.addUrlLinks("https://www.piservices.org.ng", "https://www.piservices.org.ng");
        nav.navWebsite.setLinksColor(Color.parseColor("#FFFF7500"));

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public void setFragment(@NonNull ScreenFragment fragment) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}