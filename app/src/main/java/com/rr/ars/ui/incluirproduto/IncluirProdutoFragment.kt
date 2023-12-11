package com.rr.ars.ui.incluirproduto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.rr.ars.databinding.FragmentIncluirprodutoBinding
import com.rr.ars.ui.bancodedados.DatabaseHelper

class IncluirProdutoFragment : Fragment() {

    private var _binding: FragmentIncluirprodutoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val incluirProdutoViewModel =
                ViewModelProvider(this).get(IncluirProdutoViewModel::class.java)

        _binding = FragmentIncluirprodutoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.buttonSalvarRegistro.setOnClickListener {
            inserirRegistro()
        }


        return root
    }

    private fun inserirRegistro() {
        val uid = binding.editTextUID.text.toString()
        val codigoProduto = binding.editTextCodigoProduto.text.toString()
        val loteProduto = binding.editTextLoteProduto.text.toString()
        val subLotProduto = binding.editTextSubLoteProduto.text.toString()
        val almoxarifado = binding.editTextAlmoxarifado.text.toString()
        val loteFornecedor = binding.editTextLoteFornecedor.text.toString()
        val serieNota = binding.editTextSerieNota.text.toString()
        val notaFiscal = binding.editTextNotaFiscal.text.toString()
        val enderecoEstoque = binding.editTextEnderecoEstoque.text.toString()

        if (uid.isNotEmpty() && codigoProduto.isNotEmpty() && loteProduto.isNotEmpty() && subLotProduto.isNotEmpty() &&
            almoxarifado.isNotEmpty() && loteFornecedor.isNotEmpty() && serieNota.isNotEmpty() &&
            notaFiscal.isNotEmpty() && enderecoEstoque.isNotEmpty()) {
            val databaseHelper = DatabaseHelper(requireContext())
            if (databaseHelper.isUidExist(uid)) {
                Toast.makeText(requireContext(), "UID já existente", Toast.LENGTH_SHORT).show()
                return
            }
            val sucesso = databaseHelper.insertData(uid, codigoProduto, loteProduto, subLotProduto, almoxarifado, loteFornecedor,
                serieNota, notaFiscal, enderecoEstoque)

            if (sucesso) {
                Toast.makeText(requireContext(), "Registro inserido com sucesso", Toast.LENGTH_SHORT).show()
                limparCampos()
            } else {
                Toast.makeText(requireContext(), "Erro ao inserir registro", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun limparCampos() {
        binding.editTextUID.text.clear()
        binding.editTextCodigoProduto.text.clear()
        binding.editTextLoteProduto.text.clear()
        binding.editTextSubLoteProduto.text.clear()
        binding.editTextAlmoxarifado.text.clear()
        binding.editTextLoteFornecedor.text.clear()
        binding.editTextSerieNota.text.clear()
        binding.editTextNotaFiscal.text.clear()
        binding.editTextEnderecoEstoque.text.clear()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}