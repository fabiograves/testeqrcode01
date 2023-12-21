package com.rr.ars.ui.qrcode

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.rr.ars.databinding.FragmentQrcodeBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class QrcodeFragment : Fragment() {

    private var _binding: FragmentQrcodeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val qrcodeViewModel =
                ViewModelProvider(this).get(QrcodeViewModel::class.java)

        _binding = FragmentQrcodeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.buttonGerarQrCode.setOnClickListener {
            val texto = binding.editTextUidGerarQrCode.text.toString()
            if (texto.isNotEmpty()) {
                val bitmap = gerarQrCode(texto)
                binding.imageViewQrCode.setImageBitmap(bitmap)
            }
        }

        binding.buttonCompartilharQrCode.setOnClickListener {
            val texto = binding.editTextUidGerarQrCode.text.toString()
            if (texto.isNotEmpty()) {
                val bitmap = gerarQrCode(texto)
                val file = saveBitmapToFile(requireContext(), bitmap, "qrcode.png")
                file?.let {
                    shareImageFile(requireContext(), it)
                }
            }
        }


        return root
    }

    private fun gerarQrCode(texto: String): Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(texto, BarcodeFormat.QR_CODE, 1024, 1024)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }

        return bitmap
    }

    fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String): File? {
        // Diretório para salvar a imagem (pode ser alterado conforme necessário)
        val directory = context.getExternalFilesDir(null)
        val imageFile = File(directory, fileName)

        try {
            val fos = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.close()
            return imageFile
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun shareImageFile(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "com.rr.ars.provider", file)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/png"
        }

        context.startActivity(Intent.createChooser(shareIntent, "Compartilhar via"))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}