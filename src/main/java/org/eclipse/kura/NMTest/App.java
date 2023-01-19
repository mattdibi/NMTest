package org.eclipse.kura.NMTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;

public class App {
    public static void main( String[] args ) throws DBusException {
        NMDbusConnector nm = null;
        try (DBusConnection dbusConnection = DBusConnection.getConnection(DBusConnection.DEFAULT_SYSTEM_BUS_ADDRESS)) {
            
            nm = NMDbusConnector.createInstance();
            // nm.checkPermissions();
            
            Map<String, Object> config = new HashMap<>();
            config.put("net.interfaces", "eth0");
            config.put("net.interface.eth0.config.dhcpClient4.enabled", false);
            config.put("net.interface.eth0.config.ip4.address", "192.168.1.24");
            config.put("net.interface.eth0.config.ip4.prefix", (short) 24);

            // config.put("net.interface.wlan0.config.wifi.mode", "MASTER"); // -> 802-11-wireless.mode
            // config.put("net.interface.wlan0.config.wifi.master.ssid", "ci_crediamoci");
            // config.put("net.interface.wlan0.config.wifi.master.radioMode", "RADIO_MODE_80211b"); // -> 802-11-wireless.band
            // config.put("net.interface.wlan0.config.wifi.master.passphrase", "testtesttest"); // -> 802-11-wireless-security.psk
            // config.put("net.interface.wlan0.config.wifi.master.securityType", "SECURITY_WPA2"); // -> 802-11-wireless-security.key-mgmt
            // config.put("net.interface.wlan0.config.wifi.master.channel", "6"); // -> 802-11-wireless-security.channel
            // config.put("net.interface.wlan0.config.wifi.master.groupCiphers", "CCMP"); // -> 802-11-wireless-security.group
            // config.put("net.interface.wlan0.config.wifi.master.pairwiseCiphers", "CCMP"); // -> 802-11-wireless-security.pairwise
            
            nm.apply(config);

        } catch (IOException _ex) {
            _ex.printStackTrace();
        } finally {
            if(Objects.nonNull(nm)) {
                nm.closeConnection();
            }
        }
    }
}