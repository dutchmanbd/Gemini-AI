package com.ticonsys.geminiai.domain.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class SharedPref(
    private val context: Context,
    private val storageName: String = context.packageName
) {

    private val sharedPref: SharedPreferences
        get() = context.getSharedPreferences(
            storageName,
            Activity.MODE_PRIVATE
        )

    private val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
    private val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

    private val encryptedPref: SharedPreferences =
        EncryptedSharedPreferences.create(
            storageName,
            mainKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    fun read(key: String, defValue: String) = encryptedPref.getString(key, defValue)!!

    fun write(key: String, value: String) {
        encryptedPref.edit {
            putString(key, value)
        }
    }

    fun read(key: String, defValue: Boolean) =
        encryptedPref.getBoolean(key, defValue)

    fun write(key: String, value: Boolean) {
        encryptedPref.edit {
            putBoolean(key, value)
//            apply()
        }
    }

    fun read(key: String, defValue: Int) =
        encryptedPref.getInt(key, defValue)

    fun write(key: String, value: Int) {
        encryptedPref.edit {
            putInt(key, value)
//            apply()
        }
    }

    fun read(key: String, defValue: Long) =
        encryptedPref.getLong(key, defValue)

    fun write(key: String, value: Long) {
        encryptedPref.edit {
            putLong(key, value)
        }
    }

    fun clear() {
        try {
            sharedPref.edit {
                clear()
            }
        } catch (e: Exception){
            Log.e("SharedPref", "clear: ${e.localizedMessage}")
        }

    }

}