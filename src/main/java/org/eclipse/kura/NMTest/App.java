package org.eclipse.kura.NMTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.freedesktop.NetworkManager;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.types.UInt32;
import org.freedesktop.dbus.types.Variant;

import org.freedesktop.networkmanager.Settings;
import org.freedesktop.networkmanager.settings.Connection;

public class App {
    public static void main( String[] args ) throws DBusException, InterruptedException {
        try (DBusConnection dbusConn = DBusConnection.getConnection(DBusConnection.DEFAULT_SYSTEM_BUS_ADDRESS)) {
            dbusConn.addSigHandler(NetworkManager.DeviceAdded.class, new Client());
            dbusConn.addSigHandler(NetworkManager.DeviceRemoved.class, new Client());
            
            while(true) {
                Thread.sleep(1000);
            }

        } catch (IOException _ex) {
            _ex.printStackTrace();
        }
    }
}