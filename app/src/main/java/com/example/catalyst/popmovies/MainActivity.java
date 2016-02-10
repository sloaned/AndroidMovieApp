package com.example.catalyst.popmovies;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.catalyst.popmovies.data.MovieContract;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       /* if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MovieFragment())
                    .commit();
        }*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //deleteDatabase(MovieContract.DATABASE_NAME);
        SQLiteDatabase movieDatabase = openOrCreateDatabase(MovieContract.DATABASE_NAME,MODE_PRIVATE, null);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);




        SearchManager searchManager = (SearchManager) getSystemService (Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
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

        else if (id == R.id.action_home) {

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
