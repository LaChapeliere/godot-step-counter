// TODO: Update to match your plugin's package name.
package org.godotengine.plugin.android.fitsteps

import android.util.Log
import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import com.google.android.gms.fitness.FitnessLocal
import com.google.android.gms.fitness.data.LocalDataType
import com.google.android.gms.fitness.request.LocalDataReadRequest
import com.google.android.gms.fitness.result.LocalDataReadResponse
import com.google.android.gms.fitness.LocalRecordingClient
import com.google.android.gms.fitness.data.LocalDataSet
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.UsedByGodot
import java.util.concurrent.TimeUnit
import java.time.LocalDateTime
import java.time.ZoneId


class GodotAndroidPlugin(godot: Godot): GodotPlugin(godot) {

    override fun getPluginName() = BuildConfig.GODOT_PLUGIN_NAME

    val localRecordingClient: LocalRecordingClient = FitnessLocal.getLocalRecordingClient(activity!!)
    var readDataSet: LocalDataReadResponse? = null


    /**
     * Example showing how to declare a method that's used by Godot.
     *
     * Shows a 'Hello World' toast.
     */
    @UsedByGodot
    fun helloWorld() {
        runOnUiThread {
            Toast.makeText(activity, "Hello World", Toast.LENGTH_LONG).show()
            Log.v(pluginName, "Hello World")
        }
    }

    @UsedByGodot
    fun connectToRecordingAPI() {

      // Subscribe to steps data
      localRecordingClient.subscribe(LocalDataType.TYPE_STEP_COUNT_DELTA)
      .addOnSuccessListener {
          Toast.makeText(activity, "Successfully subscribed!", Toast.LENGTH_LONG).show()
          Log.i(pluginName, "Successfully subscribed!")
      }
      .addOnFailureListener { e: Exception ->
          Toast.makeText(activity, "There was a problem subscribing.", Toast.LENGTH_LONG).show()
          Log.w(pluginName, "There was a problem subscribing.", e)
      }
    }

    fun onReadDataSuccess(response: LocalDataReadResponse) {
        Toast.makeText(activity, "Successfully read data!", Toast.LENGTH_LONG).show()
        readDataSet = response
    }

    fun onReadDataFailure(exception: Exception) {
        Log.w(pluginName,"There was an error reading data", exception)
    }


    @UsedByGodot
    fun readData() {
        val endTime = LocalDateTime.now().atZone(ZoneId.systemDefault())
        val startTime = endTime.minusWeeks(1)
        val readRequest =
            LocalDataReadRequest.Builder()
                // The data request can specify multiple data types to return,
                // effectively combining multiple data queries into one call.
                // This example demonstrates aggregating only one data type.
                .read(LocalDataType.TYPE_STEP_COUNT_DELTA)
                // Analogous to a "Group By" in SQL, defines how data should be
                // aggregated. bucketByTime allows bucketing by time span.
                .setTimeRange(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.SECONDS)
                .build()

        localRecordingClient.readData(readRequest).addOnSuccessListener { response-> onReadDataSuccess(response) }
            .addOnFailureListener { e -> onReadDataFailure(e) }
    }

    @UsedByGodot
    fun dumpDataSet(): java.lang.String {

        // The aggregate query puts datasets into buckets, so flatten into a
        // single list of datasets.
        //Toast.makeText(activity, readDataSet!!.getDataSet(LocalDataType.TYPE_STEP_COUNT_DELTA).toString(), Toast.LENGTH_LONG).show()
        if (readDataSet == null) {
            return java.lang.String("No data")
        }
        var fullDataSet = ""
        var dataSet = readDataSet!!.getDataSet(LocalDataType.TYPE_STEP_COUNT_DELTA).getDataPoints()
        for (datapoint in dataSet) {
            var stringDataPoint =  ""
            stringDataPoint += "\nData point:"
            stringDataPoint += "\tType: ${datapoint.dataType.name}"
            stringDataPoint += "\tStart: ${datapoint.getStartTime(TimeUnit.HOURS)}"
            stringDataPoint += "\tEnd: ${datapoint.getEndTime(TimeUnit.HOURS)}"
            for (field in datapoint.dataType.fields) {
                stringDataPoint += "\tLocalField: ${field.name.toString()} LocalValue: ${datapoint.getValue(field)}"
            }
            fullDataSet += stringDataPoint
            fullDataSet += "\n\n"
        }
        return java.lang.String(fullDataSet)
    }

}
