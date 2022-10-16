package khrushchev.ilya.scrollhelper

interface ScrollHelperListener {
    fun getItemCount(): Int
    fun scrollToPosition(position: Int)
    fun getCharAtPosition(position: Int): Char
}