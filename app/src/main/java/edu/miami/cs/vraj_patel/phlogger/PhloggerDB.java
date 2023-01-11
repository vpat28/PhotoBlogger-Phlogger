package edu.miami.cs.vraj_patel.phlogger;
import androidx.room.Dao;
import androidx.room.RoomDatabase;
import androidx.room.Database;

@Database(entities = {PhloggerEntity.class},version = 1, exportSchema = false)
public abstract class PhloggerDB extends RoomDatabase{
    public abstract PhloggerAccess daoAccess();
}
