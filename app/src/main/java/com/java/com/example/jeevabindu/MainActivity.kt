package com.example.jeevabindu

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jeevabindu.ui.theme.JeevaBinduTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.text.style.TextAlign


import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text

val Red = Color(0xFFE50000)
val DarkRed = Color(0xFFB71C1C)
val LightPink = Color(0xFFFFEBEE)
val Green = Color(0xFF00A651)
val Orange = Color(0xFFFF6B00)
val Blue = Color(0xFF2962FF)
val Pink = Color(0xFFE91E63)
val Black = Color(0xFF171717)

data class AlertItem(
    val id: String = "",
    val bloodGroup: String = "",
    val hospitalName: String = "",
    val location: String = "",
    val message: String = "",
    val status: String = ""
)
data class DonorItem(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val bloodGroup: String = "",
    val age: String = "",
    val location: String = "",
    val available: Boolean = true,
    val lastDonationDate: String = "",
    val nextEligibleDate: String = ""
)

class MainActivity : ComponentActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JeevaBinduTheme {
                var screen by remember { mutableStateOf("home") }

                when (screen) {
                    "home" -> SplashScreen(
                        onGetStarted = { screen = "signup" }
                    )

                    "login" -> AuthScreen(
                        title = "Login",
                        buttonText = "Login",
                        switchText = "Don't have an account? Register",
                        onSwitch = { screen = "signup" },
                        onBack = { screen = "home" },
                        onSubmit = { email, password ->
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                                    screen = "dashboard"
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Wrong password or email", Toast.LENGTH_SHORT).show()
                                }
                        }
                    )

