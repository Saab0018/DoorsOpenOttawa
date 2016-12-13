/**
 * Displaying web service data in a ListActivity.
 *
 * @see {BuildingAdapter}
 * @see {res.layout.item_building.xml}
 *
 * @author Matt Saab (saab0018)
 *
 *
 * Reference: based on DisplayList in "Connecting Android Apps to RESTful Web Services" with David Gassner
 */

package com.algonquincollege.saab0018.doorsopenottawa;

import com.algonquincollege.saab0018.doorsopenottawa.model.Building;
import com.algonquincollege.saab0018.doorsopenottawa.parsers.BuildingJSONParser;
import com.algonquincollege.saab0018.doorsopenottawa.utils.HttpManager;
import com.algonquincollege.saab0018.doorsopenottawa.utils.HttpMethod;
import com.algonquincollege.saab0018.doorsopenottawa.utils.RequestPackage;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.AdapterView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Filter;


/**
 * Sending POST parameters in JSON format.
 *
 * @see {util.HttpManager}
 * @see {util.HttpMethod}
 * @see {util.RequestPackage}
 * @see {BuildingAdapter}
 * @see {res.layout.item_planet.xml}
 *
 * @author Saab0018@algonquinlive.com
 * created on 2016-12-02
**/


public class MainActivity extends ListActivity {  //AdapterView.OnItemClickListener {

    private static final String ABOUT_DIALOG_TAG;

    static {
        ABOUT_DIALOG_TAG = "About Dialog";
    }

    // URL to my RESTful API Service hosted on my Bluemix account.
    public static final String IMAGE_BASE_URL = "https://doors-open-ottawa-hurdleg.mybluemix.net/";
    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";
    public static final String LOGOUT = "https://doors-open-ottawa-hurdleg.mybluemix.net/" + "users/logout";

    private ProgressBar pb;
    private List<DoTask> tasks;
    private List<Building> buildingList;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);

        tasks = new ArrayList<>();

        // Single selection && register this ListActivity as the event handler
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        // Event listener to handle the item's click
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Building theSelectedBuilding = buildingList.get(position);

            // Set up the intent
            Intent intent = new Intent( getApplicationContext(), DetailActivity.class );
            intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
            intent.putExtra("detailBuildingName", theSelectedBuilding.getName());
            intent.putExtra("detailBuildingDescription", theSelectedBuilding.getDescription());
            intent.putExtra("detailBuildingAddress", theSelectedBuilding.getAddress());
            intent.putExtra("buildingID", theSelectedBuilding.getBuildingId());

            // Loop to read the OpenHour of the selected building
            String openHours = "";
            for (int i=0; i<theSelectedBuilding.getOpenHours().size(); i++) {
                openHours += theSelectedBuilding.getOpenHours().get(i)+"\n";
            }

            intent.putExtra("detailBuildingOpenHours", openHours);
            startActivity( intent );

            }
        });
        //set on long click to edit building
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {

                Building theSelectedBuilding = buildingList.get(pos);
                Intent intent2 = new Intent( getApplicationContext(), EditActivity.class );
                intent2.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                intent2.putExtra("editName", theSelectedBuilding.getName());
                intent2.putExtra("editDescription", theSelectedBuilding.getDescription());
                intent2.putExtra("editAddress", theSelectedBuilding.getAddress());
                startActivity( intent2);

                Log.v("long clicked","pos: " + pos);

                return true;
            }
        });
        // Request data when application loads
        if (isOnline()) {
            requestData( REST_URI );
        } else {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        //set listener to handle refresh
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                updateDisplay();
            }
        });


    }

    private void requestData(String uri) {
//        MyTask task = new MyTask();
//        task.execute(uri);

        RequestPackage getPackage = new RequestPackage();
        getPackage.setMethod(HttpMethod.GET);
        getPackage.setUri(uri);

        DoTask DoTask = new DoTask();
        DoTask.execute(getPackage);
    }
    private class DoTask extends AsyncTask<RequestPackage, String, String> {

        @Override
        protected void onPreExecute() {
            //pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(RequestPackage ... params) {

//            String content = HttpManager.getNewData(params[0], "saab0018", "password" );
            String content = HttpManager.getNewData(params[0]);

            return content;
        }

        @Override
        protected void onPostExecute(String result) {

            // pb.setVisibility(View.INVISIBLE);

            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
            buildingList = BuildingJSONParser.parseFeed(result);
            updateDisplay();
        }
    }
    protected void updateDisplay() {
        //Use PlanetAdapter to display data
        BuildingAdapter adapter = new BuildingAdapter(this, R.layout.item_building, buildingList);
        setListAdapter(adapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }


    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnectedOrConnecting()) ? true : false;
    }

    public void onDestroy() {

        new LogOutTask().execute(LOGOUT);
        super.onDestroy();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
       SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
       searchView.setSearchableInfo(
               searchManager.getSearchableInfo(getComponentName()));

       searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
           public boolean onQueryTextSubmit(String s) {
            ((BuildingAdapter) getListAdapter()).getFilter().filter(s);
               return false;
               }

            @Override
           public boolean onQueryTextChange(String s) {
                ((BuildingAdapter) getListAdapter()).getFilter().filter(s);
                return false;
                }
            });

       return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if ( item.isCheckable() ) {
            // leave if the list is null
            if ( buildingList == null ) {
                return true;
            }

            // which sort menu item did the user pick?
            switch( id ) {
                case R.id.action_sort_name_asc:
                    Collections.sort( buildingList, new Comparator<Building>() {
                        @Override
                        public int compare( Building lhs, Building rhs ) {
                            // Log.i( "PLANETS", "Sorting planets by name (a-z)" );
                            return lhs.getName().compareTo( rhs.getName() );
                        }
                    });
                    break;

                case R.id.action_sort_name_dsc:
                    Collections.sort( buildingList, Collections.reverseOrder(new Comparator<Building>() {
                        @Override
                        public int compare( Building lhs, Building rhs ) {
                            //  Log.i( "PLANETS", "Sorting planets by name (z-a)" );
                            return lhs.getName().toLowerCase().compareTo( rhs.getName().toLowerCase() );
                        }
                    }));
                    break;

            }
            // remember which sort option the user picked
            item.setChecked( true );
            // re-fresh the list to show the sort order
            ((ArrayAdapter)getListAdapter()).notifyDataSetChanged();
        } // E




        if (id == R.id.action_about) {
            DialogFragment newFragment = new com.algonquincollege.saab0018.doorsopenottawa.AboutDialogFragment();
            newFragment.show( getFragmentManager(), ABOUT_DIALOG_TAG );
            return true;
        }

         if (id == R.id.add_building) {
            // Set up the intent
            Intent intent = new Intent( getApplicationContext(), NewBuildingActivity.class );
            intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
            startActivity( intent );
            return true;

        }


        return super.onOptionsItemSelected(item);
    }


    private class LogOutTask extends AsyncTask<String, String, String> {



        @Override
        protected String doInBackground(String... params) {
            String content = HttpManager.getData( params[0], "saab0018", "password" );
            return content;

        }

        @Override
        protected void onPostExecute(String result) {
          Log.d(result, "logged out");
        }
    }


}