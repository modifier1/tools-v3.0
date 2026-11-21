package com.example.simswapai

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import android.graphics.Color
import android.view.Gravity

class MainActivity : Activity() {
    private var photo: Uri? = null
    private var video: Uri? = null
    private lateinit var status: TextView
    private val pickPhoto = 10
    private val pickVideo = 11

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(28, 36, 28, 24)
            setBackgroundColor(Color.rgb(10, 13, 42))
            layoutDirection = LinearLayout.LayoutDirection.RTL
        }
        fun text(value: String, size: Float, color: Int = Color.WHITE) =
            TextView(this).apply {
                text = value; textSize = size; setTextColor(color)
                gravity = Gravity.CENTER; setPadding(8, 18, 8, 18)
            }
        root.addView(text("SimSwap AI", 30f))
        root.addView(text("تعویض چهره در ویدئو و GIF", 17f, Color.CYAN))
        fun addButton(label: String, action: () -> Unit) {
            root.addView(Button(this).apply {
                this.text = label
                setOnClickListener { action() }
            })
        }
        addButton("انتخاب عکس چهره") { choose(pickPhoto, "image/*") }
        addButton("انتخاب ویدئو") { choose(pickVideo, "video/*") }
        val online = RadioButton(this).apply { text = "پردازش آنلاین (نیازمند سرور GPU)"; setTextColor(Color.WHITE); isChecked = true }
        val offline = RadioButton(this).apply { text = "پردازش آفلاین (مدل محلی هنوز نصب نشده)"; setTextColor(Color.WHITE) }
        val group = RadioGroup(this).apply { orientation = RadioGroup.VERTICAL; addView(online); addView(offline) }
        root.addView(group)
        addButton("شروع") {
            status.text = when {
                photo == null || video == null -> "ابتدا عکس و ویدئو را انتخاب کنید."
                offline.isChecked -> "موتور آفلاین هنوز به مدل ONNX سازگار متصل نشده است."
                else -> "رابط آماده است؛ برای پردازش واقعی باید سرور GPU پیکربندی شود."
            }
        }
        status = text("برای شروع، عکس و ویدئو را انتخاب کنید.", 14f, Color.LTGRAY)
        root.addView(status)
        root.addView(text("سازگار با Android 8 به بالا؛ عملکرد به سخت‌افزار وابسته است.", 12f, Color.LTGRAY))
        setContentView(ScrollView(this).apply { addView(root) })
    }

    private fun choose(code: Int, mime: String) {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE); type = mime
        }
        startActivityForResult(i, code)
    }

    @Deprecated("Legacy callback for broad device compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) return
        when (requestCode) {
            pickPhoto -> photo = data?.data
            pickVideo -> video = data?.data
        }
        status.text = "عکس: ${if (photo != null) "انتخاب شد" else "انتخاب نشده"} | ویدئو: ${if (video != null) "انتخاب شد" else "انتخاب نشده"}"
    }
}
