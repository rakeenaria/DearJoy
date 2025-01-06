//import com.github.mikephil.charting.formatter.ValueFormatter
//
//class DateValueFormatter : ValueFormatter() {
//    override fun getFormattedValue(value: Float): String {
//        val day = value.toInt()
//        return "$day"
//    }
//}

open class DateValueFormatter : com.github.mikephil.charting.formatter.ValueFormatter() {
    private val dateFormatter = java.text.SimpleDateFormat("dd-MM-yy", java.util.Locale.getDefault())

    override fun getFormattedValue(value: Float): String {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.DAY_OF_YEAR, value.toInt()) // Set day of year
        return dateFormatter.format(calendar.time) // Format as DD-MM-YY
    }
}

