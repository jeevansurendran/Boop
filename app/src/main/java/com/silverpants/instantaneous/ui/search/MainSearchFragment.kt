package com.silverpants.instantaneous.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.databinding.FragmentMainSearchBinding
import com.silverpants.instantaneous.misc.Result
import com.xwray.groupie.GroupieAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainSearchFragment : Fragment(R.layout.fragment_main_search) {

    private val viewModel: SearchViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentMainSearchBinding.bind(view)
        val adapter = GroupieAdapter()

        binding.imSearchClose.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.rvSearchChat.adapter = adapter

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setQueryString(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        viewModel.result.observe(viewLifecycleOwner) { it ->
            it?.let { result ->
                when (result) {
                    is Result.Success -> {
                        adapter.update(
                            result.data.map {
                                SearchItem(it)
                            }
                        )
                    }
                    else -> {

                    }
                }
            }
        }


    }

}