package cash.andrew.lightalarm.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cash.andrew.lightalarm.R.layout
import cash.andrew.lightalarm.databinding.ActivityAlarmBinding

class AlarmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAlarmBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}
