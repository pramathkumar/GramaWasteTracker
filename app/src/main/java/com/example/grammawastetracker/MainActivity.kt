package com.example.grammawastetracker
import androidx.compose.runtime.*
import android.net.Uri
import android.widget.Toast
import android.annotation.SuppressLint
import android.location.Location
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.pm.PackageManager
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.database.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.activity.compose.BackHandler
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.text.KeyboardOptions
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.filled.Menu
import kotlinx.coroutines.launch
import coil.compose.AsyncImage

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.shape.RoundedCornerShape
data class Blackspot(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val imageUrl: String = ""
)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Google Maps
        MapsInitializer.initialize(this)

        setContent {

            var isLoggedIn by remember {
                mutableStateOf(false)
            }

            var isAdmin by remember {
                mutableStateOf(false)
            }

            if (!isLoggedIn) {

                LoginScreen(

                    onResidentLogin = {
                        isLoggedIn = true
                        isAdmin = false
                    },

                    onAdminLogin = {
                        isLoggedIn = true
                        isAdmin = true
                    }
                )

            } else {

                MainScreen(

                    isAdmin = isAdmin,

                    onLogout = {

                        FirebaseAuth.getInstance().signOut()

                        isLoggedIn = false
                        isAdmin = false
                    }
                )
            }
        }
    }
}

@Composable
fun LoginScreen(
    onResidentLogin: () -> Unit,
    onAdminLogin: () -> Unit
) {

    val auth = FirebaseAuth.getInstance()

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Grama Waste Tracker",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text("Email")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text("Password")
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                auth.signInWithEmailAndPassword(
                    email,
                    password
                )

                    .addOnSuccessListener {

                        if (email == "admin@gmail.com") {

                            Toast.makeText(
                                context,
                                "Admin Login Successful",
                                Toast.LENGTH_LONG
                            ).show()

                            onAdminLogin()

                        } else {

                            Toast.makeText(
                                context,
                                "Resident Login Successful",
                                Toast.LENGTH_LONG
                            ).show()

                            onResidentLogin()
                        }
                    }

                    .addOnFailureListener {

                        Toast.makeText(
                            context,
                            "Login Failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            },

            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Login")
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun MainScreen(
    isAdmin: Boolean,
    onLogout: () -> Unit
) {

    val navController = rememberNavController()

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val scope = rememberCoroutineScope()
    BackHandler(
        enabled = drawerState.isOpen
    ) {

        scope.launch {

            drawerState.close()
        }
    }
    ModalNavigationDrawer(

        drawerState = drawerState,

        drawerContent = {

            ModalDrawerSheet {

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text =
                        if (isAdmin)
                            "👨‍💼 Admin"
                        else
                            "👤 Resident",

                    modifier = Modifier.padding(16.dp),

                    style =
                        MaterialTheme.typography.headlineSmall
                )

                HorizontalDivider()

                NavigationDrawerItem(

                    label = {
                        Text("Logout")
                    },

                    selected = false,

                    onClick = {

                        scope.launch {

                            drawerState.close()
                        }

                        onLogout()
                    }
                )
            }
        }
    ) {

        Scaffold(

            topBar = {

                TopAppBar(

                    navigationIcon = {

                        IconButton(
                            onClick = {

                                scope.launch {

                                    drawerState.open()
                                }
                            }
                        ) {

                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    },

                    title = {

                        Text(
                            if (isAdmin)
                                "👨‍💼 Admin Panel"
                            else
                                "👤 Resident Panel"
                        )
                    }
                )
            },

            bottomBar = {

                NavigationBar {

                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            navController.navigate("map")
                        },
                        icon = {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = {
                            Text("Home")
                        }
                    )

                    if (isAdmin) {

                        NavigationBarItem(
                            selected = false,
                            onClick = {
                                navController.navigate("admin")
                            },
                            icon = {
                                Icon(
                                    Icons.Default.List,
                                    contentDescription = "Admin"
                                )
                            },
                            label = {
                                Text("Admin")
                            }
                        )

                    } else {

                        NavigationBarItem(
                            selected = false,
                            onClick = {
                                navController.navigate("report")
                            },
                            icon = {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = "Report"
                                )
                            },
                            label = {
                                Text("Report")
                            }
                        )

                        NavigationBarItem(
                            selected = false,
                            onClick = {
                                navController.navigate("guide")
                            },
                            icon = {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = "Guide"
                                )
                            },
                            label = {
                                Text("Guide")
                            }
                        )
                    }
                }
            }

        ) { paddingValues ->

            NavHost(
                navController = navController,
                startDestination = "map",
                modifier = Modifier.padding(paddingValues)
            ) {

                composable("map") {
                    MapScreen(isAdmin)
                }

                composable("report") {
                    ReportScreen()
                }

                composable("guide") {
                    GuideScreen()
                }

                composable("admin") {
                    AdminScreen()
                }
            }
        }
    }
}


