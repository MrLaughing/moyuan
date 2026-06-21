package com.mrlaughing.moyuan.di

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mrlaughing.moyuan.data.local.db.MoyuanDatabase
import com.mrlaughing.moyuan.data.remote.ApiKeyInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import androidx.room.Room

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
  @Provides
  fun provideMoyuanDatabase(@ApplicationContext context: Context): MoyuanDatabase {
    return Room.databaseBuilder(context, MoyuanDatabase::class.java, "moyuan_db")
      .addMigrations(MoyuanDatabase.MIGRATION_1_2, MoyuanDatabase.MIGRATION_2_3)
      .fallbackToDestructiveMigration()
      .build()
  }

  @Provides
  fun provideBaseSnapshotDao(db: MoyuanDatabase) = db.baseSnapshotDao()

  @Provides
  fun provideDailyRecordDao(db: MoyuanDatabase) = db.dailyRecordDao()

  @Provides
  fun providePlantStateDao(db: MoyuanDatabase) = db.plantStateDao()

  @Provides
  fun provideGardenMetaDao(db: MoyuanDatabase) = db.gardenMetaDao()

  @Provides
  fun provideBookTrackingDao(db: MoyuanDatabase) = db.bookTrackingDao()

  @Provides
  fun provideGson(): Gson = GsonBuilder()
      .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
      .setLenient()
      .create()

  @Provides
  fun provideOkHttpClient(apiKeyInterceptor: ApiKeyInterceptor): OkHttpClient {
    return OkHttpClient.Builder()
      .addInterceptor(apiKeyInterceptor)
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .build()
  }
}
