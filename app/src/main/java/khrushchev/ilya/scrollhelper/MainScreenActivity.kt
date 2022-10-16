package khrushchev.ilya.scrollhelper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class MainScreenActivity() : AppCompatActivity() {

    private val alphabet = 'A'.rangeTo('z').toList()
    private val adapter = BaseAdapter()
    private var scrollHelper: ScrollHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_screen_layout)
        findViewById<RecyclerView>(R.id.recycler)?.apply {
            scrollHelper = this@MainScreenActivity.findViewById(R.id.scroll_helper)
            adapter = this@MainScreenActivity.adapter
            scrollHelper?.setOnScrollHelperListener(object :ScrollHelperListener {
                override fun getItemCount(): Int {
                    return adapter?.itemCount ?: 0
                }

                override fun scrollToPosition(position: Int) {
                    this@apply.scrollToPosition(position)
                }

                override fun getCharAtPosition(position: Int): Char {
                    return (this@apply.findViewHolderForAdapterPosition(position) as BaseAdapter.AlphabetViewHolder).getChar()
                }
            })
        }
    }

    override fun onStart() {
        super.onStart()
        val resultList = 1.rangeTo(10).map { number ->
            alphabet.map {
                it + number.toString()
            }
        }.flatten().sorted()
        adapter.submitList(resultList)
    }

    override fun onDestroy() {
        super.onDestroy()
        scrollHelper?.removeOnScrollHelperListener()
    }
}