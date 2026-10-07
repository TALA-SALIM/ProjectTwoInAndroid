package com.example.talasalim.project2

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.android.synthetic.main.fragment_blank.view.*
import kotlinx.android.synthetic.main.dialog.*
import kotlinx.android.synthetic.main.item.view.*

class BlankFragment : Fragment() {

    private fun showTaskNotification(taskName: String) {

        val currentActivity = activity ?: return
        val channelId = "tasks_channel"

        val notificationManager =
                currentActivity.getSystemService(
                        Context.NOTIFICATION_SERVICE
                ) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                    channelId,
                    "إشعارات المهام",
                    NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(
                currentActivity,
                MainActivity::class.java
        )

        intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingFlags =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_UPDATE_CURRENT or
                            PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }

        val pendingIntent = PendingIntent.getActivity(
                currentActivity,
                0,
                intent,
                pendingFlags
        )

        val notification = NotificationCompat.Builder(
                currentActivity,
                channelId
        )
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("بدأت المهمة")
                .setContentText(taskName)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

        notificationManager.notify(1234, notification)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        Toast.makeText(
                activity,
                "onAttach",
                Toast.LENGTH_SHORT
        ).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Toast.makeText(
                activity,
                "onCreate",
                Toast.LENGTH_SHORT
        ).show()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        Toast.makeText(
                activity,
                "onCreateView",
                Toast.LENGTH_SHORT
        ).show()

        val view = inflater.inflate(
                R.layout.fragment_blank,
                container,
                false
        )

