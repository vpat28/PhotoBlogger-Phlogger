package edu.miami.cs.vraj_patel.phlogger;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Dao
@Entity(tableName = "RatedText")
public class PhloggerEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "the_description")
    private String theDescription;
    @ColumnInfo(name = "the_title")
    private String theTitle;
    @ColumnInfo(name = "the_time")
    private String theTime;
    @ColumnInfo(name = "the_coordinates")
    private String theCoordinates;
    @ColumnInfo(name = "the_addy")
    private String theAddy;
    //@NonNull
    @ColumnInfo(name = "the_image")
    private String theImage;
    @ColumnInfo(name = "the_video")
    private String theVideo;


    public int getId() { //getting primary key
        return this.id;
    }
    public String getTheTitle(){
        return this.theTitle;
    }
    public String getTheDescription() { //getting the description
        return this.theDescription;
    }
    public String getTheImage() { //getting the image
        return this.theImage;
    }
    public String getTheVideo() { //getting the image
        return this.theVideo;
    }
    public String getTheTime() {
        return this.theTime;
    }
    public String getTheCoordinates() {
        return this.theCoordinates;
    }
    public String getTheAddy() {
        return this.theAddy;
    }
    public void setId(int newId) { //setting primary key
        this.id = newId;
    }
    public void setTheDescription(String newDescription) { //setting the description
        this.theDescription = newDescription;
    }
    public void setTheImage(String newImage) { //setting the image ID
        this.theImage = newImage;
    }
    public void setTheVideo(String newVideo) { //setting the image ID
        this.theVideo = newVideo;
    }
    public void setTheTitle(String newTitle){
        this.theTitle = newTitle;
    }
    public void setTheTime(String newTime) {
        this.theTime = newTime;
    }
    public void setTheCoordinates(String newCoordinates) {
        this.theCoordinates = newCoordinates;
    }
    public void setTheAddy(String newAddy) {
        this.theAddy = newAddy;
    }
}