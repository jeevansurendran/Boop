package com.silverpants.instantaneous.ui.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.databinding.FragmentAuthMobileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthMobileFragment : Fragment(R.layout.fragment_auth_mobile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentAuthMobileBinding.bind(view)

        val ccpCountryCode = binding.ccpAuthMobileCountryCode
        val tilAuthMobileNumber = binding.tilAuthMobileNumber
        val btnAuthMobileNext = binding.btnAuthMobileNext

        ccpCountryCode.registerCarrierNumberEditText(tilAuthMobileNumber.editText)

        btnAuthMobileNext.setOnClickListener {
            val action = AuthMobileFragmentDirections.verifyNumber(ccpCountryCode.fullNumberWithPlus)
            findNavController().navigate(action)
        }
    }
}