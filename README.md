# CipherRoom

Implements the Support interfaces of Room with [SQLCipher for Android.](https://www.zetetic.net/sqlcipher/sqlcipher-for-android/)

## Usage
This library provide `CipherRoomFactory`.
When you want to use an encrypted database, pass it to `RoomDatabase.Builder` openHelperFactory method.

```kotlin
val factory = CipherRoomFactory(password)
val database = Room.databaseBuilder(context, CacheDatabase::class.java, "db_name")
    .openHelperFactory(factory)
    .build()
```
