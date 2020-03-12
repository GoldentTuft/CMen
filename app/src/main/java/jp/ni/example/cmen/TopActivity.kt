package jp.ni.example.cmen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.top.*

object MyModel {
    object CHAMFER_SIZE {
        val def = 5f
        val name = "CHAMFER_SIZE"
    }
    
    object TOOL_DIAMETER {
        val def = 20f
        val name = "TOOL_DIAMETER"
    }
    
    object CLEARANCE {
        val def = 1f
        val name = "CLEARANCE"
    }

    object ORIGINAL {
        val def = 0
        val name = "ORIGINAL"

        // 色々良くないとは思う。
        enum class  POINT(val id: Int) {
            RIGHT_TOP(0),
            RIGHT_BOTTOM(1)
        }
    }
}

class TopActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        setContentView(R.layout.top)
        editText_chamferSize.setText(MyModel.CHAMFER_SIZE.def.toInt().toString(), TextView.BufferType.NORMAL)
        editText_toolDiameter.setText(MyModel.TOOL_DIAMETER.def.toInt().toString(), TextView.BufferType.NORMAL)
        editText_clearance.setText(MyModel.CLEARANCE.def.toInt().toString(), TextView.BufferType.NORMAL)
    }

    fun onCalcClick(v: View) {

        var intent = Intent(applicationContext, DrawActivity::class.java)

        intent.putExtra(MyModel.CHAMFER_SIZE.name, editText_chamferSize.text.toString().toFloat())
        intent.putExtra(MyModel.TOOL_DIAMETER.name, editText_toolDiameter.text.toString().toFloat())
        intent.putExtra(MyModel.CLEARANCE.name, editText_clearance.text.toString().toFloat())
        intent.putExtra(MyModel.ORIGINAL.name, spinner_originalPoint.selectedItemPosition)

        startActivity(intent)
    }


}
