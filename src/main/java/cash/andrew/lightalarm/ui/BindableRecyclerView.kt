package cash.andrew.lightalarm.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cash.andrew.lightalarm.ui.BindableRecyclerAdapter.BindableViewHolder

/**
 * A recycler view adapter that makes life better.
 *
 * @param <I> The backing data model for the views.
 * @author Andrew Reitz
 * @since 2.0.0
 */
abstract class BindableRecyclerAdapter<I> : RecyclerView.Adapter<BindableViewHolder>() {

    /**
     * Create a new view for the view type.
     *
     * @param layoutInflater inflater to use for inflating new views.
     * @param viewType user defined groupId to specify what view to return. See
     *                 {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     * @param parent   The parent view of the view being created.
     * @return a newly created view.
     */
    abstract fun newView(layoutInflater: LayoutInflater, viewType: Int, parent: ViewGroup): View

    /**
     * Retrieve an item from the data store backing this adapter.
     *
     * @param position the position in the list to get the data for.
     * @return the retrieve item for the specified position.
     */
    abstract fun getItem(position: Int): I

    /**
     * Bind the data from the item into the view.
     *
     * @param item     the item whom's data will be bound to the view for displaying relevant content.
     * @param view     the view to display the data in.
     * @param position the position in the view list.
     */
    abstract fun bindView(item: I, view: View, position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindableViewHolder =
        BindableViewHolder(
            newView(
                LayoutInflater.from(parent.context),
                viewType,
                parent
            )
        )


    override fun onBindViewHolder(holder: BindableViewHolder, position: Int) {
        bindView(getItem(position), holder.itemView, position)
    }

    /**
     * A view holder that does nothing.
     */
    class BindableViewHolder(view: View): RecyclerView.ViewHolder(view)
}
