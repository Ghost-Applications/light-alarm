package cash.andrew.lightalarm.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cash.andrew.lightalarm.R.layout

class AlarmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_alarm)
    }
}
