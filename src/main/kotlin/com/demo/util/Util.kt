package com.demo.util

fun containsSpecialCharacters(input: String): Boolean {
    val regex =
        Regex("[^A-Za-z0-9 ]") // Define a regex pattern to match any characters that are not letters, digits, or spaces
    return regex.find(input) != null
}
