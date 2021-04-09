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
import com.silverpants.instantaneous.misc.hideKeyboard
import com.silverpants.instantaneous.misc.toast
import com.xwray.groupie.GroupieAdapter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean


@AndroidEntryPoint
class MainSearchFragment : Fragment(R.layout.fragment_main_search), SearchClickListener {

    private val viewModel: SearchViewModel by viewModels()
    private var isSearchItemSelected: AtomicBoolean = AtomicBoolean(false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentMainSearchBinding.bind(view)
        val adapter = GroupieAdapter()
        val fallbackItem = SearchFallbackItem()

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
                        if (binding.etSearch.text.length in 0..2) {
                            adapter.update(listOf(fallbackItem))
                        } else {
                            adapter.update(result.data.map { SearchItem(it, this) })
                        }
                    }
                    else -> {
                    }
                }
            }
        }


    }

    override fun onClick(view: View, user2: String) {
        if (isSearchItemSelected.compareAndSet(false, true)) {
            val chatIdLiveData = viewModel.getChatId(user2)
            chatIdLiveData.observe(viewLifecycleOwner) {
                it.let {
                    when (it) {
                        is Result.Success -> {
                            if (it.data.isNullOrEmpty()) {
                                return@observe
                            }
                            requireActivity().hideKeyboard()
                            val action = MainSearchFragmentDirections.openChatFromSearch(it.data)
                            findNavController().navigate(action)
                        }
                        is Result.Error -> {
                            toast("Wait something went wrong, Try again")
                            Timber.e(it.exception)
                        }
                        else -> {
                        }
                    }
                }
                isSearchItemSelected.set(false)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().hideKeyboard()
    }
}