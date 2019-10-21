package cash.andrew.lightalarm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import javax.inject.Inject

@ActivityScope
class AlarmRecyclerAdapter @Inject constructor(keeper: AlarmKeeper) : BindableRecyclerAdapter<Alarm>() {

    private val alarms: MutableList<Alarm> = keeper.alarms.toMutableList()

    fun addAlarm(alarm: Alarm) {
        alarms.add(alarm)
        // todo sort by time
        notifyItemInserted(alarms.size)
    }

    override fun newView(layoutInflater: LayoutInflater, viewType: Int, parent: ViewGroup): View  = layoutInflater
        .inflate(R.layout.alarm_list_item_view, parent, false)

    override fun getItem(position: Int): Alarm = alarms[position]

    override fun bindView(item: Alarm, view: View, position: Int) = view.let { it as AlarmListItemView }.bind(item)

    override fun getItemCount(): Int = alarms.size
}
