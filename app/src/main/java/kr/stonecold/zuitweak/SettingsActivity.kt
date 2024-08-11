package kr.stonecold.zuitweak

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kr.stonecold.zuitweak.ui.theme.ZuiTweakTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.topjohnwu.superuser.Shell
import kr.stonecold.zuitweak.common.Constants
import kr.stonecold.zuitweak.common.SharedPrefsUtil
import kr.stonecold.zuitweak.common.Util

private lateinit var prefsSnapshot: Map<String, *>

class SettingsActivity : ComponentActivity() {
    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //// libsu 초기화
        //Shell.enableVerboseLogging = BuildConfig.DEBUG
        //Shell.setDefaultBuilder(
        //    Shell.Builder.create()
        //        .setFlags(Shell.FLAG_MOUNT_MASTER)
        //        .setTimeout(10)
        //)
        //// Shell.getShell을 호출하여 캐싱된 메인 쉘을 사전 로드합니다.
        //Shell.getShell { }

        SharedPrefsUtil.init(this)
        if (SharedPrefsUtil.isInitialized) {
            prefsSnapshot = SharedPrefsUtil.getAllOptions()
        }

        setContent {
            ZuiTweakTheme {
                Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                ) {
                    AppContent()
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}

private fun reloadSettings(context: Context) {
    val packagesToReload = mutableListOf<String>()
    if (!SharedPrefsUtil.isInitialized || !::prefsSnapshot.isInitialized) {
        Toast.makeText(context, "Module is not loaded.", Toast.LENGTH_SHORT).show()
        return
    }

    for (hook in HookManager.getAllHooks()) {
        val oldState = prefsSnapshot.getOrDefault(hook.javaClass.simpleName, hook.menuItem.defaultSelected)
        val newState = SharedPrefsUtil.getOptionValue(hook.javaClass.simpleName, hook.menuItem.defaultSelected)
        if (oldState != newState) {
            packagesToReload.addAll(hook.hookTargetPackage)
        }
    }

    if (packagesToReload.contains("android")) {
        Toast.makeText(context, "A reboot is required if any settings related to packages named 'android' have been changed.", Toast.LENGTH_SHORT).show()
        return
    }
    prefsSnapshot = SharedPrefsUtil.getAllOptions()

    if (packagesToReload.isEmpty()) {
        Toast.makeText(context, "No changes detected.", Toast.LENGTH_SHORT).show()
        return
    }

    val commands = mutableListOf<String>()

    packagesToReload.distinct()
        .filter { it != "android" }
        .forEach { pkg ->
            val result = Shell.cmd("pidof $pkg").exec()
            if (result.isSuccess && result.out.isNotEmpty()) {
                commands.add("killall $pkg")
            }
        }

    if (commands.isEmpty()) {
        Toast.makeText(context, "No running processes to kill.", Toast.LENGTH_SHORT).show()
        return
    }

    try {
        val result = Shell.cmd(*commands.toTypedArray()).exec()
        if (!result.isSuccess) {
            Toast.makeText(context, "Failed to reload settings.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Settings reloaded successfully.", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to reload settings: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun AppContent(modifier: Modifier = Modifier) {
    val deviceModel = remember { Constants.deviceModel }
    var isRooted by remember { mutableStateOf(false) }
    var region by remember { mutableStateOf("") }
    val isModuleEnabled by remember { mutableStateOf(SharedPrefsUtil.isInitialized) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val categories = HookOptions.getHookOptions(context)

    LaunchedEffect(Unit) {
        scope.launch {
            isRooted = Util.isDeviceRooted()
            region = Constants.deviceRegion
        }
    }

    LazyColumn(
            modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
    ) {
        item {
            Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                            text = "ZuiTweak",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                    )
                    Row {
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                                text = "by StoneCold",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        val intent = Intent(context, SettingsViewActivity::class.java)
                        context.startActivity(intent)
                    }
                ) {
                    Text(text = "SettingsView")
                }
                Button(
                        onClick = {
                            reloadSettings(context)
                        },
                        enabled = isRooted && isModuleEnabled
                ) {
                    Text(text = "Reload")
                }
            }

            Text(
                    text = "Xiaoxin Pad Pro 12.7 (TB371FC), Legion Y700 2023 (TB320FC)의 숨겨진 기능을 활성화 하거나 오류를 수정합니다.",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                    text = "정보",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                    text = "Device Model: $deviceModel",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 16.dp)
            )
            if (deviceModel != "TB371FC" && deviceModel != "TB320FC") {
                Row {
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                            text = "미지원 단말기",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red,
                            modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
            Row(
                    modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                        text = "Region: ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                        text = region,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (region == "UNKNOWN") Color.Red else MaterialTheme.colorScheme.onSurface
                )
            }
            Row(
                    modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                        text = "Rooting Status: ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                )
                Text(
                        text = if (isRooted) "Rooted" else "Not Rooted",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isRooted) MaterialTheme.colorScheme.onSurface else Color.Red
                )
            }
            Row(
                    modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                        text = "Module Status: ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                )
                Text(
                        text = if (isModuleEnabled) "Enabled" else "Disabled",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isModuleEnabled) MaterialTheme.colorScheme.onSurface else Color.Red
                )
            }
        }

        val isSupportedDevice = (deviceModel == "TB371FC" || deviceModel == "TB320FC")

        items(categories) { (category, options) ->
            CategorySection(category = category, options = options.map { it.copy(isEnabledOption = isSupportedDevice && it.isEnabledOption) }, context = context)
        }

        item {
            ConfigSection(context = context)
            NoticeSection()
        }
    }
}

@Composable
fun ConfigSection(context: Context) {
    var isIconHidden by remember { mutableStateOf(isLauncherIconHidden(context)) }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
                text = "설정",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text(
                    text = "런처에서 아이콘 숨기기",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                    checked = isIconHidden,
                    onCheckedChange = {
                        isIconHidden = it
                        setLauncherIconHidden(context, it)
                        Toast.makeText(context, "설정 변경됨. 변경사항을 적용하려면 재부팅하십시오.", Toast.LENGTH_LONG).show()
                    }
            )
        }
        Text(
                text = "이 옵션을 활성화하면, 앱 아이콘이 런처에서 숨겨집니다. 아이콘을 다시 표시하려면, 이 옵션을 비활성화하십시오.",
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun NoticeSection() {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
                text = "Notice",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
                text = "• 일부 설정은 UnfuckZUI에서 가져왔습니다.",
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 8.dp)
        )
    }
}

