package pl.onrwir.kidsdrawingapp

import android.app.Activity
import android.app.Dialog
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get

class MainActivity : AppCompatActivity() {

    private var drawingView : DrawingView? = null
    private var mImageButtonCurrentPaint : ImageButton? = null

    val requestPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            permissions ->
            permissions.entries.forEach{
                val permissionName = it.key
                val isGranted = it.value

                if (isGranted){
                    Toast.makeText(
                        this,
                        "Permission granted, now you can read the storage files.",
                        Toast.LENGTH_LONG
                    ).show()
                }else{
                    if (permissionName==android.Manifest.permission.READ_EXTERNAL_STORAGE){
                        Toast.makeText(
                            this,
                            "Permission denied.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingView = findViewById(R.id.drawing_view)
        drawingView?.setSizeForBrush(20.toFloat())

        val linearLayoutPaintColors = findViewById<LinearLayout>(R.id.ll_paint_colors)
        mImageButtonCurrentPaint = linearLayoutPaintColors[1] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
        )

        val ib_brush : ImageButton = findViewById(R.id.ib_brush)
        ib_brush.setOnClickListener {
            showBrushSizeChoserDialog()
        }

        val ibGallery : ImageButton = findViewById(R.id.ib_gallery)
        ibGallery.setOnClickListener{
            requestStoragePermission()
        }

    }

    private fun showBrushSizeChoserDialog(){
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush size: ")
        val smallBtn = brushDialog.findViewById<ImageButton>(R.id.ib_small_brush)
        val mediumBtn = brushDialog.findViewById<ImageButton>(R.id.ib_medium_brush)
        val largeBtn = brushDialog.findViewById<ImageButton>(R.id.ib_large_brush)
        smallBtn.setOnClickListener{
            drawingView?.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        }
        mediumBtn.setOnClickListener{
            drawingView?.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }
        largeBtn.setOnClickListener{
            drawingView?.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()
        }
        brushDialog.show()
    }

    fun paintClicked(view: View){
        if (view !== mImageButtonCurrentPaint){
            val imageButton = view as ImageButton
            val colorTag = imageButton.tag.toString()
            drawingView?.setColor(colorTag)

            imageButton.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
            )
            mImageButtonCurrentPaint?.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_normal)
            )
            mImageButtonCurrentPaint = view
        }
    }

    private fun requestStoragePermission(){
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {

                Toast.makeText(
                    this,
                    "Permission granted, now you can read the storage files.",
                    Toast.LENGTH_LONG
                ).show()

                //Permisssion is granted
//                requestPermission.launch(
//                arrayOf(
//                android.Manifest.permission.READ_EXTERNAL_STORAGE
//            // TODO - Add writing external storage permission
//                    )
//                )
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                //Additional rationale should be displayed

                showRationalDialog("Kids Drawing App", "Kids Drawing App needs to Access Your External Storage")

            }
            else -> {
                //Permission has not been asked yet
                requestPermission.launch(
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                    // TODO - Add writing external storage permission
                    )
                )
            }


        }
//        if (ActivityCompat.shouldShowRequestPermissionRationale(
//                this,
//                android.Manifest.permission.READ_EXTERNAL_STORAGE)
//        ){
//            showRationalDialog("Kids Drawing App", "Kids Drawing App needs to Access Your External Storage")
//        }else{
//            requestPermission.launch(
//                arrayOf(
//                android.Manifest.permission.READ_EXTERNAL_STORAGE
//            // TODO - Add writing external storage permission
//                )
//            )
//        }
    }

    private fun showRationalDialog(
        title: String,
        message: String
    ){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder. setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK"){dialog, _ ->
                requestPermission.launch(
                    arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                        // TODO - Add writing external storage permission
                    )
                )
                dialog.dismiss()
            }
            .setNegativeButton("Cancel"){dialog, _ ->
                dialog.dismiss()
            }

        builder.create().show()
    }
}