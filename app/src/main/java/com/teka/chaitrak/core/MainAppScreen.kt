package com.teka.chaitrak.core

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.teka.chaitrak.core.navigation.MainNavGraph
import com.teka.chaitrak.core.navigation.rememberAppState


@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainAppScreen() {
    val navController = rememberNavController()
    val newBackStackEntry by navController.currentBackStackEntryAsState()
    val appState = rememberAppState(navHostController = navController)
    val route = newBackStackEntry?.destination?.route
    val context = LocalContext.current

    Box {
        MainNavGraph(navController = appState.navHostController)
    }

}






@Composable
fun enableBluetooth(
    launcher: ActivityResultLauncher<Intent>,
    bluetoothAdapter: BluetoothAdapter
) {
    if (!bluetoothAdapter.isEnabled) {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        launcher.launch(enableBtIntent)
    }
}

@SuppressLint("MissingPermission")
@Composable
fun makeDiscoverable(
    launcher: ActivityResultLauncher<Intent>,
    bluetoothAdapter: BluetoothAdapter
) {
    if (!bluetoothAdapter.isDiscovering) {
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        launcher.launch(discoverableIntent)
    }
}

@SuppressLint("MissingPermission")
@Composable
fun disableBluetooth(bluetoothAdapter: BluetoothAdapter) {
    if (bluetoothAdapter.isEnabled) {
        bluetoothAdapter.disable()
        Toast.makeText(LocalContext.current, "Turning off Bluetooth", Toast.LENGTH_LONG).show()
    }
}

fun handlePermissionResults(context: Context, permissions: Map<String, Boolean>) {
    val allPermissionsGranted = permissions.all { it.value }

    if (allPermissionsGranted) {
        Toast.makeText(context, "All permissions granted", Toast.LENGTH_SHORT).show()
    } else {
        permissions.forEach { (permission, isGranted) ->
            val message = if (isGranted) "$permission granted" else "$permission denied"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}