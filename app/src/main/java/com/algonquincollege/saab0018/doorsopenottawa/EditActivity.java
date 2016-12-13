package com.algonquincollege.saab0018.doorsopenottawa;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.algonquincollege.saab0018.doorsopenottawa.model.Building;
import com.algonquincollege.saab0018.doorsopenottawa.utils.HttpManager;
import com.algonquincollege.saab0018.doorsopenottawa.utils.HttpMethod;
import com.algonquincollege.saab0018.doorsopenottawa.utils.RequestPackage;

/**
 * Created by mattsaab on 2016-12-12.
 */

public class EditActivity extends Activity {

    public static final String IMAGE_BASE_URL = "https://doors-open-ottawa-hurdleg.mybluemix.net/";
    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";


    private EditText editName;
    private EditText editAddress;
    private EditText editDescription;
    private Integer bID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_building);


        editName = (EditText) findViewById(R.id.editName);
        editAddress = (EditText) findViewById(R.id.editAddress);
        editDescription = (EditText) findViewById(R.id.editDescription);

        Bundle bundle = getIntent().getExtras();
        if ( bundle != null ) {
            String buildingNameEdit = bundle.getString( "editName");
            String buildingDescriptionEdit = bundle.getString( "editDescription" );
            String buildingAddressEdit = bundle.getString("editAddress");


            // Update the view
            editName.setText(buildingNameEdit);
            editAddress.setText(buildingAddressEdit);
            editDescription.setText(buildingDescriptionEdit);

        }

    }

    private void updatePlanet(String uri) {

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bID = bundle.getInt("buildingID");

            Building building = new Building();
            building.setName(editName.getText().toString());
            building.setImage("123.png");
            building.setAddress(editAddress.getText().toString());
            building.setDescription(editDescription.getText().toString());
            RequestPackage pkg = new RequestPackage();
            pkg.setMethod(HttpMethod.PUT);
            pkg.setUri(uri + "/" + bID);
            pkg.setParam("name", building.getName());
            pkg.setParam("image", building.getImage());
            pkg.setParam("address", building.getAddress());
            pkg.setParam("description", building.getDescription());


            DoTask putTask = new DoTask();
            putTask.execute(pkg);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.cancelEdit) {
            Intent intent = new Intent( getApplicationContext(), MainActivity.class );
            intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
            startActivity( intent );
            return true;
        }else if (id == R.id.saveEdit){
            Intent intent = new Intent( getApplicationContext(), MainActivity.class );
            intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
            finish();
            updatePlanet( REST_URI );
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DoTask extends AsyncTask<RequestPackage, String, String> {

        @Override
        protected String doInBackground(RequestPackage ... params) {

            String content = HttpManager.getNewData(params[0]);
            return content;
        }

        @Override
        protected void onPostExecute(String result) {

            if (result == null) {
                Toast.makeText(EditActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }




}