                    "signup" -> AuthScreen(
                        title = "Create Account",
                        buttonText = "Register",
                        switchText = "Already have an account? Login",
                        onSwitch = { screen = "login" },
                        onBack = { screen = "home" },
                        onSubmit = { email, password ->
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                                    screen = "login"
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Signup failed: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    )

                    "dashboard" -> DashboardScreen(
                        onLogout = {
                            FirebaseAuth.getInstance().signOut()
                            screen = "home"
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SplashScreen(
    onGetStarted: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "🩸",
                fontSize = 90.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Jeeva-Bindu",
                fontSize = 45.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Every Drop Matters",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "A rapid response blood donor network for saving lives.",
                fontSize = 15.sp,
                color = Color.Gray,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {

                Text(
                    text = "Get Started",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun AuthScreen(
    title: String,
    buttonText: String,
    switchText: String,
    onSwitch: () -> Unit,
    onBack: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(55.dp))

            Text(
                text = "🩸",
                fontSize = 75.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Jeeva-Bindu",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.height(6.dp))


            Spacer(modifier = Modifier.height(30.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                elevation = CardDefaults.cardElevation(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(26.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = "EMAIL / USERNAME",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    Text(
                        text = "PASSWORD",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                return@Button
                            }
                            onSubmit(email, password)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Text(
                            text = buttonText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    OutlinedButton(
                        onClick = onSwitch,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = switchText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = onBack,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 15.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
@Composable
fun NotificationScreen(onBack: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    var alerts by remember { mutableStateOf<List<AlertItem>>(emptyList()) }

    DisposableEffect(Unit) {
        val listener = db.collection("responses")
            .addSnapshotListener { result, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (result != null) {
                    alerts = result.documents.map { doc ->
                        AlertItem(
                            id = doc.id,
                            bloodGroup = doc.getString("bloodGroup") ?: "",
                            hospitalName = doc.getString("hospitalName") ?: "",
                            location = doc.getString("location") ?: "",
                            message = doc.getString("message") ?: "",
                            status = doc.getString("status") ?: ""
                        )
                    }
                }
            }

        onDispose {
            listener.remove()
        }
    }

    PageContainer(
        title = "Inbox",
        color = Red,
        onBack = onBack,
        onHome = onBack
    ) {
        if (alerts.isEmpty()) {
            Text(
                text = "No notifications yet",
                color = Color.Gray,
                fontSize = 16.sp
            )
        } else {
            alerts.forEach { alert ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = LightPink),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color.White, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("♥", color = Red, fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${alert.message}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Black
                            )

                            Text(
                                text = "Blood needed at ${alert.hospitalName}, ${alert.location}",
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )

                            Text(
                                text = alert.message,
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }

                        Text("•", color = Red, fontSize = 24.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardScreen(onLogout: () -> Unit) {
    var page by remember { mutableStateOf("menu") }

    when (page) {
        "register" -> DonorRegistrationScreen(onBack = { page = "menu" }, onHome = { page = "menu" })
        "alerts" -> EmergencyAlertScreen(onBack = { page = "menu" }, onHome = { page = "menu" })
        "responses" -> ResponsesScreen(onBack = { page = "menu" }, onHome = { page = "menu" })
        "community" -> CommunityScreen(onBack = { page = "menu" })
        "chart" -> BloodChartScreen(onBack = { page = "menu" })
        "settings" -> SettingsScreen(onBack = { page = "menu" }, onLogout = onLogout)
        "inbox" -> NotificationScreen(
            onBack = { page = "menu" }
        )
        else -> MainMenuScreen(
            onRegister = { page = "register" },
            onAlerts = { page = "alerts" },
            onResponses = { page = "responses" },
            onCommunity = { page = "community" },
            onChart = { page = "chart" },
            onSettings = { page = "settings" },
            onNotifications = { page = "inbox" },
            onLogout = onLogout
        )
    }
}

@Composable
fun MainMenuScreen(
    onRegister: () -> Unit,
    onAlerts: () -> Unit,
    onResponses: () -> Unit,
    onCommunity: () -> Unit,
    onChart: () -> Unit,
    onSettings: () -> Unit,
    onNotifications: () -> Unit,
    onLogout: () -> Unit
) {
    val email = FirebaseAuth.getInstance().currentUser?.email ?: "user"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        // LEFT SIDE LOGO + TITLE
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "🩸",
                fontSize = 26.sp
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "JEEVA-BINDU",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
        }

        // RIGHT SIDE ICONS
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = { onSettings() }
            ) {
                Text("⚙️", fontSize = 20.sp)
            }

            IconButton(
                onClick = { onNotifications() }
            ) {
                Text("🔔", fontSize = 20.sp)
            }

            IconButton(
                onClick = { onLogout() }
            ) {
                Text("↪", fontSize = 20.sp)
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {
        TopBar(
            onSettings = onSettings,
            onNotifications = onNotifications,
            onLogout = onLogout
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Welcome Back,", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Black)
        Text(email.substringBefore("@"), fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(28.dp))

        MenuCard("1) My Profile & Registry", "Save your health details and manage your profile.", Red, "👥", onRegister)
        MenuCard("2) Emergency Alerts", "View or post urgent blood requests in your area.", Orange, "!", onAlerts)
        MenuCard("3) Responses", "Track volunteers and record your life-saving donations.", Green, "↯", onResponses)
        MenuCard("4) Donor Community", "View all registered heroes and potential donors.", Blue, "👥", onCommunity)
        MenuCard("5) Blood Compatibility Chart", "Check who can donate and receive blood easily.", Pink, "💧", onChart)
    }
}

@Composable
fun TopBar(
    onSettings: () -> Unit,
    onNotifications: () -> Unit,
    onLogout: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var alertCount by remember { mutableStateOf(0) }

    DisposableEffect(Unit) {
        val listener = db.collection("alerts")
            .addSnapshotListener { result, _ ->
                alertCount = result?.size() ?: 0
            }

        onDispose {
            listener.remove()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "🩸", fontSize = 28.sp)

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "JEEVA-BINDU",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = onSettings) {
            Text("⚙", fontSize = 22.sp)
        }

        Box {
            IconButton(onClick = onNotifications) {
                Text("🔔", fontSize = 21.sp)
            }

            if (alertCount > 0) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Red, CircleShape)
                        .align(Alignment.TopEnd)
                )
            }
        }

        IconButton(onClick = onLogout) {
            Text("↪", fontSize = 22.sp)
        }
    }
}

@Composable
fun MenuCard(title: String, sub: String, color: Color, icon: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(52.dp).background(color, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, color = Color.White, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Black)
                Text(sub, fontSize = 12.sp, color = Color.Gray)
            }

            Text("›", fontSize = 32.sp, color = Color.LightGray)
        }
    }
}

@Composable
fun HomeScreen(
    onLoginClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Blood Drop Logo
            Text(
                text = "🩸",
                fontSize = 90.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Jeeva-Bindu",
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Every Drop Matters",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Connecting heroes to local blood emergencies instantly.",
                fontSize = 15.sp,
                color = Color.Gray,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {

                Text(
                    text = "Get Started",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun DonorRegistrationScreen(
    onBack: () -> Unit,
    onHome: () -> Unit
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var lastDonationDate by remember { mutableStateOf("") }

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    PageContainer(
        title = "My Profile & Registry",
        color = Red,
        onBack = onBack,
        onHome = onHome
    ) {
        InputBox(name, { name = it }, "Name")
        InputBox(phone, { phone = it }, "Phone Number")
        InputBox(bloodGroup, { bloodGroup = it }, "Blood Group Example: O+")
        InputBox(age, { age = it }, "Age")
        InputBox(location, { location = it }, "Location / Town")
        InputBox(lastDonationDate, { lastDonationDate = it }, "Last Donation Date: yyyy-MM-dd")

        val eligibilityText = try {
            if (lastDonationDate.isNotBlank()) {
                val nextDate = LocalDate.parse(lastDonationDate, formatter).plusDays(90)
                "Next Eligible Date: $nextDate"
            } else {
                "Enter last donation date to calculate eligibility"
            }
        } catch (e: Exception) {
            "Invalid date format. Use yyyy-MM-dd"
        }

        Text(
            text = eligibilityText,
            color = Green,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 10.dp)
        )

        Button(
            onClick = {
                if (name.isBlank() || phone.isBlank() || bloodGroup.isBlank() || age.isBlank() || location.isBlank()) {
                    Toast.makeText(context, "Please fill all details", Toast.LENGTH_SHORT).show()
                } else {
                    val nextEligibleDate = try {
                        if (lastDonationDate.isNotBlank()) {
                            LocalDate.parse(lastDonationDate, formatter).plusDays(90).toString()
                        } else {
                            "Not updated"
                        }
                    } catch (e: Exception) {
                        "Invalid date"
                    }

                    val donorData = hashMapOf(
                        "name" to name,
                        "phone" to phone,
                        "bloodGroup" to bloodGroup.uppercase(),
                        "age" to age,
                        "location" to location,
                        "available" to true,
                        "lastDonationDate" to lastDonationDate,
                        "nextEligibleDate" to nextEligibleDate
                    )

                    db.collection("users")
                        .add(donorData)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Donor details saved", Toast.LENGTH_SHORT).show()
                            onHome()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Green),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Ready to Donate", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun EmergencyAlertScreen(onBack: () -> Unit, onHome: () -> Unit) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var alerts by remember { mutableStateOf<List<AlertItem>>(emptyList()) }
    var showForm by remember { mutableStateOf(false) }

    var bloodGroup by remember { mutableStateOf("") }
    var hospitalName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    DisposableEffect(Unit) {
        val listener = db.collection("alerts")
            .addSnapshotListener { result, _ ->
                alerts = result?.documents?.map { doc ->
                    AlertItem(
                        id = doc.id,
                        bloodGroup = doc.getString("bloodGroup") ?: "",
                        hospitalName = doc.getString("hospitalName") ?: "",
                        location = doc.getString("location") ?: "",
                        message = doc.getString("message") ?: "",
                        status = doc.getString("status") ?: "Active"
                    )
                } ?: emptyList()
            }

        onDispose { listener.remove() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
    ) {

        Spacer(modifier = Modifier.height(42.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🩸", fontSize = 28.sp)

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                "JEEVA-BINDU",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Black
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "↯ Alerts",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Red
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { showForm = !showForm },
                colors = ButtonDefaults.buttonColors(containerColor = Red),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text("+ New", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (showForm) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    InputBox(bloodGroup, { bloodGroup = it }, "Blood Group Needed")
                    InputBox(hospitalName, { hospitalName = it }, "Hospital Name")
                    InputBox(location, { location = it }, "Location / Town")
                    InputBox(message, { message = it }, "Emergency Message")

                    Button(
                        onClick = {
                            if (bloodGroup.isBlank() || hospitalName.isBlank() || location.isBlank() || message.isBlank()) {
                                Toast.makeText(context, "Please fill all alert details", Toast.LENGTH_SHORT).show()
                            } else {
                                val alertData = hashMapOf(
                                    "bloodGroup" to bloodGroup.uppercase(),
                                    "hospitalName" to hospitalName,
                                    "location" to location,
                                    "message" to message,
                                    "status" to "Active"
                                )

                                db.collection("alerts").add(alertData)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Emergency Alert Created", Toast.LENGTH_SHORT).show()
                                        bloodGroup = ""
                                        hospitalName = ""
                                        location = ""
                                        message = ""
                                        showForm = false
                                    }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Red),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Post Alert", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (alerts.isEmpty()) {
            Text(
                "No emergency alerts found",
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        alerts.forEach { alert ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(5.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(LightPink, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                alert.bloodGroup,
                                color = Red,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                alert.hospitalName,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = Black
                            )

                            Text(
                                alert.location,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        Text(
                            "YOUR POST",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(Red, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LightPink, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            "Urgent ${alert.bloodGroup} blood needed at ${alert.hospitalName}",
                            color = Red,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            db.collection("alerts")
                                .document(alert.id)
                                .update("status", "Resolved")
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Alert marked as resolved", Toast.LENGTH_SHORT).show()
                                }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Black),
                        shape = RoundedCornerShape(12.dp)
                    ) {


                        Text(
                            text = if (alert.status == "Resolved") "Resolved" else "Mark as Resolved",
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onHome,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(150.dp),
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Black
            )
        ) {
            Text("⌂ Main Menu")
        }

        Spacer(modifier = Modifier.height(25.dp))
    }
}
@Composable
fun ResponsesScreen(onBack: () -> Unit, onHome: () -> Unit) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    var alerts by remember { mutableStateOf<List<AlertItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        db.collection("alerts").get()
            .addOnSuccessListener { result ->
                alerts = result.documents.map { doc ->
                    AlertItem(
                        id = doc.id,
                        bloodGroup = doc.getString("bloodGroup") ?: "",
                        hospitalName = doc.getString("hospitalName") ?: "",
                        location = doc.getString("location") ?: "",
                        message = doc.getString("message") ?: "",
                        status = doc.getString("status") ?: ""
                    )
                }
            }
    }

    PageContainer(title = "Donations", color = Green, onBack = onBack, onHome = onHome) {
        if (alerts.isEmpty()) {
            Text("No emergency alerts found", color = Color.Gray)
        }

        alerts.forEach { alert ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "${alert.bloodGroup} needed",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Red
                    )

                    Text(alert.hospitalName, fontWeight = FontWeight.Bold)
                    Text(alert.location, color = Color.Gray, fontSize = 12.sp)
                    Text("“${alert.message}”", color = Red, fontSize = 13.sp)

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            val currentUserEmail =
                                FirebaseAuth.getInstance().currentUser?.email ?: "Unknown user"

                            val responderName = currentUserEmail.substringBefore("@")

                            val responseData = hashMapOf(
                                "alertId" to alert.id,
                                "hospitalName" to alert.hospitalName,
                                "bloodGroup" to alert.bloodGroup,
                                "location" to alert.location,
                                "responderName" to responderName,
                                "message" to "$responderName is coming for ${alert.bloodGroup} blood donation at ${alert.hospitalName}",
                                "status" to "I'm Coming"
                            )

                            db.collection("responses").add(responseData)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        context,
                                        "Response sent: $responderName is coming",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        context,
                                        "Error: ${it.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Black),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("I'm Coming")
                    }
                }
            }
        }
    }
}
@Composable
fun CommunityScreen(onBack: () -> Unit) {
    val db = FirebaseFirestore.getInstance()

    var donors by remember { mutableStateOf<List<DonorItem>>(emptyList()) }
    var selectedBloodGroup by remember { mutableStateOf("") }

    LaunchedEffect(selectedBloodGroup) {
        var query = db.collection("users")
            .whereEqualTo("available", true)

        if (selectedBloodGroup.isNotBlank()) {
            query = query.whereEqualTo("bloodGroup", selectedBloodGroup.uppercase())
        }

        query.get()
            .addOnSuccessListener { result ->
                donors = result.documents.map { doc ->
                    DonorItem(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        phone = doc.getString("phone") ?: "",
                        bloodGroup = doc.getString("bloodGroup") ?: "",
                        age = doc.getString("age") ?: "",
                        location = doc.getString("location") ?: "",
                        available = doc.getBoolean("available") ?: true,
                        lastDonationDate = doc.getString("lastDonationDate") ?: "",
                        nextEligibleDate = doc.getString("nextEligibleDate") ?: ""
                    )
                }
            }
    }

    PageContainer(
        title = "Available Donors",
        color = Green,
        onBack = onBack,
        onHome = onBack
    ) {
        InputBox(
            value = selectedBloodGroup,
            onChange = { selectedBloodGroup = it },
            label = "Filter Blood Group Example: O+"
        )

        Text(
            text = "Showing only available donors",
            color = Green,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        donors.forEach { donor ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(LightPink, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(donor.bloodGroup, color = Red, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(donor.name, fontWeight = FontWeight.Bold)
                        Text(donor.location, fontSize = 12.sp, color = Color.Gray)
                        Text("READY TO DONATE", fontSize = 11.sp, color = Green, fontWeight = FontWeight.Bold)
                        Text("Next Eligible: ${donor.nextEligibleDate}", fontSize = 11.sp, color = Color.DarkGray)
                    }

                    Text("☎", color = Red, fontSize = 22.sp)
                }
            }
        }
    }
}

@Composable
fun BloodChartScreen(onBack: () -> Unit) {
    val rows = listOf(
        Triple("A+", "A+, AB+", "A+, A-, O+, O-"),
        Triple("O+", "O+, A+, B+, AB+", "O+, O-"),
        Triple("B+", "B+, AB+", "B+, B-, O+, O-"),
        Triple("AB+", "All", "Everyone"),
        Triple("A-", "A+, A-, AB+, AB-", "A-, O-"),
        Triple("O-", "Everyone", "O- only"),
        Triple("B-", "B+, B-, AB+, AB-", "B-, O-"),
        Triple("AB-", "AB+, AB-", "AB-, A-, B-, O-")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🩸", fontSize = 34.sp)

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "JEEVA-BINDU",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Red
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "WHICH BLOOD TYPES\nAM I COMPATIBLE WITH?",
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Red,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        rows.forEach { row ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(5.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = row.first,
                        color = Red,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.weight(0.8f)
                    )

                    Text(
                        text = row.second,
                        color = Color.Black,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1.5f)
                    )

                    Text(
                        text = row.third,
                        color = Color.Black,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1.6f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBack,
            modifier = Modifier
                .width(210.dp)
                .height(58.dp),
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Red)
        ) {
            Text(
                text = "⌂  Main Menu",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(25.dp))
    }
}

@Composable
fun SettingsScreen(onBack: () -> Unit, onLogout: () -> Unit) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    fun deleteCollection(collectionName: String, successMessage: String) {
        db.collection(collectionName)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show()
                } else {
                    result.documents.forEach { doc ->
                        db.collection(collectionName).document(doc.id).delete()
                    }
                    Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    PageContainer(
        title = "Account Settings",
        color = Red,
        onBack = onBack,
        onHome = onBack
    ) {
        SettingsCard(
            text = "Delete My Alert History",
            icon = "🗑",
            onClick = {
                deleteCollection("alerts", "Alert history deleted")
            }
        )

        SettingsCard(
            text = "Clear Donation History",
            icon = "💧",
            onClick = {
                deleteCollection("responses", "Donation history deleted")
            }
        )

        SettingsCard(
            text = "Clear Notification History",
            icon = "🔔",
            onClick = {
                deleteCollection("alerts", "Notification history cleared")
            }
        )
        SettingsCard(
            text = "Permanently Delete My Account",
            icon = "⚠️",
            onClick = {
                val user = FirebaseAuth.getInstance().currentUser

                if (user != null) {
                    db.collection("users")
                        .whereEqualTo("email", user.email)
                        .get()
                        .addOnSuccessListener { result ->
                            result.documents.forEach { doc ->
                                db.collection("users").document(doc.id).delete()
                            }

                            user.delete()
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Account deleted permanently", Toast.LENGTH_SHORT).show()
                                    onLogout()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Please login again before deleting account", Toast.LENGTH_LONG).show()
                                }
                        }
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                onLogout()
            },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Black),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Log Out")
        }
    }
}

@Composable
fun SettingsCard(
    text: String,
    icon: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = LightPink)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 20.sp)

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                color = Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Text("›", color = Red, fontSize = 24.sp)
        }
    }
}

@Composable
fun PageContainer(
    title: String,
    color: Color,
    onBack: () -> Unit,
    onHome: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 18.dp, end = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🩸",
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "JEEVA-BINDU",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = Color.Black
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                title,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )

            Spacer(modifier = Modifier.height(18.dp))

            content()

            Spacer(modifier = Modifier.height(20.dp))



            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onHome,
                modifier = Modifier.width(150.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Black)
            ) {
                Text("⌂ Main Menu")
            }

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}
@Composable
fun InputBox(value: String, onChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun DropLogo(size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Red, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text("🩸", fontSize = 30.sp, color = Color.White)
    }
}

@Composable
fun CircleBg(size: androidx.compose.ui.unit.Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .background(Color(0xFFFFE3E6), CircleShape)
    )
}