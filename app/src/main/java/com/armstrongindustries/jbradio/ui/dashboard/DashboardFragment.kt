package com.armstrongindustries.jbradio.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.armstrongindustries.jbradio.databinding.FragmentDashboardBinding

/**
 * @author Jeremiah Boothe
 * @date 06/24/2024
 * @version 1.0
 * @see Fragment
 * @see DashboardViewModel
 * @see FragmentDashboardBinding
 * @see TextView
 * @see LayoutInflater
 * @see ViewGroup
 * @see Bundle
 * @see View
 * @see onCreateView
 * @see onDestroyView
 * @see ViewModelProvider
 * @see ViewModelProvider.Factory
 * @see DashboardViewModel
 * @see DashboardViewModel.artist
 * @see DashboardViewModel.artist.observe
 * @param
 * @property _binding
 * @property binding
 * @property dashboardViewModel
 */
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding should not be null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this)[DashboardViewModel::class.java]

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textView: TextView = binding.textDashboard

        dashboardViewModel.artist.observe(viewLifecycleOwner) { text ->
            textView.text = text
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
