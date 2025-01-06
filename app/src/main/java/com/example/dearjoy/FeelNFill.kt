package com.example.dearjoy

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Color.RED
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.dearjoy.DrawingView.DrawingView
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FeelNFill.newInstance] factory method to
 * create an instance of this fragment.
 */
class FeelNFill : Fragment() {
    private lateinit var drawingView: DrawingView
    private lateinit var button: ImageView
    private lateinit var buttonChoose: ImageView
    private lateinit var insertImage: ImageView
    private lateinit var pen: ImageView
    private lateinit var erase: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Tidak perlu inisialisasi view di sini karena view belum ada saat onCreate
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout fragment
        return inflater.inflate(R.layout.fragment_feel_n_fill, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi tombol kembali
        val backButton: ImageView = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            // Kembali ke fragmen sebelumnya di back stack
            parentFragmentManager.popBackStack()
        }

        // Sekarang kita bisa mengakses komponen UI setelah view di-inflate
        drawingView = view.findViewById(R.id.drawing_view)
        button = view.findViewById(R.id.btn_save)
        buttonChoose = view.findViewById(R.id.choose_color)
        insertImage = view.findViewById(R.id.insert_image)
        pen = view.findViewById(R.id.pen)
        erase = view.findViewById(R.id.eraser)

        // Menambahkan listeners
        insertImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 501)
        }

        buttonChoose.setOnClickListener {
            openColorPicker()
        }

        pen.setOnClickListener{
            drawingView.pen()
        }

        erase.setOnClickListener {
            drawingView.erase()
        }

        val clearButton = view.findViewById<ImageView>(R.id.btn_clear)
        clearButton.setOnClickListener {
            drawingView.clear()  // Menghapus gambar
        }

        val strokeWidthSeekbar = view.findViewById<SeekBar>(R.id.stroke_width)
        strokeWidthSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                drawingView.setStrokeWidth(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        button.setOnClickListener {
            saveImage()
        }
    }

    // Membuka dialog pemilih warna
    @SuppressLint("ResourceType")
    private fun openColorPicker() {
        ColorPickerDialog
            .Builder(requireActivity())
            .setTitle("Pick Color")
            .setColorShape(ColorShape.SQAURE)
            .setDefaultColor(Color.BLACK)
            .setColorListener { color, _ ->
                drawingView.setColor(color)  // Mengubah warna gambar
            }
            .show()
    }

    // Menyimpan gambar ke penyimpanan eksternal
    private fun saveImage() {
        val bitmap = drawingView.getBitmap()

        // Lokasi penyimpanan di folder umum (Pictures)
        val saveDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val filename = "drawing_${System.currentTimeMillis()}.png"
        val file = File(saveDir, filename)

        try {
            // Membuat direktori jika belum ada
            if (!saveDir.exists()) {
                saveDir.mkdirs()
            }

            // Menyimpan gambar ke file
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
            }

            // Menambahkan file ke galeri
            MediaScannerConnection.scanFile(
                requireContext(), arrayOf(file.toString()), arrayOf("image/png"), null
            )

            Toast.makeText(requireContext(), "Image saved to Pictures as $filename", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Error saving image", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FeelNFill().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
