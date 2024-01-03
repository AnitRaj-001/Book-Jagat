package com.example.drawer_navigation.database

import androidx.room.Database
import androidx.room.RoomDatabase
import okhttp3.internal.Version

@Database(entities = [BookEntities ::class], version = 1)
abstract class BookDatabase : RoomDatabase(){
    abstract fun bookDao() : BookDao
}