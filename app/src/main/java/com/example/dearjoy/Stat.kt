package com.example.dearjoy

import DateValueFormatter
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.example.dearjoy.databinding.FragmentStatBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.prolificinteractive.materialcalendarview.CalendarDay

class Stat : Fragment() {

    private lateinit var binding: FragmentStatBinding
    private lateinit var database: DatabaseReference
    private val moodValues = ArrayList<Entry>()
    private var pendingQueries = 0
    private val moodMap = mutableMapOf<String, MutableList<Float>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().reference
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentStatBinding.inflate(inflater, container, false)

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        val months = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, months)
        binding.autoCompleteMonth.setAdapter(adapter)

        binding.autoCompleteMonth.setOnItemClickListener { _, _, position, _ ->
            val selectedMonth = months[position] //belum di setting

        }

        // Total progress
        val moodPercentages = listOf(35, 25, 15, 15, 10)
        binding.moodProgressBar.progress = moodPercentages.sum()

        binding.calendarView.setOnDateChangedListener { widget, date, selected ->
            // Format tanggal menjadi "yyyy-MM-dd" untuk mencocokkan createdDate di database
            val selectedDate = String.format("%04d-%02d-%02d", date.year, date.month, date.day)
            navigateToHistoryJournal(selectedDate)
        }

        fetchMoodData(currentUserId)

        return binding.root
    }

    private fun fetchMoodData(userId: String?) {
        if (userId == null) return

        database.child("journal")
            .orderByChild("userID")
            .equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        Log.d("MoodData", "No journals found for user.")
                        return
                    }

                    pendingQueries = snapshot.childrenCount.toInt()

                    for (journalSnapshot in snapshot.children) {
                        val journalId = journalSnapshot.key
                        fetchMoodForJournal(journalId)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MoodData", "Database error: ${error.message}")
                }
            })
    }

    private fun fetchMoodForJournal(journalId: String?) {
        if (journalId == null) return

        database.child("mood")
            .orderByChild("journalID")
            .equalTo(journalId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        return
                    }

                    for (moodSnapshot in snapshot.children) {
                        val moodType = moodSnapshot.child("moodType").getValue(Int::class.java)?.toFloat()
                        if (moodType != null) {
                            fetchDateForMood(journalId, moodType)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MoodData", "Database error: ${error.message}")
                }
            })
    }


    private fun decrementPendingQueries() {
        pendingQueries -= 1
        Log.d("MoodData", "Pending queries remaining: $pendingQueries")

        if (pendingQueries == 0) {
            Log.d("MoodData", "All queries completed. Sorting moodValues and setting chart.")
            moodValues.sortBy { it.x }
        }
    }

    private fun fetchDateForMood(journalId: String, moodType: Float) {
        database.child("journal").child(journalId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dateString = snapshot.child("createdDate").getValue(String::class.java)
                    val dateKey = extractDateFromDateString(dateString) // Ambil tanggal lengkap

                    if (dateKey != null) {

                        val existingMood = moodMap[dateKey] ?: mutableListOf()
                        existingMood.add(moodType)
                        moodMap[dateKey] = existingMood

                        Log.d("MoodData", "MoodMap after adding: $moodMap")
                    }

                    decrementPendingQueries()
                    if (pendingQueries == 0) {
                        processMoodData()
                        SetChart()
                        setCalendarEmoji()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MoodData", "Database error: ${error.message}")
                }
            })
    }

    private fun extractDateFromDateString(dateString: String?): String? {
        return try {
            val formatter = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
            val date = formatter.parse(dateString ?: return null)
            val calendar = java.util.Calendar.getInstance()
            calendar.time = date

            val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
            val month = calendar.get(java.util.Calendar.MONTH) + 1 // Bulan dimulai dari 0
            val year = calendar.get(java.util.Calendar.YEAR) % 100 // Ambil 2 digit terakhir dari tahun

            // Format tanggal menjadi "dd-MM-yy"
            String.format("%02d-%02d-%02d", day, month, year)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    // Buat versi transformasi moodMap yang menggunakan CalendarDay sebagai kunci
    private val transformedMoodMap = mutableMapOf<String, MutableList<Float>>()

    private fun processMoodData() {
        transformedMoodMap.clear()

        moodMap.forEach { (dateKey, moodTypes) ->
            val dateParts = dateKey.split("-")
            val day = dateParts[0].toInt()
            val month = dateParts[1].toInt()
            val year = "20" + dateParts[2] // Mengubah menjadi tahun penuh (contoh: "2025")

            val calendarDay = CalendarDay.from(year.toInt(), month, day)
            transformedMoodMap[dateKey] = moodTypes

            // Hitung rata-rata untuk setChart
            val averageMoodType = if (moodTypes.size > 1) {
                moodTypes.average().toFloat()
            } else {
                moodTypes.first()
            }
//        moodValues.add(Entry(calendarDay.day.toFloat(), averageMoodType))
            val calendar = java.util.Calendar.getInstance()
            calendar.set(calendarDay.year, calendarDay.month - 1, calendarDay.day)  // Set bulan dengan index yang benar (0-based)
            val dateInMillis = calendar.timeInMillis  // Mendapatkan epoch timestamp dalam milidetik
            moodValues.add(Entry(dateInMillis.toFloat(), averageMoodType))
        }

        moodValues.sortBy { it.x } // Urutkan berdasarkan tanggal (x-axis)
    }


    private fun SetChart() {

        if(moodValues.isEmpty()){
            Log.d("MoodData", "No mood values")
            return
        }

        val dataSet: LineDataSet

        binding.chart.xAxis.apply {
            valueFormatter = DateValueFormatter()
            granularity = 1f // Jarak antar tanggal
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false) // Hilangkan garis grid

            binding.chart.xAxis.valueFormatter = object : DateValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val date = java.util.Date(value.toLong())  // Mengonversi epoch timestamp ke objek Date
                    val formatter = java.text.SimpleDateFormat("dd-MM-yy", java.util.Locale.getDefault())
                    return formatter.format(date)  // format tanggal "dd-MM-yy"
                }
            }
        }

        binding.chart.axisLeft.apply {
            axisMinimum = 0.5f
            axisMaximum = 5f
            granularity = 1f
            setDrawGridLines(false)
            setDrawLabels(false) // Nonaktifkan label teks
        }

        binding.chart.apply {
            description.isEnabled = false // Hilangkan deskripsi default
            legend.isEnabled = false // Hilangkan legend
            setTouchEnabled(true)
            setPinchZoom(true)
            isDragEnabled = true
            setScaleEnabled(true)
        }

        // Cek jika chart sudah memiliki data
        if (binding.chart.data != null && binding.chart.data.dataSetCount > 0) {
            dataSet = binding.chart.data.getDataSetByIndex(0) as LineDataSet
            dataSet.values = moodValues
            binding.chart.data.notifyDataChanged()
            binding.chart.notifyDataSetChanged()
        } else {
            // Jika belum ada data, buat dataset baru
            dataSet = LineDataSet(moodValues, "Mood Tracker")
            dataSet.setColors(*ColorTemplate.VORDIPLOM_COLORS)
            dataSet.valueTextColor = R.color.green
            dataSet.circleColors = listOf(R.color.green)
            dataSet.lineWidth = 4f
            dataSet.circleRadius = 6f
            dataSet.setDrawCircles(true)
            dataSet.setDrawValues(false)

            // Buat objek LineData
            val lineData = LineData(dataSet)
            binding.chart.data = lineData
            Log.d("MoodData", "MoodValues: $dataSet")
            // Set data ke chart
            binding.chart.axisRight.isEnabled = false
        }

        binding.chart.invalidate()  // Refresh chart setelah data dimasukkan
    }

    private fun setCalendarEmoji() {
        moodMap.forEach { (dateKey, moodTypes) ->
            //dateKey dalam format "dd-MM-yy"
            val dateParts = dateKey.split("-")
            val day = dateParts[0].toInt()
            val month = dateParts[1].toInt()
            val year = "20" + dateParts[2] // Mengubah menjadi tahun penuh

            // Emoji berdasarkan rata-rata mood
            val averageMood = moodTypes.average().toFloat()
            val emojiRes = when {
                averageMood < 2 -> R.drawable.mood1
                averageMood < 3 -> R.drawable.mood2
                averageMood < 4 -> R.drawable.mood3
                averageMood < 5 -> R.drawable.mood4
                averageMood == 5f -> R.drawable.mood5
                else -> null
            }

            emojiRes?.let {
                // Menambahkan decorator pada kalender dengan tanggal yang sesuai
                val drawable: Drawable = ContextCompat.getDrawable(requireContext(), it) ?: return@forEach
                val calendarDay = CalendarDay.from(year.toInt(), month, day)
                binding.calendarView.addDecorator(EmojiDecorator(setOf(calendarDay), drawable))
            }
        }
    }

    private fun navigateToHistoryJournal(selectedDate: String) {
        val fragment = HistoryJournal()
        val bundle = Bundle()
        bundle.putString("selectedDate", selectedDate)
        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)
            .addToBackStack(null)
            .commit()
    }






}

