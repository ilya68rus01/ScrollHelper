package khrushchev.ilya.scrollhelper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class BaseAdapter() : ListAdapter<String, BaseAdapter.AlphabetViewHolder>(
    PersonalDiffUtil()
) {

    class AlphabetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun populate(string: String) {
            itemView.findViewById<TextView>(R.id.title)?.apply {
                text = string
            }
        }

        fun getChar() = itemView.findViewById<TextView>(R.id.title)?.text?.get(0) ?: ' '
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlphabetViewHolder {
        return AlphabetViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.alphabet_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AlphabetViewHolder, position: Int) {
        holder.populate(getItem(position))
    }
}

class PersonalDiffUtil() : DiffUtil.ItemCallback<String>() {

    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

}