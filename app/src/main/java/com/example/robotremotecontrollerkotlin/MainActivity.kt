package com.example.robotremotecontrollerkotlin

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import java.io.IOException
import java.io.OutputStream
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener, View.OnTouchListener, NavigationView.OnNavigationItemSelectedListener{

    private var PORT_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")

    private var bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private lateinit var bluetoothSocket: BluetoothSocket
    private lateinit var bluetoothDevice: BluetoothDevice
    private var outputStream: OutputStream? = null
    private var find: Boolean = false
    private var command: Char = '\u0000'

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation_drawer.setNavigationItemSelectedListener(this)

        pairBtn.setOnClickListener(this)
        navigation_menu.setOnClickListener(this)

        forward.setOnTouchListener(this)
        backward.setOnTouchListener(this)
        leftward.setOnTouchListener(this)
        rightward.setOnTouchListener(this)

        btChecker()
    }


    private fun showSettingsAlert() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)

        alertDialog.setTitle(resources.getString(R.string.alert_title))
        alertDialog.setMessage(resources.getString(R.string.alert_message))
        alertDialog.setPositiveButton(resources.getString(R.string.alert_btn_positive),
            DialogInterface.OnClickListener { dialog, which ->
                val intent: Intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                startActivity(intent)
            })
        alertDialog.setNegativeButton(resources.getString(R.string.alert_btn_negative),
            DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
            })

        alertDialog.show()
    }

    private fun btChecker() {
        bluetoothAdapter
        if(bluetoothAdapter.isEnabled) {
            Toast.makeText(this, resources.getString(R.string.toast_enable), Toast.LENGTH_SHORT).show()
        } else {
            showSettingsAlert()
        }

        var bondedDevice: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
        if(bondedDevice.isEmpty()) {
            Toast.makeText(this, resources.getString(R.string.toast_bonded), Toast.LENGTH_LONG).show()
        } else {
            for (iterator in bondedDevice) {
                if(iterator.address != null) {
                    bluetoothDevice = iterator
                    Log.e("TAG", bluetoothDevice.toString())
                    find = true
                    break
                }
            }
        }
    }

    private fun pairingDevices() {
        var connected: Boolean = true

        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(PORT_UUID)
            bluetoothSocket.connect()
            robotName.text = bluetoothDevice.name
            Toast.makeText(this, resources.getString(R.string.toast_connection_success), Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            connected = false
        }

        if (connected) {
            try {
                outputStream = bluetoothSocket.outputStream
            } catch (e:IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.pairBtn -> {
                pairingDevices()
                Log.e("TAG", "pairBtn")
            }
            R.id.firstGear -> {
            }
            R.id.secondGear -> {
            }
            R.id.thirdGear -> {
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                when (v.id) {
                    R.id.forward -> {
                        Log.e("TAG", "forward")
                        command = 'W'

                        try {
                            outputStream?.write(command.toInt())
                            Log.e("W", "command $command")
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }
                    R.id.backward -> {
                        Log.e("TAG", "backward")
                        command = 'S'
                        try {
                            outputStream?.write(command.toInt())
                            Log.e("S", "command $command")
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    R.id.leftward -> {
                        Log.e("TAG", "leftward")
                        command = 'A'
                        try {
                            outputStream?.write(command.toInt())
                            Log.e("A", "command $command")
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    R.id.rightward -> {
                        Log.e("TAG", "rightward")
                        command = 'D'
                        try {
                            outputStream?.write(command.toInt())
                            Log.e("D", "command $command")
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                command = 'N'
                try {
                    outputStream?.write(command.toInt())
                    Log.e("N", "command $command")
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.navigation_drawer_menu_language -> {
                Log.e("TAG", "Language Settings")
            }
        }
        return true
    }
}