fun isLauncherIconHidden(context: Context): Boolean {
    val componentName = ComponentName(context, MainActivity::class.java)
    val pm = context.packageManager
    val componentEnabledSetting = pm.getComponentEnabledSetting(componentName)
    return componentEnabledSetting == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
}

fun setLauncherIconHidden(context: Context, hidden: Boolean) {
    val componentName = ComponentName(context, MainActivity::class.java)
    val pm = context.packageManager
    pm.setComponentEnabledSetting(
            componentName,
            if (hidden) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
            PackageManager.DONT_KILL_APP
    )
}

@Composable
fun CategorySection(category: String, options: List<HookOption>, context: Context) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
                text = category,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        options.forEach { option ->
            if (SharedPrefsUtil.isInitialized && !option.isEnabledOption) {
                SharedPrefsUtil.deleteOptionValue(option.key)
            }
            OptionItem(context = context, option = option)
        }
    }
}

@Composable
fun OptionItem(context: Context, option: HookOption, modifier: Modifier = Modifier) {
    var isChecked by remember { mutableStateOf(SharedPrefsUtil.isInitialized && SharedPrefsUtil.getOptionValue(option.key, option.defaultEnabled)) }
    val isEnabled by remember { mutableStateOf(SharedPrefsUtil.isInitialized && option.isEnabledOption) }

    Column(
            modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
    ) {
        Row(
                verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                    text = option.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                    checked = isChecked,
                    onCheckedChange = {
                        isChecked = it
                        if (SharedPrefsUtil.isInitialized) {
                            SharedPrefsUtil.setOptionValue(option.key, isChecked)
                        } else {
                            Toast.makeText(context, "Settings changed. Please reboot for changes to take effect.", Toast.LENGTH_LONG).show()
                        }
                    },
                    enabled = isEnabled
            )
        }
        Text(
                text = option.description,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ZuiTweakTheme {
        AppContent()
    }
}
