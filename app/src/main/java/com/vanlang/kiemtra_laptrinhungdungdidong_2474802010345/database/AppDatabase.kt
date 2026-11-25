package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.CartItemEntity
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.FavoriteEntity
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.OrderEntity
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.ProductEntity
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.UserEntity

@Database(
    entities = [
        UserEntity::class, ProductEntity::class, CartItemEntity::class,
        FavoriteEntity::class, OrderEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun shopDao(): ShopDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}