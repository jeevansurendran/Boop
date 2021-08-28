package com.silverpants.instantaneous.ui.easter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.databinding.FragmentMainEasterBinding
import com.silverpants.instantaneous.misc.Intents


class MainEasterEggFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main_easter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentMainEasterBinding.bind(view)
        binding.btnEasterInvite.setOnClickListener {
            val intent =
                Intents.openPlayStoreIntent()
            startActivity(Intent.createChooser(intent, "Open Playstore"))
        }
    }
}