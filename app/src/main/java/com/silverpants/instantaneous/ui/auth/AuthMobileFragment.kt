package com.silverpants.instantaneous.ui.auth

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.databinding.FragmentAuthMobileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthMobileFragment : Fragment(R.layout.fragment_auth_mobile) {

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentAuthMobileBinding.bind(view)

        val ccpCountryCode = binding.ccpAuthMobileCountryCode
        val tilAuthMobileNumber = binding.tilAuthMobileNumber
        val btnAuthMobileNext = binding.btnAuthMobileNext

        tilAuthMobileNumber.editText?.doOnTextChanged { _, _, _, _ ->
            tilAuthMobileNumber.error = null
        }

        tilAuthMobileNumber.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                if (!ccpCountryCode.isValidFullNumber) {
                    tilAuthMobileNumber.error = "The mobile number entered is invalid."
                    return@setOnEditorActionListener true
                }
                verifyNumber(ccpCountryCode.fullNumberWithPlus)
            }
            true
        }

        ccpCountryCode.registerCarrierNumberEditText(tilAuthMobileNumber.editText)

        btnAuthMobileNext.setOnClickListener {
            if (!ccpCountryCode.isValidFullNumber) {
                tilAuthMobileNumber.error = "The mobile number entered is invalid."
                return@setOnClickListener
            }
            verifyNumber(ccpCountryCode.fullNumberWithPlus)
        }
    }

    private fun verifyNumber(number: String) {
        val action =
            AuthMobileFragmentDirections.verifyNumber(number)
        authViewModel.setOtpState(AuthViewModel.OtpStates.READY)
        findNavController().navigate(action)
    }
}