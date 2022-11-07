package com.guidofe.pocketlibrary.data.remote.google_book

import java.lang.reflect.Type
import retrofit2.Converter
import retrofit2.Retrofit

class QueryFactory : Converter.Factory() {
    override fun stringConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, String>? {
        if (type != QueryData::class.java) {
            return null
        } else {
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