        view.addTask.setOnClickListener {

            val dialog = Dialog(activity!!)
            dialog.setContentView(R.layout.dialog)

            val today = Calendar.getInstance()

            var selectedDate = ""
            var selectedDay = ""
            var startTime = ""
            var endTime = ""

            var startMinutes = -1
            var endMinutes = -1

            dialog.selectDate.setOnClickListener {

                val datePicker = DatePickerDialog(
                        activity!!,
                        DatePickerDialog.OnDateSetListener {
                            _, year, month, day ->

                            selectedDate = String.format(
                                    Locale.US,
                                    "%02d/%02d/%04d",
                                    day,
                                    month + 1,
                                    year
                            )

                            val chosenDate = Calendar.getInstance()
                            chosenDate.set(year, month, day)

                            selectedDay = SimpleDateFormat(
                                    "EEEE",
                                    Locale("ar")
                            ).format(chosenDate.time)

                            dialog.selectDate.text =
                                    "$selectedDay - $selectedDate"
                        },
                        today.get(Calendar.YEAR),
                        today.get(Calendar.MONTH),
                        today.get(Calendar.DAY_OF_MONTH)
                )

                datePicker.show()
            }

            dialog.selectStartTime.setOnClickListener {

                val timePicker = TimePickerDialog(
                        activity!!,
                        TimePickerDialog.OnTimeSetListener {
                            _, hour, minute ->

                            startMinutes = hour * 60 + minute

                            startTime = String.format(
                                    Locale.US,
                                    "%02d:%02d",
                                    hour,
                                    minute
                            )

                            dialog.selectStartTime.text =
                                    "◷ بداية: $startTime"
                        },
                        today.get(Calendar.HOUR_OF_DAY),
                        today.get(Calendar.MINUTE),
                        true
                )

                timePicker.show()
            }

            dialog.selectEndTime.setOnClickListener {

                val timePicker = TimePickerDialog(
                        activity!!,
                        TimePickerDialog.OnTimeSetListener {
                            _, hour, minute ->

                            endMinutes = hour * 60 + minute

                            endTime = String.format(
                                    Locale.US,
                                    "%02d:%02d",
                                    hour,
                                    minute
                            )

                            dialog.selectEndTime.text =
                                    "◷ نهاية: $endTime"
                        },
                        today.get(Calendar.HOUR_OF_DAY),
                        today.get(Calendar.MINUTE),
                        true
                )

                timePicker.show()
            }

            dialog.cancelTask.setOnClickListener {
                dialog.dismiss()
            }

            dialog.saveTask.setOnClickListener {

                val name =
                        dialog.taskName.text.toString().trim()

                val category =
                        dialog.taskCategory.text.toString().trim()

                val description =
                        dialog.taskDescription.text.toString().trim()

                if (name.isEmpty() ||
                        category.isEmpty() ||
                        description.isEmpty()) {

                    Toast.makeText(
                            activity,
                            "املئي الاسم والتصنيف والوصف",
                            Toast.LENGTH_SHORT
                    ).show()

                } else if (selectedDate.isEmpty() ||
                        startMinutes == -1 ||
                        endMinutes == -1) {

                    Toast.makeText(
                            activity,
                            "اختاري التاريخ ووقت البداية والنهاية",
                            Toast.LENGTH_SHORT
                    ).show()

                } else if (endMinutes <= startMinutes) {

                    Toast.makeText(
                            activity,
                            "وقت النهاية يجب أن يكون بعد البداية في نفس اليوم",
                            Toast.LENGTH_LONG
                    ).show()

                } else {

                    val taskView = inflater.inflate(
                            R.layout.item,
                            view.tasksContainer,
                            false
                    )

                    taskView.nameText.text = name
                    taskView.categoryText.text = category
                    taskView.dayText.text = selectedDay
                    taskView.descriptionText.text = description

                    taskView.timeText.text =
                            "◷  $startTime - $endTime"

                    taskView.dateText.text =
                            "التاريخ: $selectedDate"

                    taskView.startButton.setOnClickListener {

                        val alert = AlertDialog.Builder(activity!!)

                        alert.setTitle("تنبيه")

                        alert.setMessage(
                                "هل تريدين بدء المهمة: $name؟"
                        )

                        alert.setPositiveButton("موافق") { _, _ ->

                            taskView.startButton.text = "قيد التنفيذ"
                            taskView.startButton.isEnabled = false

                            showTaskNotification(name)

                            Toast.makeText(
                                    activity,
                                    "بدأت المهمة: $name",
                                    Toast.LENGTH_SHORT
                            ).show()
                        }

                        alert.setNegativeButton("إلغاء") {
                            dialogInterface, _ ->

                            dialogInterface.dismiss()
                        }

                        alert.show()
                    }

                    view.tasksContainer.addView(taskView)

                    Toast.makeText(
                            activity,
                            "تمت إضافة المهمة",
                            Toast.LENGTH_SHORT
                    ).show()

                    dialog.dismiss()
                }
            }

            dialog.show()

            dialog.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Toast.makeText(
                activity,
                "onActivityCreated",
                Toast.LENGTH_SHORT
        ).show()
    }

    override fun onStart() {
        super.onStart()

        Toast.makeText(
                activity,
                "onStart",
                Toast.LENGTH_SHORT
        ).show()
    }

    override fun onResume() {
        super.onResume()

        Toast.makeText(
                activity,
                "onResume",
                Toast.LENGTH_SHORT
        ).show()
    }

    override fun onPause() {
        Toast.makeText(
                activity,
                "onPause",
                Toast.LENGTH_SHORT
        ).show()

        super.onPause()
    }

    override fun onStop() {
        Toast.makeText(
                activity,
                "onStop",
                Toast.LENGTH_SHORT
        ).show()

        super.onStop()
    }

    override fun onDestroyView() {
        Toast.makeText(
                activity,
                "onDestroyView",
                Toast.LENGTH_SHORT
        ).show()

        super.onDestroyView()
    }

    override fun onDestroy() {
        Toast.makeText(
                activity,
                "onDestroy",
                Toast.LENGTH_SHORT
        ).show()

        super.onDestroy()
    }

    override fun onDetach() {
        Toast.makeText(
                activity,
                "onDetach",
                Toast.LENGTH_SHORT
        ).show()

        super.onDetach()
    }
}