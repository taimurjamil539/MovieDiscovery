package com.movieapp.moviediscoveryapp.ui.movies


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.movieapp.moviediscoveryapp.databinding.BottomSheetExitBinding

class ExitBottomSheetDialog : BottomSheetDialogFragment() {
    private var _binding: BottomSheetExitBinding? = null
    private val binding get() = _binding!!
    companion object {
        const val TAG = "ExitBottomSheet"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetExitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnExit.setOnClickListener {
            requireActivity().finishAffinity()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}