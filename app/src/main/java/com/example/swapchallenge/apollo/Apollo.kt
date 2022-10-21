package com.example.swapchallenge.apollo

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

/**
 * This class it's used to connect to the GraphQL & to invoke the apolloClient
 */

private class AuthorizationInterceptor(val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().build()

        return chain.proceed(request)
    }
}

private var instance: ApolloClient? = null

fun apolloClient(context: Context): ApolloClient {

    if (instance != null) {
        return instance!!
    }

    val okHttpClient =
        OkHttpClient.Builder().addInterceptor(AuthorizationInterceptor(context)).build()

    instance = ApolloClient.Builder().httpServerUrl("https://graphbrainz.herokuapp.com/graphql")
        .okHttpClient(okHttpClient).build()

    return instance!!
}