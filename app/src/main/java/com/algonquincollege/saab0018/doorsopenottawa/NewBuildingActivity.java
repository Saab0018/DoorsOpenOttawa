package com.algonquincollege.saab0018.doorsopenottawa;


import android.app.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.algonquincollege.saab0018.doorsopenottawa.model.Building;
import com.algonquincollege.saab0018.doorsopenottawa.utils.HttpManager;
import com.algonquincollege.saab0018.doorsopenottawa.utils.HttpMethod;
import com.algonquincollege.saab0018.doorsopenottawa.utils.RequestPackage;

/**
 * Created by mattsaab on 2016-12-02.
 */

public class NewBuildingActivity extends Activity {


    public static final String IMAGE_BASE_URL = "https://doors-open-ottawa-hurdleg.mybluemix.net/";
    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";

    private TextView newBuildingName;
    private TextView newBuildingDescription;
    private ImageView newBuildingImage;
    private TextView newBuildingAddress;
    private ImageButton cameraButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_building);

        cameraButton = (ImageButton) findViewById(R.id.ImageButton01);
        newBuildingName = (TextView) findViewById( R.id.newBuildingName );
        newBuildingAddress = (TextView) findViewById( R.id.newBuildingAddress );
        newBuildingImage = (ImageView) findViewById(R.id.imageView);
        newBuildingDescription = (TextView) findViewById(R.id.newBuildingDescription);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, 100);
            }
        });

    }


    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent resultIntent){
        Bundle extras;
        Bitmap imageBitmap;

        if ( resultCode== RESULT_CANCELED) {
            Toast.makeText( getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            return;

        }

        switch( requestCode){
            case 100:
                extras = resultIntent.getExtras();
                imageBitmap = (Bitmap) extras.get("data");

                if (imageBitmap != null){
                    newBuildingImage.setImageBitmap(imageBitmap);
                }
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_building_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
                       // Handle action bar item clicks here. The action bar will
                      // automatically handle clicks on the Home/Up button, so long
                       // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.cancel) {
            Intent intent = new Intent( getApplicationContext(), MainActivity.class );
            intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
            startActivity( intent );
            return true;
        }else if (id == R.id.save){
            Intent intent = new Intent( getApplicationContext(), MainActivity.class );
            intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
            startActivity( intent );
            createBuilding( REST_URI);
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void createBuilding(String uri) {

        Building building = new Building();
        building.setName( newBuildingName.getText().toString());
        building.setImage("123.png");
        building.setDescription( newBuildingDescription.getText().toString() );
        building.setAddress( newBuildingAddress.getText().toString() );


        RequestPackage pkg = new RequestPackage();
        pkg.setMethod( HttpMethod.POST );
        pkg.setUri( uri );
        pkg.setParam("name", building.getName() );
        pkg.setParam("image", building.getImage() );
        pkg.setParam("description", building.getDescription() );
        pkg.setParam("address", building.getAddress() );
        DoTask postTask = new DoTask();
        postTask.execute( pkg );
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
                Toast.makeText(NewBuildingActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }


}
