package com.kay.forecast.persistence.query

import android.app.Application
import android.content.Context
import androidx.annotation.VisibleForTesting
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

interface QueryCachePersistor {
    fun get(): Map<String, QueryInfo>
    fun persist(snapshot: Map<String, QueryInfo>)
}

class QueryCachePersistImpl(
    app: Application,
    private val gson: Gson = Gson()
) : QueryCachePersistor {
    private val type = object : TypeToken<MutableMap<String, QueryInfo>>() {}.type
    private val sharedPreferences =
        app.getSharedPreferences(QUERIES_CACHE_FILE, Context.MODE_PRIVATE)

    override fun get(): Map<String, QueryInfo> {
        val json = sharedPreferences.getString(QUERIES_CACHE_KEY, "")
        return if (!json.isNullOrEmpty()) {
            gson.fromJson(json, type)
        } else {
            mutableMapOf()
        }
    }

    override fun persist(snapshot: Map<String, QueryInfo>) {
        sharedPreferences.edit().run {
            putString(QUERIES_CACHE_KEY, gson.toJson(snapshot, type))
            apply()
        }
    }

    @VisibleForTesting
    fun clearForTesting() {
        sharedPreferences.edit().run {
            putString(QUERIES_CACHE_KEY, "")
            commit()
        }
    }

    companion object {
        const val QUERIES_CACHE_FILE = "queries.cache"
        const val QUERIES_CACHE_KEY = "queries"
    }
}