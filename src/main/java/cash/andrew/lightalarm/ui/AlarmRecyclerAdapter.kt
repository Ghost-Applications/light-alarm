package cash.andrew.lightalarm.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import cash.andrew.lightalarm.data.Alarm
import cash.andrew.lightalarm.R.layout
import cash.andrew.lightalarm.data.AlarmKeeper
import dagger.hilt.android.scopes.ActivityRetainedScoped
import java.util.*
import javax.inject.Inject

class AlarmRecyclerAdapter(alarms: List<Alarm>) : BindableRecyclerAdapter<Alarm>() {

    private val alarms: MutableList<Alarm> = alarms
        .toMutableList()
        .apply { sortBy { it.time } }

    fun addAlarm(alarm: Alarm) {
        val oldList = alarms.toList()

        alarms.add(alarm)
        alarms.sortBy { it.time }

        val alarmDiffCallback = AlarmDiffCallback(oldList, alarms)
        val diffResult = DiffUtil.calculateDiff(alarmDiffCallback)
        diffResult.dispatchUpdatesTo(this)
    }

    fun removeAlarm(position: Int) {
        alarms.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun newView(layoutInflater: LayoutInflater, viewType: Int, parent: ViewGroup): View  = layoutInflater
        .inflate(layout.alarm_list_item_view, parent, false)

    override fun getItem(position: Int): Alarm = alarms[position]

    override fun bindView(item: Alarm, view: View, position: Int) = view.let { it as AlarmListItemView }.bind(item)

    override fun getItemCount(): Int = alarms.size
}

class AlarmDiffCallback(
    private val oldList: List<Alarm>,
    private val newList: List<Alarm>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]

}
