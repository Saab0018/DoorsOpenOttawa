package com.algonquincollege.saab0018.doorsopenottawa;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import android.graphics.Color;
import android.util.LruCache;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.algonquincollege.saab0018.doorsopenottawa.model.Building;

import static com.algonquincollege.saab0018.doorsopenottawa.MainActivity.IMAGE_BASE_URL;

/**
 * Created by mattsaab and alve0024 on 2016-11-08.
 */

public class BuildingAdapter extends ArrayAdapter<Building> implements Filterable {

    private Context context;
    private List<Building> buildingList;

    private LruCache<Integer, Bitmap> imageCache;

    int[] color_arr={Color.BLUE,Color.CYAN,Color.DKGRAY,Color.GREEN,Color.RED};


    public BuildingAdapter(Context context, int resource, List<Building> objects) {
        super(context, resource, objects);
        this.context = context;
        this.buildingList = objects;
        final int maxMemory = (int)(Runtime.getRuntime().maxMemory()/1024);
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>(cacheSize);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_building, parent, false);


        //Display building name in the TextView widget
        Building building = buildingList.get(position);
        TextView tv = (TextView) view.findViewById(R.id.textView);
        TextView tv2 = (TextView) view.findViewById(R.id.textView2);
        tv.setText(building.getName());
        tv2.setText(building.getAddress());

        //Display building photo in ImageView widget
        //Lazy initialize a building's bitmap image
        if (building.getBitmap() != null) {
            Log.i("BUILDINGS", building.getName() + "\tbitmap in memory");
            ImageView image = (ImageView) view.findViewById(R.id.imageView1);
            image.setImageBitmap(building.getBitmap());
        }
        else{
            Log.i("BUILDINGS", building.getName() + "\tbitmap using AsyncTask");
            BuildingAndView container = new BuildingAndView();
            container.building = building;
            container.view = view;

            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }
        return view;
    }

    //container for AsyncTask params
    private class BuildingAndView{
        public Building building;
        public View view;
        public Bitmap bitmap;

    }

    private class ImageLoader extends AsyncTask<BuildingAndView, Void, BuildingAndView> {

        @Override
        protected BuildingAndView doInBackground(BuildingAndView... params) {
            BuildingAndView container = params[0];
            Building building = container.building;

            try {
                String imageUrl = MainActivity.IMAGE_BASE_URL + building.getImage();
                InputStream in = (InputStream) new URL(imageUrl).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                building.setBitmap(bitmap);
                in.close();
                container.bitmap = bitmap;
                return container;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("IAMGE " + building.getName());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(BuildingAndView result) {
            if(result==null) return;
            ImageView image = (ImageView) result.view.findViewById(R.id.imageView1);
            image.setImageBitmap(result.bitmap);
           // result.building.setBitmap(result.bitmap);
            imageCache.put(result.building.getBuildingId(), result.bitmap);
        }
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                buildingList = (List<Building>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<String> FilteredArrayNames = new ArrayList<>();

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < buildingList.size(); i++) {
                    String dataNames = buildingList.get(i).getName();
                    if (dataNames.toLowerCase().startsWith(constraint.toString()))  {
                        FilteredArrayNames.add(dataNames);
                    }
                }

                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;
                Log.e("VALUES", results.values.toString());

                return results;
            }
        };

        return filter;
    }



}
