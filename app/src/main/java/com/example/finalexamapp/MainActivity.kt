package com.example.finalexamapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView // RecyclerView déclaré une seule fois
    private lateinit var userDao: UserDao // DAO pour interagir avec Room
    private lateinit var sharedPreferences: SharedPreferences // Pour les préférences utilisateur

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialisation des boutons
        val toastButton: Button = findViewById(R.id.toastButton)
        val snackbarButton: Button = findViewById(R.id.snackbarButton)
        val notificationButton: Button = findViewById(R.id.notificationButton)

        // Initialisation de RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialisation de Room et des DAO
        val db = Room.databaseBuilder(
            applicationContext,
            UserDatabase::class.java, "database-name"
        ).fallbackToDestructiveMigration().build()

        userDao = db.userDao()

        // Préférences utilisateur
        sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

        // Afficher un Toast
        toastButton.setOnClickListener {
            Toast.makeText(this, "Bienvenue sur FinalExamApp!", Toast.LENGTH_LONG).show()
        }

        // Afficher un Snackbar
        snackbarButton.setOnClickListener {
            val rootView = findViewById<View>(android.R.id.content)
            Snackbar.make(rootView, "Voici un exemple de Snackbar", Snackbar.LENGTH_SHORT).show()
        }

        // Afficher une Notification
        notificationButton.setOnClickListener {
            showNotification()
        }

        // Planification WorkManager
        scheduleWorker()

        // Charger les données de Room et observer les changements
        observeUserData()

        // Ajouter un exemple d'utilisateur dans la base de données
        addUserToDatabase()

        // Lire et afficher les préférences utilisateur
        manageSharedPreferences()
    }

    private fun showNotification() {
        val channelId = "exam_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Créer un canal pour les notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Exam Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Construire et afficher la notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Notification FinalExamApp")
            .setContentText("Bonjour, voici un exemple de notification.")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
            return
        }

        NotificationManagerCompat.from(this).notify(1, notification)
    }

    private fun scheduleWorker() {
        val workRequest = OneTimeWorkRequestBuilder<MyWorker>().build()
        WorkManager.getInstance(applicationContext).enqueue(workRequest)
    }

    private fun observeUserData() {
        userDao.getAll().observe(this, Observer { users ->
            recyclerView.adapter = UserAdapter(users)
        })
    }

    private fun addUserToDatabase() {
        val user = User(1, "Alice", "alice@example.com") // Exemple d'utilisateur
        Thread {
            try {
                userDao.insert(user) // Les appels Room doivent être faits en arrière-plan
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun manageSharedPreferences() {
        // Stocker les préférences
        val editor = sharedPreferences.edit()
        editor.putString("username", "Alice")
        editor.putBoolean("isLoggedIn", true)
        editor.apply()

        // Lire les préférences
        val username = sharedPreferences.getString("username", "DefaultName")
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        Toast.makeText(this, "Username: $username, Logged In: $isLoggedIn", Toast.LENGTH_SHORT).show()
    }
}
