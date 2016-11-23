/**
 * Displaying web service data in a ListActivity.
 *
 * @see {BuildingAdapter}
 * @see {res.layout.item_building.xml}
 *
 * @author Matt Saab (saab0018)
 * @author Leonardo Alps (alve0024)
 *
 * Reference: based on DisplayList in "Connecting Android Apps to RESTful Web Services" with David Gassner
 */

package com.algonquincollege.saab0018.doorsopenottawa;

import com.algonquincollege.saab0018.doorsopenottawa.model.Building;
import com.algonquincollege.saab0018.doorsopenottawa.parsers.BuildingJSONParser;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ListActivity {  //AdapterView.OnItemClickListener {

    private static final String ABOUT_DIALOG_TAG;

    static {
        ABOUT_DIALOG_TAG = "About Dialog";
    }

    // URL to my RESTful API Service hosted on my Bluemix account.
    public static final String IMAGE_BASE_URL = "https://doors-open-ottawa-hurdleg.mybluemix.net/";
    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";

    private ProgressBar pb;
    private List<MyTask> tasks;
    private List<Building> buildingList;

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

            // Loop to read the OpenHour of the selected building
            String openHours = "";
            for (int i=0; i<theSelectedBuilding.getOpenHours().size(); i++) {
                openHours += theSelectedBuilding.getOpenHours().get(i)+"\n";
            }

            intent.putExtra("detailBuildingOpenHours", openHours);
            startActivity( intent );
            }
        });
        // Request data when application loads
        if (isOnline()) {
            requestData( REST_URI );
        } else {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
        }
    }

    private void requestData(String uri) {
        MyTask task = new MyTask();
        task.execute(uri);
    }

    protected void updateDisplay() {
        //Use PlanetAdapter to display data
        BuildingAdapter adapter = new BuildingAdapter(this, R.layout.item_building, buildingList);
        setListAdapter(adapter);
 }


    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnectedOrConnecting()) ? true : false;
    }


    private class MyTask extends AsyncTask<String, String, List<Building>> {

        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected List<Building> doInBackground(String... params) {
            String content = HttpManager.getData(params[0]);
            //return content;
            buildingList = BuildingJSONParser.parseFeed(content);
            return buildingList;
        }

        @Override
        protected void onPostExecute(List<Building> result) {
            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }
            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
            buildingList = result;
            updateDisplay();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_about) {
            DialogFragment newFragment = new com.algonquincollege.saab0018.doorsopenottawa.AboutDialogFragment();
            newFragment.show( getFragmentManager(), ABOUT_DIALOG_TAG );
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}