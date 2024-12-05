package me.spica.spicaweather2.view.list

import androidx.recyclerview.widget.RecyclerView

abstract class DiffAdapter<T, I, VH : RecyclerView.ViewHolder>(
  differFactory: ListDiffer.Factory<T, I>,
) : RecyclerView.Adapter<VH>() {
  private val differ = differFactory.new(@Suppress("LeakingThis") this)

  final override fun getItemCount() = differ.currentList.size

  /** The current list of [T] items. */
  val currentList: List<T>
    get() = differ.currentList

  /**
   * Get a [T] item at the given position.
   * @param at The position to get the item at.
   * @throws IndexOutOfBoundsException If the index is not in the list bounds/
   */
  fun getItem(at: Int) = differ.currentList[at]

  /**
   * Dynamically determine how to update the list based on the given instructions.
   * @param newList The new list of [T] items to show.
   * @param instructions The instructions specifying how to update the list.
   * @param onDone Called when the update process is completed. Defaults to a no-op.
   */
  fun submitList(
    newList: List<T>,
    instructions: I,
    onDone: () -> Unit = {},
  ) {
    differ.submitList(newList, instructions, onDone)
  }
}
