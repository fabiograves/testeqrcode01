package com.rr.ars.ui.pesquisa

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.zxing.integration.android.IntentIntegrator
import com.rr.ars.R
import com.rr.ars.databinding.FragmentPesquisaBinding
import com.rr.ars.ui.CustomGridView
import com.rr.ars.ui.bancodedados.DatabaseHelper
import com.rr.ars.ui.home.HomeFragment


class PesquisaFragment : Fragment() {

    private var _binding: FragmentPesquisaBinding? = null
    private val binding get() = _binding!!

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentPesquisaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        databaseHelper = DatabaseHelper(requireContext())

        val customGridView = binding.customGridView // Se você está usando View Binding
        // Ou se você não estiver usando View Binding:
        //val customGridView = view.findViewById<CustomGridView>(R.id.customGridView)

        val buttonScan: Button = binding.leituraQrcode
        buttonScan.setOnClickListener {
            checkCameraPermissionAndScan()
        }

        val buttonPesquisar: Button = binding.buttonPesquisar
        buttonPesquisar.setOnClickListener {
            val uid = binding.editTextUidPesquisa.text.toString()
            verificarEAtualizarInformacoes(uid)
        }
    }

    private fun verificarEAtualizarInformacoes(uid: String) {
        if (databaseHelper.isUidExist(uid)) {
            val cursor = databaseHelper.getDadosPorUid(uid)
            if (cursor != null && cursor.moveToFirst()) {
                // Correção: usar getEnderecoEstoqueColumnName para obter o nome da coluna
                val enderecoEstoque = cursor.safeGetString(DatabaseHelper.getEnderecoEstoqueColumnName())
                if (enderecoEstoque.length == 6) { // Verifica se o endereço é válido
                    val posX = enderecoEstoque.substring(0, 2).toIntOrNull() ?: return
                    val posY = enderecoEstoque.substring(2, 4).toIntOrNull() ?: return
                    // Atualiza as coordenadas do produto na CustomGridView
                    binding.customGridView.setProductPosition(posX, posY)
                    val armazem = enderecoEstoque[4].toString()
                    val prateleira = enderecoEstoque[5].toString()

                    binding.textViewPosicaoX.text = "Posição X: $posX"
                    binding.textViewPosicaoY.text = "Posição Y: $posY"
                    binding.textViewArmazem.text = "Armazem: $armazem"
                    binding.textViewPrateleira.text = "Prateleira: $prateleira"
                } else {
                    Toast.makeText(context, "Endereço cadastrado errado", Toast.LENGTH_SHORT).show()
                }
                cursor.close()
            } else {
                Toast.makeText(context, "UID não cadastrado", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "UID não cadastrado", Toast.LENGTH_SHORT).show()
        }
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
            requestPermissions(arrayOf(Manifest.permission.CAMERA),
                HomeFragment.PERMISSION_REQUEST_CAMERA
            )
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
                binding.editTextUidPesquisa.setText(uidScaneado)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun Cursor.safeGetString(columnName: String): String {
        val columnIndex = getColumnIndex(columnName)
        return if (columnIndex != -1 && !isNull(columnIndex)) getString(columnIndex) else ""
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            HomeFragment.PERMISSION_REQUEST_CAMERA -> {
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