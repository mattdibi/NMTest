package org.eclipse.kura.NMTest;

import org.freedesktop.NetworkManager;
import org.freedesktop.dbus.interfaces.DBusSigHandler;
import org.freedesktop.dbus.messages.DBusSignal;

public class Client implements DBusSigHandler {
    public void handle(DBusSignal s) {
        if (s instanceof NetworkManager.DeviceAdded) {
            System.out.println("Device added: " + ((NetworkManager.DeviceAdded) s).getName());
            System.out.println("On interface: " + ((NetworkManager.DeviceAdded) s).getInterface());
        } else if (s instanceof NetworkManager.DeviceRemoved) {
            System.out.println("Device removed: " + ((NetworkManager.DeviceRemoved) s).getName());
            System.out.println("On interface: " + ((NetworkManager.DeviceRemoved) s).getInterface());
        }
    }
}
