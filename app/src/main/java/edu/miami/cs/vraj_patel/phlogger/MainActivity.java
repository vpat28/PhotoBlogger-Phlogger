package edu.miami.cs.vraj_patel.phlogger;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, SimpleAdapter.ViewBinder {
    private static final String DATABASE_NAME = "Phlogger.db"; // setting the database naem
   public static PhloggerDB DBobj; // making the database object
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation =null;
    private LocationRequest locationRequest;
    static String addy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.DBobj = Room.databaseBuilder(getApplicationContext(),PhloggerDB.class,DATABASE_NAME).allowMainThreadQueries().build(); //building the database
        getPermissions.launch(new String[]{Manifest.permission.CAMERA, // getting permissions (theres a shit load)
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_CONTACTS});

    }

    private void goOnCreating(boolean havePermission) {
        if (havePermission) {
        setContentView(R.layout.activity_main);
        fillList();
            Toast.makeText(this, "Made by Vraj Patel", Toast.LENGTH_LONG).show(); // just giving credit where credit is due this thing took some effort
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this); // new fused location client
            locationRequest = new LocationRequest();
            locationRequest.setInterval(getResources().getInteger( //standard stuff
                    R.integer.time_between_location_updates_ms));
            locationRequest.setFastestInterval(getResources().getInteger(
                    R.integer.time_between_location_updates_ms) / 2);
            startLocating(LocationRequest.PRIORITY_HIGH_ACCURACY); // not mission critical, but we're still making it high accuracy
        } else {
            Toast.makeText(this,"Need permission!",Toast.LENGTH_LONG).show();
            finish();
        }
    }
    private void startLocating(int accuracy) { // From Goeffs examples on his site

        locationRequest.setPriority(accuracy);
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    myLocationCallback, Looper.myLooper());
        } catch (SecurityException e) {
            Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
        }
    }
    LocationCallback myLocationCallback = new LocationCallback() { // Also from Goeffs examples
        @Override
        public void onLocationResult(LocationResult locationResult) {
            currentLocation = locationResult.getLastLocation();
          //  onLocationChanged(locationResult.getLastLocation());
        }
    };
    public void ClickMaster (View view){
        Intent nextActivity; // declaring intent
        Boolean isLocation = false;
        nextActivity = new Intent(MainActivity.this,Editor.class);
        new SensorLocatorDecode(getApplicationContext(),this).execute(
                currentLocation); // Assigning the Intent to the seperate Editor class
        switch (view.getId()) {
            case (R.id.add_phlog): // If the phlog button is pressed, its time to go to work
                GregorianCalendar theDate = new GregorianCalendar();
              @SuppressLint("SimpleDateFormat") SimpleDateFormat formattedDate = new SimpleDateFormat("HH:mm:ss: EEEE, MMMM dd, yyyy z");
              theDate.setTimeInMillis(System.currentTimeMillis());
                nextActivity.putExtra("time", formattedDate.format(theDate.getTime())); // formatting the time to make it actually readable and putting it in the Intent for the Editor activity to deal with
                nextActivity.putExtra("existing",false);
                if(currentLocation!=null){ // if there is a location
                    isLocation = true; // set the boolean to true
                    String latd = String.valueOf(currentLocation.getLatitude()); // extract the latitude as a string
                    String lontd = String.valueOf(currentLocation.getLongitude()); // extract the longitude as a string
                    nextActivity.putExtra("lat",latd); // putting the latitude into the Intent for the Editor activity to deal with
                    nextActivity.putExtra("long", lontd); // putting the longitude into the Intent for the Editor activity to deal with
                  //  Log.i("ALERT","LAT: " + latd);
                }
                nextActivity.putExtra("is_location",isLocation); // putting the boolean for location into the Intent for the Editor activity to deal with
                startEditor.launch(nextActivity); // launch the Editor activity
        }
    }
    public void fillList() { // populating the Listview
        String[] displayFields = {"image_list", "title_list", "time_list"};
        int[] displayViews = {R.id.image, R.id.description, R.id.time_text};
        ListView theList = (ListView) findViewById(R.id.the_simple_list);
        List<PhloggerEntity> allPhlogEntities = DBobj.daoAccess().fetchAllPhlogs();
        ArrayList<HashMap<String, Object>> phlogList = new ArrayList<>();
        for (PhloggerEntity thisPhlog : allPhlogEntities) {
            HashMap<String, Object> phlogItem = new HashMap<>();
            phlogItem.put("image_list", thisPhlog.getTheImage());
            phlogItem.put("title_list", thisPhlog.getTheTitle());
            phlogItem.put("time_list", thisPhlog.getTheTime());
            phlogList.add(phlogItem);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, phlogList, R.layout.list_item, displayFields, displayViews);
        simpleAdapter.setViewBinder(this);
        theList.setOnItemClickListener(this); // setting On CLick Listener for individual list items
        theList.setAdapter(simpleAdapter); //setting the adapter for the list


    }


    public boolean setViewValue(View view, Object o, String s) {
        switch (view.getId()) {
            case R.id.image:
                ((ImageView) view).setImageURI(Uri.parse((String) o));
                return true;
            case R.id.time_text:
                ((TextView) view).setText((String) o);
                return true;
            case R.id.description:
                if (o == null || ((String) o).isEmpty()) {
                    ((TextView) view).setText(getResources().getString(R.string.no_title));
                    return true;
                }
                ((TextView) view).setText((String) o);
                return true;
            default:
                return true;
        }
    }
    /*
    public boolean setViewValue(View view, Object o, String s) {
        Log.i("ALERT","STRING: " + o);
        switch (view.getId()) {
            case R.id.description:
                if (o == null || ((String) o).isEmpty()) {
                    return true;
                }
                ((TextView) view).setText((String) o);
                return true;


            case R.id.image:
                ((ImageView)view).setImageURI(Uri.parse((String) o)); //setting the thumbnail
                break;
            case R.id.time_text:
                if(o == null) {
                }else {
                    ((TextView) view).setText((String) o); //setting the time
                }
        }
        return(true);
    }

     */

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView itsGotime; //im sorry for the stupid variable names i hope they're kinda funny, Im starting to lose my mind while commenting
        itsGotime = view.findViewById(R.id.time_text);
            Intent nextActivity;
            nextActivity = new Intent(MainActivity.this,Editor.class);
            Log.i("ALERT", "Time: " + itsGotime.getText());
            nextActivity.putExtra("time", itsGotime.getText());
            startEditor.launch(nextActivity);
// to summarize this method: If i click on an existing phlog in the list, it sends the time to the editor activity, where the editor activity determines there is an existing phlog matching that time
        }


    ActivityResultLauncher<Intent> startEditor = registerForActivityResult( //ARL for Editor activity
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode()==RESULT_OK){ // Result_OK is sent back if a description was successfully set

                    }else{ // user pressed back button or didnt save in the Editor activity

                    }

                    fillList(); // no matter what the result is, just fill the list in case theres changes
                }
            });
    ActivityResultLauncher<String[]> getPermissions = registerForActivityResult( // Once again, standard stuff
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> results) {

                    for (String key : results.keySet()) {
                        if (!results.get(key)) {
                            goOnCreating(false);
                        }
                    }
                    goOnCreating(true);
                }
            });
}