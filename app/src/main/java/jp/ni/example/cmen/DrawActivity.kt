package jp.ni.example.cmen

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.drawing.*

class DrawActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawing)

        val intent = getIntent()

        var ex = intent.extras
        if (ex != null) {
            cmenView.chamferSize = ex.getFloat(MyModel.CHAMFER_SIZE.name,MyModel.CHAMFER_SIZE.def)
            cmenView.toolDiameter = ex.getFloat(MyModel.TOOL_DIAMETER.name, MyModel.TOOL_DIAMETER.def)
            cmenView.clearance = ex.getFloat(MyModel.CLEARANCE.name, MyModel.CLEARANCE.def)
            cmenView.originalPoint = ex.getInt(MyModel.ORIGINAL.name, MyModel.ORIGINAL.def)
        }
    }

    fun onBackClick(v: View) {
        finish()
    }
}