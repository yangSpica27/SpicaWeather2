package me.spica.spicaweather2.view.list

import androidx.recyclerview.widget.DiffUtil

abstract class SimpleDiffCallback<T : Any> : DiffUtil.ItemCallback<T>() {
  final override fun areItemsTheSame(
    oldItem: T,
    newItem: T,
  ) = oldItem == newItem
}
