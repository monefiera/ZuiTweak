package kr.stonecold.zuitweak

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent()
        intent.setClass(this, SettingsActivity::class.java)
        intent.setAction(Intent.ACTION_VIEW)
        startActivity(intent)
        finish()
    }
}
