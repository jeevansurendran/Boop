package com.silverpants.instantaneous.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
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

        ccpCountryCode.registerCarrierNumberEditText(tilAuthMobileNumber.editText)

        btnAuthMobileNext.setOnClickListener {
            if (!ccpCountryCode.isValidFullNumber) {
                Toast.makeText(requireContext(), "Not a valid mobile number", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            val action =
                AuthMobileFragmentDirections.verifyNumber(ccpCountryCode.fullNumberWithPlus)
            authViewModel.setOtpState(AuthViewModel.OtpStates.READY)
            findNavController().navigate(action)
        }
    }
}