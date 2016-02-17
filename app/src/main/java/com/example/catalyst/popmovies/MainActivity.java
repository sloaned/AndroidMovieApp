package com.example.catalyst.popmovies;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.example.catalyst.popmovies.data.MovieContract;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //deleteDatabase(MovieContract.DATABASE_NAME);
        SQLiteDatabase movieDatabase = openOrCreateDatabase(MovieContract.DATABASE_NAME,MODE_PRIVATE, null);

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        String query = prefs.getString(getString(R.string.pref_saved_search), null);

        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putString(getString(R.string.pref_search_key), query);
        editor.apply();
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        SearchManager searchManager = (SearchManager) getSystemService (Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                editor.putString(getString(R.string.pref_search_key), query);
                editor.apply();
                Intent intent = new Intent(getBaseContext(), SearchActivity.class);
                intent.setAction(Intent.ACTION_SEARCH).putExtra(SearchManager.QUERY, query);
                startActivity(intent);
                return true;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_home) {

            System.out.println("Home button clicked");
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putString(getString(R.string.pref_search_key), (String) null);
            editor.apply();

            this.setIntent(null);
            startActivity(new Intent(this, MainActivity.class));

            return true;
        }

        if (id == R.id.action_country) {
            System.out.println("country button clicked");
            DialogFragment dialog = CountryFragment.newInstance();
            if (dialog.getDialog() != null) {
                dialog.getDialog().setCanceledOnTouchOutside(true);
            }
            dialog.show(this.getSupportFragmentManager(), "dialog");
            //startActivity(this, CountryActivity.class));
            return true;
        }


      /*  if (id == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
                 //   .putExtra("Movies", film);
            startActivity(intent);
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

}
