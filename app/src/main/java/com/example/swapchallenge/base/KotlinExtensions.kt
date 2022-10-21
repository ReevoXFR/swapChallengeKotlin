package com.example.swapchallenge.base

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * KotlinExtensions to store the shared elements between fragments.
 *
 * @fun hideKeyboard() can be called from any fragment to close the keyboard, if active.
 */

fun hideKeyboard(view: View, context: Context) {
    val manager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    manager.hideSoftInputFromWindow(view.windowToken, 0)
}