@Composable
fun MapScreen(isAdmin: Boolean) {

    var tractorLatitude by remember {
        mutableStateOf(12.2958)
    }

    var tractorLongitude by remember {
        mutableStateOf(76.6394)
    }
    var trackingEnabled by remember {
        mutableStateOf(false)
    }
    var mapInitialized by remember {
        mutableStateOf(false)
    }
    var alreadyNotified by remember {
        mutableStateOf(false)
    }

    val blackspots = remember {
        mutableStateListOf<Blackspot>()
    }

    val context = LocalContext.current
    val  notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { }
    val mapView = remember {
        MapView(context)
    }

    val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)
    val tractorRef =
        FirebaseDatabase.getInstance()
            .getReference("tractor")
    LaunchedEffect(trackingEnabled) {
        if (
            isAdmin &&
            trackingEnabled &&
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            val locationRequest =
                LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    5000
                ).build()

            val locationCallback =
                object : LocationCallback() {

                    override fun onLocationResult(
                        result: LocationResult
                    ) {

                        val location =
                            result.lastLocation

                        if (location != null) {
                            Toast.makeText(
                                context,
                                "Tracking Updated",
                                Toast.LENGTH_SHORT
                            ).show()
                            val tractorData = mapOf(

                                "latitude" to location.latitude,

                                "longitude" to location.longitude
                            )

                            tractorRef.setValue(tractorData)
                        }
                    }
                }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        }
        mapView.onCreate(Bundle())
        mapView.onResume()

        val database = FirebaseDatabase.getInstance()

        // 🚜 Tractor Location Listener
        database.getReference("tractor")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    tractorLatitude =
                        snapshot.child("latitude")
                            .getValue(Double::class.java)
                            ?: 12.2958

                    tractorLongitude =
                        snapshot.child("longitude")
                            .getValue(Double::class.java)
                            ?: 76.6394

                    // 📍 Check User Distance
                    if (
                        ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {

                        fusedLocationClient.lastLocation
                            .addOnSuccessListener { location ->

                                if (location != null) {

                                    val results = FloatArray(1)

                                    Location.distanceBetween(
                                        location.latitude,
                                        location.longitude,

                                        tractorLatitude,
                                        tractorLongitude,

                                        results
                                    )

                                    val distanceInMeters = results[0]

                                    // 🔔 Notify if nearby
                                    if (!isAdmin) {

                                        // 🚜 Tractor entered nearby zone
                                        if (
                                            distanceInMeters < 500 &&
                                            !alreadyNotified
                                        ) {

                                            showNearbyNotification(context)

                                            alreadyNotified = true
                                        }

                                        // 🚜 Tractor moved away again
                                        if (distanceInMeters > 700) {

                                            alreadyNotified = false
                                        }
                                    }
                                }
                            }
                    }

                    updateMap(
                        mapView,
                        tractorLatitude,
                        tractorLongitude,
                        blackspots,
                        mapInitialized
                    ) {
                        mapInitialized = true
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        // 🗑 Blackspots Listener
        database.getReference("blackspots")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    blackspots.clear()

                    for (child in snapshot.children) {

                        val blackspot =
                            child.getValue(Blackspot::class.java)

                        blackspot?.let {
                            blackspots.add(it)
                        }
                    }

                    updateMap(
                        mapView,
                        tractorLatitude,
                        tractorLongitude,
                        blackspots,
                        mapInitialized
                    ) {
                        mapInitialized = true
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
    if (isAdmin) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(
                text = "🚜 Device Tracking"
            )

            Switch(
                checked = trackingEnabled,

                onCheckedChange = {

                    trackingEnabled = it
                }
            )
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        AndroidView(
            factory = {
                mapView
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isAdmin) {

            Card(
                modifier = Modifier
                    .padding(16.dp),

                elevation = CardDefaults.cardElevation(8.dp)
            ) {

                Row(
                    modifier = Modifier
                        .padding(12.dp),

                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    Text(
                        text = "🚜 Device Tracking"
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Switch(
                        checked = trackingEnabled,

                        onCheckedChange = {

                            trackingEnabled = it
                        }
                    )
                }
            }
        }
    }
}
fun updateMap(
    mapView: MapView,
    tractorLatitude: Double,
    tractorLongitude: Double,
    blackspots: List<Blackspot>,
    mapInitialized: Boolean,
    onMapInitialized: () -> Unit
) {

    mapView.getMapAsync { googleMap ->

        googleMap.clear()

        // Tractor Marker
        val tractorLocation = LatLng(
            tractorLatitude,
            tractorLongitude
        )

        googleMap.addMarker(
            MarkerOptions()
                .position(tractorLocation)
                .title("🚜 Tractor")
                .icon(
                    BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_AZURE
                    )
                )
        )

        if (!mapInitialized) {

            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    tractorLocation,
                    17f
                )
            )

            onMapInitialized()
        }

        // Blackspot Markers
        blackspots.forEach { spot ->

            val blackspotLocation = LatLng(
                spot.latitude,
                spot.longitude
            )

            googleMap.addMarker(
                MarkerOptions()
                    .position(blackspotLocation)
                    .title("🗑 Garbage Blackspot")
                    .icon(
                        BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_GREEN
                        )
                    )
            )
        }
    }
}
fun showNearbyNotification(context: Context) {

    val channelId = "tractor_alert"

    val notificationManager =
        context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

    // ✅ Create Notification Channel only for Android 8+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        val channel = NotificationChannel(
            channelId,
            "Tractor Alerts",
            NotificationManager.IMPORTANCE_HIGH
        )

        notificationManager.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(
        context,
        channelId
    )

        .setContentTitle("🚜 Tractor Nearby")
        .setContentText(
            "Waste collection vehicle is near your area"
        )
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    // ✅ Notification Permission Check
    if (
        ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    ) {

        NotificationManagerCompat.from(context)
            .notify(1, notification)
    }
}

