package com.rr.ars.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.integration.android.IntentIntegrator
import com.rr.ars.databinding.FragmentHomeBinding
import android.widget.Button
import androidx.core.content.ContextCompat
import com.rr.ars.ui.bancodedados.DatabaseHelper


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val PERMISSION_REQUEST_CAMERA = 1
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Listener do botao leitura_qrcode
        val buttonScan: Button = binding.leituraQrcode
        buttonScan.setOnClickListener {
            checkCameraPermissionAndScan()
        }

        return root
    }

    private fun scanQRCode() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Scanear QR code")
        integrator.setCameraId(0)  // Usar config padrão câmera
        integrator.setBeepEnabled(false)
        integrator.setBarcodeImageEnabled(true)
        integrator.initiateScan()
    }

    private fun checkCameraPermissionAndScan() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            // Permissão não concedida, solicite ao usuário.
            requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
        } else {
            // Permissão já concedida, inicie o scanner.
            scanQRCode()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(context, "Cancelado", Toast.LENGTH_LONG).show()
            } else {
                val uidScaneado = result.contents
                binding.textViewUID.text = "UID: $uidScaneado"
                buscarEDefinirDados(uidScaneado)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun buscarEDefinirDados(uid: String) {
        val databaseHelper = DatabaseHelper(requireContext())
        val cursor = databaseHelper.getDadosPorUid(uid)

        if (cursor != null && cursor.moveToFirst()) {
            binding.textViewCodigoProduto.text = "Código Produto: ${cursor.safeGetString(DatabaseHelper.getCodigoProdutoColumnName())}"
            binding.textViewLoteProduto.text = "Lote Produto: ${cursor.safeGetString(DatabaseHelper.getLoteProdutoColumnName())}"
            binding.textViewSubLoteProduto.text = "Sub Lote Produto: ${cursor.safeGetString(DatabaseHelper.getSubLoteProdutoColumnName())}"
            binding.textViewAlmoxarifado.text = "Almoxarifado: ${cursor.safeGetString(DatabaseHelper.getAlmoxarifadoColumnName())}"
            binding.textViewLoteFornecedor.text = "Lote Fornecedor: ${cursor.safeGetString(DatabaseHelper.getLoteFornecedorColumnName())}"
            binding.textViewSerieNota.text = "Serie Nota: ${cursor.safeGetString(DatabaseHelper.getSerieNotaColumnName())}"
            binding.textViewNotaFiscal.text = "Nota Fiscal: ${cursor.safeGetString(DatabaseHelper.getNotaFiscalColumnName())}"
            binding.textViewEnderecoEstoque.text = "Endereço Estoque: ${cursor.safeGetString(DatabaseHelper.getEnderecoEstoqueColumnName())}"

        } else {
            Toast.makeText(context, "UID não encontrado", Toast.LENGTH_SHORT).show()
        }

        cursor?.close()
    }

    fun Cursor.safeGetString(columnName: String): String {
        val columnIndex = getColumnIndex(columnName)
        return if (columnIndex != -1 && !isNull(columnIndex)) getString(columnIndex) else ""
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CAMERA -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permissão concedida, inicie o scanner.
                    scanQRCode()
                } else {
                    // Permissão negada, informe o usuário sobre a necessidade da permissão.
                    Toast.makeText(context, "Permissão da câmera necessária para leitura do QR code", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}