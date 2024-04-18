package edu.utap.exerciseapp.program
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import edu.utap.exerciseapp.databinding.CalendarProgramBinding
import androidx.navigation.fragment.findNavController
import androidx.navigation.findNavController

class CalendarFragment: Fragment() {
    private var _binding: CalendarProgramBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = CalendarProgramBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = CalendarProgramBinding.bind(view)

        binding.calendarView.setOnDateChangeListener(CalendarView.OnDateChangeListener { view, year, month, dayOfMonth ->
            var mm = "$month"
            if (month < 10) {
                mm = "0$month"
            }
            var dd = "$dayOfMonth"
            if (dayOfMonth < 10) {
                dd = "0$dayOfMonth"
            }
            val direction = CalendarFragmentDirections.actionCalFragmentToProgram("${dd}/${mm}/$year")
            findNavController().navigate(direction)


        })
    }
}