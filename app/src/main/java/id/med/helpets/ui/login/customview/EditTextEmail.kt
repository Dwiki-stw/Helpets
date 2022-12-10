package id.med.helpets.ui.login.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import id.med.helpets.R

class EditTextEmail : AppCompatEditText {
    private lateinit var backgroundEditText: Drawable

    constructor(context: Context) : super(context){
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        hint = ""
    }

    private fun init(){
        maxLines = 1
        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        backgroundEditText = ContextCompat.getDrawable(context, R.drawable.layout_shape) as Drawable
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun onTextChanged(s: CharSequence?, before: Int, count: Int, p3: Int) {
                setSelection(text!!.length)
                if (s != null){
                    if (s.isEmpty()){
                        error = "Email tidak boleh kosong"
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                        error = "Email tidak valid !"
                    } else {
                        error = null
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }
}