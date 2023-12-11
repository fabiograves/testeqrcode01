package com.rr.ars.ui.qrcode

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.rr.ars.databinding.FragmentQrcodeBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter


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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}