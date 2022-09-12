package com.guidofe.pocketlibrary.model.repositories.google_book

import android.util.Log
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class QueryFactory: Converter.Factory() {
    override fun stringConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, String>? {
        if (type != QueryData::class.java) {
            return null
        }
        else {
            return Converter<QueryData, String> { value ->
                var response = ""
                if (value.text != null) {
                    response += "${value.text}+"
                }
                value.parameters.forEach {
                    response += "${it.key.name}:${it.value}+"
                }
                response = response.dropLast(1)
                response
            }
        }
    }
}