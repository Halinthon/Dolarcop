package com.dolarcop.app.data

import androidx.room.Dao
import androidx.room.OnConflictStrategy
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RatesCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(cache: RatesCache)

    @Query("SELECT * FROM rates_cache WHERE id = 0 LIMIT 1")
    suspend fun getCache(): RatesCache?
}
