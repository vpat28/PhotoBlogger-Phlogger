package edu.miami.cs.vraj_patel.phlogger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

//=================================================================================================
public class SensorLocatorDecode extends AsyncTask<Location,Void,String> {
    //-------------------------------------------------------------------------------------------------

    Context theContext;
    Activity theActivity;
    //-------------------------------------------------------------------------------------------------
    public SensorLocatorDecode(Context context, Activity activity) {

        theContext = context;
        theActivity = activity;
    }
    //-------------------------------------------------------------------------------------------------
    protected String doInBackground(Location... location) {

        return(androidGeodecode(location[0]));
    }
    //-------------------------------------------------------------------------------------------------
    protected void onPostExecute(String result) {
        Editor.testaddy = result;

    }
    //-------------------------------------------------------------------------------------------------
    private String androidGeodecode(Location thisLocation) {
        Log.i("LOGGER", "GEODECODING!!! " );
        Geocoder androidGeocoder;
        List<Address> addresses;
        Address firstAddress;
        String addressLine;
        StringBuilder locationName;
        int index;

        if (Geocoder.isPresent()) {
            androidGeocoder = new Geocoder(theContext);
            try {
                addresses = androidGeocoder.getFromLocation(thisLocation.getLatitude(),
                        thisLocation.getLongitude(),1);
                if (addresses.isEmpty()) {
                    return("ERROR: Unkown location");
                } else {
                    firstAddress = addresses.get(0);
                    locationName = new StringBuilder();
                    index = 0;
                    while ((addressLine = firstAddress.getAddressLine(index)) != null) {
                        locationName.append(addressLine).append(", ");
                        index++;
                    }
                    return (locationName.toString());
                }
            } catch (Exception e) {
                return("ERROR: " + e.getMessage());
            }
        } else {
            return("ERROR: No Geocoder available");
        }
    }

//-------------------------------------------------------------------------------------------------

//=================================================================================================

}
