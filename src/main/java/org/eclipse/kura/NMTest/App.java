package org.eclipse.kura.NMTest;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.freedesktop.dbus.exceptions.DBusException;

public class App {
    public static void main( String[] args ) throws DBusException {
        NMDbusConnector nm = null;
        try {
            
            nm = NMDbusConnector.getInstance();
            
            nm.printInterfaceInfo("eth0");

        } catch (DBusException _ex) {
            _ex.printStackTrace();
        } finally {
            if(Objects.nonNull(nm)) {
                nm.closeConnection();
            }
        }
    }
}
