package edu.miami.cs.vraj_patel.phlogger;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.media.MediaPlayer;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class Editor extends AppCompatActivity implements View.OnTouchListener {
    // Im so sorry for all the global variables, it had to be done I'll comment to make it easier to read
    Uri image = null; // The URI for the main imageview
    ImageView theBigpicture; // The aforementioned main imageview
    VideoView theSilverScreen; // video view in the xml
    String theTime; // String that holds the time
    Uri photoUri; // URI to store the photo (if the user takes one)
    Uri videoUri; // URI to store the video (if the user takes one)
    EditText theTitle; // EditText where you enter the title of the Phlog
    EditText theDescription; // EditText where you enter the description of the Phlog
    TextView timeView; // Textview that shows the time the phlog was made
     TextView addyView; // Textview that shows the street address of where the phlog was made
    TextView coordinateView; // Textview that shows the coordinates fo wehre the phlog was made
    PhloggerEntity checkEntity; // Phlogger entity object to see if there an existing phlog with the time sent in from the main activity
    Boolean isLocation; // Boolean variable to see if there is a location for this phlog (sent from MainActivity)
    String coordinates; // String to hold the coordinates
    static String testaddy; // String whos input is set in the onPost execute of the SensorLocatorDecode
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor);
        Button emailButton = findViewById(R.id.email); // Linking this button to the email button in the XML
        emailButton.setVisibility(View.INVISIBLE); //Setting it invisible by default
        theSilverScreen = findViewById(R.id.vidview); // Linking this videoview to the one in the XML
        theSilverScreen.setVisibility(View.INVISIBLE); // VideoView will be invisible by default, it'll only show up if the user actually takes a video
        theBigpicture = findViewById(R.id.the_image); //linking this imageview to the one in the XML
        theTitle = findViewById(R.id.title_text); // linking this EditText to the one in the XML
        theDescription = findViewById(R.id.description_text); // linking this EditText to the one in the XML
        addyView = findViewById(R.id.addy_view); // Linking this textview to the one in the XML
        coordinateView = findViewById(R.id.coordinates); // Linking this textview to the one in the XML
        timeView = findViewById(R.id.time_view); // Linking this textview to the one in the XML (Theres so many of these, I'm so sorry)
        theTime = this.getIntent().getStringExtra("time"); // getting the time of the phlog sent from MainActivity
        isLocation = this.getIntent().getBooleanExtra("is_location", false); // getting location boolean of phlog from MainActivity
        checkEntity = MainActivity.DBobj.daoAccess().getByTime(theTime); // checking to see if there is a phlog in the database with a matching time
        if (checkEntity != null) { // Hooray! There is a matching phlog! So do this
            emailButton.setVisibility(View.VISIBLE); // email button is now visible since it is already a saved phlog
            image = Uri.parse(checkEntity.getTheImage()); // converting the string stored in the database to a URI
                if(checkEntity.getTheVideo() != null){
                    videoUri = Uri.parse(checkEntity.getTheVideo());
                    theSilverScreen.setVideoURI(videoUri);
                    theSilverScreen.setVisibility(View.VISIBLE);
                    theSilverScreen.setClickable(true);
                    theSilverScreen.setOnTouchListener(Editor.this::onTouch);
                }
            timeView.setText(checkEntity.getTheTime()); // setting the textview for the time from the string in the database
            theTitle.setText(checkEntity.getTheTitle());// setting the textview for the title from the string in the database
            theDescription.setText(checkEntity.getTheDescription());
            theBigpicture.setImageURI(image); // setting the main imageview from the URI in the database
            coordinates = checkEntity.getTheCoordinates();
            coordinateView.setText(coordinates); // setting the textview for the coordinates from the string in the database
            addyView.setText(checkEntity.getTheAddy()); // setting the textview for the address from the string in the database
        } else { // there is no phlog matching the time so we have to make a new one
            timeView.setText(theTime); // first things first, set the textview with the time from the MainActivity

             if (isLocation == true) { // There is a location
                 String latitude = this.getIntent().getStringExtra("lat"); //setting the latitude sent in from the MainActivity
                 Log.i("ALERT", "LATEDIT: " + latitude);
                String longitude = this.getIntent().getStringExtra("long"); // setting the longitude sent in from the MainActivity
                coordinates = ("Lat : " + latitude + "," + " Long : " + longitude);
                coordinateView.setText(coordinates); // setting the coordinates sent in from the MainActivity
                addyView.setText(testaddy); // setting the address from the string from the SensorLocatorDecode
                    } else { // There is no location
                    Log.i("ALERT", "location properties all null");
                    coordinateView.setText(R.string.not_available); // set the textview for coordinates to say Not Available
                    addyView.setText(R.string.not_available); // set the textview for the address to say Not Available
        }
    }
    }

    public void ClickMaster(View view) throws IOException {
        PhloggerEntity phlogData =new PhloggerEntity(); // making a new Phlog entity to save into the database with all the attributes
        String titleText = theTitle.getText().toString(); //getting the Text the user entered in the EditTExt
        String descriptionText = theDescription.getText().toString(); //getting the Text the user entered in the EditTExt
        switch (view.getId()) {
            case (R.id.save):
                Intent backHome = new Intent(); //creating intent to return to the main
                backHome.setClassName("edu.miami.cs.vraj_patel.phlogger", "edu.miami.cs.vraj_patel.phlogger.MainActivity");
                if (image !=null ) { // for a Photo Blog, you need at least a photo.
                    Log.i("ALERT", "if statement entered" );
                    phlogData.setTheTitle(titleText); //saving the title the user entered into the Database
                    phlogData.setTheDescription(descriptionText); // saving the description the user entered into the Database
                    phlogData.setTheImage(image.toString()); //  saving the image URI as a string to into the database
                        if(videoUri != null){
                            phlogData.setTheVideo(videoUri.toString());
                        }
                    phlogData.setTheTime(theTime); // saving the time into the database
                    phlogData.setTheCoordinates(coordinates); // saving the coordinates into the Database
                    phlogData.setTheAddy(testaddy); // saving the address into the database
                    if(checkEntity!=null){ // If this is an existing phlog
                        phlogData.setId(checkEntity.getId());
                        MainActivity.DBobj.daoAccess().updatePhlog(phlogData); //update existing phlogs data if changes are made
                    }else{ // otherwise its a new phlog
                        MainActivity.DBobj.daoAccess().addPhlog(phlogData); // add it to the databasse
                    }
                    //backHome.putExtra("title", titleText); //sending the title to the Main Activity for the ARL to deal with
                   // backHome.putExtra("description", descriptionText); //sending the description to the Main Activity for the ARL to deal with
                    setResult(RESULT_OK, backHome); // setting result to okay
                    finish(); // return to the MainActivity

                }else if(image == null){ // if theres no image im not counting it as a phlog, kinda defeats the whole purpose of the photo thing. Also Goeffs app crashes if you try to save without a photo so yeah, this is my way of dealing with that.
                    Toast.makeText(this, "Need at least an image for valid phlog!", Toast.LENGTH_SHORT).show();
                    finish(); // return to the Main Activity
                }
                else{
                    setResult(RESULT_CANCELED,backHome); // in case something goes wrong
                    finish(); // return to the Main Activity
                }
                break;
            case (R.id.gallery):
                startGallery.launch("image/*" + theTime ); // Adding theTime to the input as a string so each image I store has a unique name with the time the phlog was made
                break;
            case (R.id.camera):
                photoUri = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).toString() + "/" + theTime  +
                        getResources().getString(R.string.camera_photo_name))); //I append the time to the end of the file string so that each picture has a unique file name without me having to do extra work
                Log.i("ALERT","PHOTO URI : " + photoUri.toString());
                runCameraPhotoApp.launch(photoUri);
               break;
            case (R.id.video_button):
                videoUri = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MOVIES).toString() + "/" +
                        getResources().getString(R.string.camera_video_name)));
                Log.i("ALERT","VIDEO URI : " + videoUri.toString());
                runCameraVideoApp.launch(videoUri);
                break;
            case (R.id.delete):
                if(checkEntity!=null) {
                    MainActivity.DBobj.daoAccess().deletePhlog(checkEntity); //Uses DBobj in the main activity to delete this entity
                }else{
                    Toast.makeText(this, "No phlog to delete!", Toast.LENGTH_SHORT).show();
                }
                finish();
                break;
            case (R.id.email):
            startContacts.launch(null);
           // finish();
            break;
            case (R.id.the_image):
                Bundle bundleToFragment = new Bundle();
                bundleToFragment.putParcelable("show_image", image); //passing the picture to the fragment to display
                CustomFrag theDialogFragment = new CustomFrag(); // making a new fragment
                theDialogFragment.setArguments(bundleToFragment);
                theDialogFragment.show(getFragmentManager(), "my_frag");
                break;
                //basically I just made a dialog fragment so you can actually see the whole image on your screen instead of it being like a 10dp by 10dp box

        }

    }
    // GALLERY, PHOTO, AND VIDEO ACTIVITY RESULT LAUNCHERS.
    ActivityResultLauncher<String> startGallery = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri resultUri) {
                    Intent nextActivity;
                    Log.i("LOG onActivityResult","OnActivityResult launched!?");
                    if (resultUri != null) { // If a photo was selected
                        image = resultUri; // the global URI variable is equal to the resultURi
                        theBigpicture.setImageURI(image); // set the imageview to be the picture the user selected
                    } else {
                        Toast.makeText(Editor.this,"No photo selected",Toast.LENGTH_LONG).show();
                    }

                }
            });
    ActivityResultLauncher<Uri> runCameraPhotoApp = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            new ActivityResultCallback<Boolean>() {
                Intent nextActivity;
                @Override
                public void onActivityResult(Boolean result) {

                    if (result) { //if a photo was taken successfully
                        theBigpicture.setImageURI(null);
                        image = photoUri; //setting the global URI to the photoURI I declared previously
                    theBigpicture.setImageURI(image); // set the imageview to be the picture the user took
                    }else{

                    }
                }
            });
    ActivityResultLauncher<Uri> runCameraVideoApp = registerForActivityResult(
//----I want to use CaptureVideo that returns Boolean, but it won't compile.
            new ActivityResultContracts.TakeVideo(),
            new ActivityResultCallback<Bitmap>() {
                @Override
                public void onActivityResult(Bitmap result) {
                    if(videoUri != null){
                        Log.i("ALERT", "I AM NOT NULL!");
                        theSilverScreen.setVideoURI(videoUri);
                        theSilverScreen.setVisibility(View.VISIBLE);
                        theSilverScreen.setClickable(true);
                        theSilverScreen.setOnTouchListener(Editor.this::onTouch);
                        Toast.makeText(Editor.this, "Click on the video to play it! Click Again to Pause!", Toast.LENGTH_SHORT).show();
                    }

                }
            });
    //ALL EMAIL STUFF FROM HERE ON OUT PRETTY STANDARD STUFF
    // MOST OF IT IS FROM GOEFFS EXAMPLES SO
    ActivityResultLauncher<Void> startContacts = registerForActivityResult( // Standard Stuff
            new ActivityResultContracts.PickContact(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri contactData) {

                    Cursor contactsCursor;
                    int contactId;
                    String contactName;
                    if (contactData != null) {
                        contactsCursor = getContentResolver().query(contactData,null,null,null,null);
                        if (contactsCursor.moveToFirst()) {
                            contactId = contactsCursor.getInt(contactsCursor.getColumnIndexOrThrow(
                                    ContactsContract.Contacts._ID));
                            contactName = contactsCursor.getString(contactsCursor.getColumnIndexOrThrow(
                                    ContactsContract.Contacts.DISPLAY_NAME));
                            setEmailAddress(contactName,searchForEmailAddressById(contactId));
                        }
                        contactsCursor.close();

                    } else {
                        Toast.makeText(Editor.this,"No contact selected",Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            });
    private String searchForEmailAddressById(int contactId) { //Standard stuff, shout out to Goeff

        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Email.CONTACT_ID,
                ContactsContract.CommonDataKinds.Email.DATA
        };
        Cursor emailCursor;
        String emailAddress;

        emailCursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,projection,"CONTACT_ID = ?",
                new String[]{Integer.toString(contactId)},null);
        if (emailCursor.moveToFirst()) {
            emailAddress = emailCursor.getString(emailCursor.getColumnIndexOrThrow(
                    ContactsContract.CommonDataKinds.Email.DATA));
        } else {
            emailAddress = null;
        }
        emailCursor.close();
        return(emailAddress);
    }
    //-------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------
    private void setEmailAddress(String name,String emailAddress) {

        String[] emailToSendTo = new String[1];
        if (emailAddress != null) {
            ArrayList<Uri> uris = new ArrayList<Uri>(); // if i need to send multiple uris need to send an arraylist of them
            uris.add(image);
            String titleText = theTitle.getText().toString(); //getting the Text the user entered in the EditTExt
            String descriptionText = theDescription.getText().toString(); //getting the Text the user entered in the EditTExt
            String coordinateText = coordinateView.getText().toString(); // GETTING the coordinates the user entered in the EditText
            String timeText = timeView.getText().toString(); // getting the time from the textview
            String addyText = addyView.getText().toString(); //getting the address from the textview
            Intent emailIntent;
            emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE); // new emailIntent
            emailIntent.setType("plain/text");
            emailToSendTo[0] = emailAddress;
            emailIntent.putExtra(Intent.EXTRA_EMAIL,emailToSendTo); //setting recepient of email
         //   emailIntent.putExtra(Intent.EXTRA_STREAM,image); // emailing the actual photo
          //  Log.i("ALERT", "IMAGE URI: " + image.toString());
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, titleText); //setting the subject line of the email as the title of the phlog
            emailIntent.putExtra(Intent.EXTRA_TEXT,"Phlog Entry at  " + timeText + ".   Description : " +descriptionText + ".   At coordinates- " + coordinateText + ". (decoded) : " + addyText); //Body of email
            if(videoUri !=null) {
                uris.add(videoUri);
            }
            emailIntent.putExtra(Intent.EXTRA_STREAM,uris); // emailing the actual photo and/or video
            startActivity(Intent.createChooser(emailIntent,"Choose ..."));
            finish();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) { // for the videoview, needs this method to respond to clicks
        if(theSilverScreen.isPlaying()){
            theSilverScreen.pause();
        }else {
            theSilverScreen.start();
        }



        return false;
    }
}

