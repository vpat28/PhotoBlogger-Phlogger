package edu.miami.cs.vraj_patel.phlogger;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
@Dao
public interface PhloggerAccess {
    @Query("SELECT * FROM RatedText ORDER BY the_time ASC")
    List<PhloggerEntity> fetchAllPhlogs();

    @Query("SELECT * FROM RatedText where the_time LIKE :this_id")
    PhloggerEntity getByTime(String this_id);

    @Insert
    void addPhlog(PhloggerEntity phloggerEntity);
    @Update
    void updatePhlog(PhloggerEntity phloggerEntity);
    @Delete
    void deletePhlog(PhloggerEntity phloggerEntity);
}
