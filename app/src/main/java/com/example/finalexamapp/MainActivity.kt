package com.example.finalexamapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toastButton: Button = findViewById(R.id.toastButton)
        val snackbarButton: Button = findViewById(R.id.snackbarButton)
        val notificationButton: Button = findViewById(R.id.notificationButton)

        // Liste d'exemple
        val userList = listOf(
            User(1, "Alice", "alice@example.com"),
            User(2, "Bob", "bob@example.com"),
            User(3, "Charlie", "charlie@example.com")
        )

        // RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = UserAdapter(userList)

        // Toast
        toastButton.setOnClickListener {
            Toast.makeText(this, "Bienvenue sur FinalExamApp!", Toast.LENGTH_LONG).show()
        }

        // Snackbar
        snackbarButton.setOnClickListener {
            val rootView = findViewById<View>(android.R.id.content)
            Snackbar.make(rootView, "Voici un exemple de Snackbar", Snackbar.LENGTH_SHORT).show()
        }

        // Notification
        notificationButton.setOnClickListener {
            showNotification()
        }

        // WorkManager
        scheduleWorker()
    }

    private fun scheduleWorker() {
        val workRequest = OneTimeWorkRequestBuilder<MyWorker>().build()
        WorkManager.getInstance(applicationContext).enqueue(workRequest)
    }

    private fun showNotification() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "exam_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Exam Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Notification FinalExamApp")
            .setContentText("Bonjour, voici un exemple de notification.")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
            return
        }
        NotificationManagerCompat.from(this).notify(1, notification)

        val db = Room.databaseBuilder(
            applicationContext,
            UserDatabase::class.java, "database-name"
        ).build()

        val userDao = db.userDao()

       // Exemple d'ajout d'un utilisateur
        val user = User(1, "Alice", "alice@example.com") // ID est un Int et email est ajouté
        Thread {
            try {
                userDao.insert(user) // Les appels à la base de données doivent être faits sur un thread d'arrière-plan
            } catch (e: Exception) {
                e.printStackTrace() // Gestion des erreurs si nécessaire
            }
        }.start()


        Room.databaseBuilder(
            applicationContext,
            UserDatabase::class.java, "database-name"
        ).fallbackToDestructiveMigration().build()

    }

}
