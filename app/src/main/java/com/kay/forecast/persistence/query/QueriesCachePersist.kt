package com.kay.forecast.persistence.query

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

interface QueryCachePersistor {
    fun getAll(): Map<String, QueryInfo>
    fun persist(info: QueryInfo)
}

class QueryCachePersistImpl(
    app: Application,
    private val gson: Gson = Gson()
) : QueryCachePersistor {
    private val type = object : TypeToken<QueryInfo>() {}.type
    private val sharedPreferences =
        app.getSharedPreferences(QUERIES_CACHE_FILE, Context.MODE_PRIVATE)

    override fun getAll(): Map<String, QueryInfo> {
        val mutableMap = mutableMapOf<String, QueryInfo>()
        sharedPreferences.all.forEach { (key, jsonStr) ->
            mutableMap[key] = gson.fromJson(jsonStr as String, type)
        }
        return mutableMap

    }

    override fun persist(info: QueryInfo) =
        sharedPreferences.edit { putString(info.query, gson.toJson(info, type)) }


    @SuppressLint("ApplySharedPref")
    @VisibleForTesting
    fun clearForTesting() {
        sharedPreferences.edit().clear().commit()
    }

    companion object {
        const val QUERIES_CACHE_FILE = "queries.cache"
    }
}