@SuppressLint("MissingPermission")

@Composable
fun ReportScreen() {

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val context = LocalContext.current

    val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    // Image Picker
    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->

            imageUri = uri
        }

    // Permission Launcher
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (!granted) {

                Toast.makeText(
                    context,
                    "Location permission denied",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Report Garbage Blackspot",
            style = MaterialTheme.typography.headlineSmall
        )

        Button(
            onClick = {
                imagePickerLauncher.launch("image/*")
            }
        ) {

            Text("Select Garbage Photo")
        }

        imageUri?.let { uri ->

            Text("Photo Selected ✅")

            Button(
                onClick = {

                    // Check Permission
                    if (
                        ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {

                        permissionLauncher.launch(
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        )

                        return@Button
                    }

                    // Get Current Location
                    val cancellationTokenSource =
                        CancellationTokenSource()

                    fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        cancellationTokenSource.token
                    )

                        .addOnSuccessListener { location ->

                            if (location == null) {

                                Toast.makeText(
                                    context,
                                    "Unable to get location",
                                    Toast.LENGTH_LONG
                                ).show()

                                return@addOnSuccessListener
                            }

                            val latitude = location.latitude
                            val longitude = location.longitude

                            val storageRef = FirebaseStorage
                                .getInstance()
                                .reference
                                .child(
                                    "blackspots/${
                                        System.currentTimeMillis()
                                    }.jpg"
                                )

                            // Upload Image
                            storageRef.putFile(uri)

                                .addOnSuccessListener {

                                    storageRef.downloadUrl
                                        .addOnSuccessListener { downloadUrl ->

                                            val reportData = mapOf(

                                                "imageUrl" to downloadUrl.toString(),

                                                "latitude" to latitude,

                                                "longitude" to longitude,

                                                "timestamp" to System.currentTimeMillis()
                                            )

                                            FirebaseDatabase.getInstance()
                                                .getReference("blackspots")
                                                .push()
                                                .setValue(reportData)

                                            Toast.makeText(
                                                context,
                                                "Blackspot Uploaded ✅",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                }

                                .addOnFailureListener {

                                    Toast.makeText(
                                        context,
                                        "Upload Failed",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        }
                }
            ) {

                Text("Upload Report")
            }
        }
    }
}
@Composable
fun GuideScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Waste Segregation",
            style = MaterialTheme.typography.headlineSmall
        )

        Card {

            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                Text(
                    text = "Dry Waste",
                    style = MaterialTheme.typography.titleMedium
                )

                Text("Plastic, Paper, Metal")
            }
        }

        Card {

            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                Text(
                    text = "Wet Waste",
                    style = MaterialTheme.typography.titleMedium
                )

                Text("Food, Vegetables")
            }

                }
            }
        }
@Composable
fun AdminScreen() {

    val blackspots = remember {
        mutableStateListOf<Pair<String, Blackspot>>()
    }

    val context = LocalContext.current

    LaunchedEffect(Unit) {

        FirebaseDatabase.getInstance()
            .getReference("blackspots")

            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    blackspots.clear()

                    for (child in snapshot.children) {

                        val id = child.key ?: ""

                        val blackspot =
                            child.getValue(Blackspot::class.java)

                        if (blackspot != null) {

                            blackspots.add(
                                Pair(id, blackspot)
                            )
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    LazyColumn(

        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),

        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        items(blackspots) { item ->

            val reportId = item.first
            val spot = item.second

            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {

                Column(
                    modifier = Modifier.padding(12.dp)
                ) {

                    if (spot.imageUrl.isNotEmpty()) {

                        AsyncImage(
                            model = spot.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Latitude: ${spot.latitude}"
                    )

                    Text(
                        text = "Longitude: ${spot.longitude}"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(

                        onClick = {

                            FirebaseDatabase
                                .getInstance()
                                .getReference("blackspots")
                                .child(reportId)
                                .removeValue()

                            Toast.makeText(
                                context,
                                "Report Removed ✅",
                                Toast.LENGTH_LONG
                            ).show()
                        },

                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                                MaterialTheme.colorScheme.error
                        )
                    ) {

                        Text("Mark as Cleaned")
                    }
                }
            }
        }
    }
}
