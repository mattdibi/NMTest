package org.eclipse.kura.NMTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import org.freedesktop.NetworkManager;
import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.exceptions.DBusExecutionException;
import org.freedesktop.dbus.interfaces.Properties;
import org.freedesktop.dbus.types.UInt32;
import org.freedesktop.dbus.types.Variant;
import org.freedesktop.networkmanager.Device;
import org.freedesktop.networkmanager.Settings;
import org.freedesktop.networkmanager.settings.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NMDbusConnector {

    private static final Logger logger = LoggerFactory.getLogger(NMDbusConnector.class);
    private static final String NM_BUS_NAME = "org.freedesktop.NetworkManager";
    private static final String NM_BUS_PATH = "/org/freedesktop/NetworkManager";
    private static final String NM_SETTINGS_PATH = "/org/freedesktop/NetworkManager/Settings";

    private static final List<NMDeviceType> SUPPORTED_DEVICES = Arrays.asList(NMDeviceType.NM_DEVICE_TYPE_ETHERNET,
            NMDeviceType.NM_DEVICE_TYPE_WIFI);
    private static final List<KuraInterfaceStatus> SUPPORTED_STATUSES = Arrays.asList(KuraInterfaceStatus.DISABLED,
            KuraInterfaceStatus.ENABLEDLAN, KuraInterfaceStatus.ENABLEDWAN, KuraInterfaceStatus.UNMANAGED);

    private static NMDbusConnector instance;
    private DBusConnection dbusConnection;
    private NetworkManager nm;

    private NMDbusConnector(DBusConnection dbusConnection) throws DBusException {
        this.dbusConnection = Objects.requireNonNull(dbusConnection);
        this.nm = this.dbusConnection.getRemoteObject(NM_BUS_NAME, NM_BUS_PATH, NetworkManager.class);
    }

    public synchronized static NMDbusConnector getInstance() throws DBusException {
        return getInstance(DBusConnection.getConnection(DBusConnection.DEFAULT_SYSTEM_BUS_ADDRESS));
    }

    public synchronized static NMDbusConnector getInstance(DBusConnection dbusConnection) throws DBusException {
        if (Objects.isNull(instance)) {
            instance = new NMDbusConnector(dbusConnection);
        }

        return instance;
    }

    public DBusConnection getDbusConnection() {
        return this.dbusConnection;
    }

    public void closeConnection() {
        dbusConnection.disconnect();
    }

    public void checkPermissions() {
        Map<String, String> getPermissions = nm.GetPermissions();
        for (Entry<String, String> entry : getPermissions.entrySet()) {
            logger.info("Permission for {}: {}", entry.getKey(), entry.getValue());
        }
    }

    public synchronized void apply(Map<String, Object> networkConfiguration) throws DBusException {
        logger.info("Applying configuration using NetworkManager Dbus connector");

        NetworkProperties properties = new NetworkProperties(networkConfiguration);

        // Handle configured devices
        List<String> configuredInterfaces = properties.getStringList("net.interfaces");
        for (String iface : configuredInterfaces) {
            Device device = getDeviceByIpIface(iface);
            NMDeviceType deviceType = getDeviceType(device);
            Boolean isNMManaged = getDeviceManaged(device);

            KuraInterfaceStatus ip4Status = KuraInterfaceStatus
                    .fromString(properties.get(String.class, "net.interface.%s.config.ip4.status", iface));
            KuraInterfaceStatus ip6Status = KuraInterfaceStatus
                    .fromString(properties.get(String.class, "net.interface.%s.config.ip6.status", iface));
            NMDeviceEnable deviceStatus = NMDeviceEnable.fromKuraInterfaceStatus(ip4Status, ip6Status);

            if (!isNMManaged || !SUPPORTED_DEVICES.contains(deviceType) || !SUPPORTED_STATUSES.contains(ip4Status) || !SUPPORTED_STATUSES.contains(ip6Status)) {
                logger.warn("Device \"{}\" of type \"{}\" with status \"{}\" currently not supported (is NM managed: {})", iface,
                        deviceType, ip4Status, isNMManaged);
                continue;
            }

            logger.info("Settings iface \"{}\":{}", iface, deviceType);

            Optional<Connection> connection = getAppliedConnection(device);

            if (deviceStatus == NMDeviceEnable.DISABLED) {
                device.Disconnect();
                if (connection.isPresent()) {
                    connection.get().Delete();
                }
                continue;
            } else if(deviceStatus == NMDeviceEnable.UNMANAGED) {
                // TODO: Set it as unmanaged
            } else { // NMDeviceEnable.ENABLED
                // TODO: Handle if !isNMManaged -> Set it as managed
                Map<String, Map<String, Variant<?>>> newConnectionSettings = NMSettingsConverter.buildSettings(properties,
                        connection, iface, deviceType);

                logger.info("New settings: {}", newConnectionSettings);

                if (connection.isPresent()) {
                    logger.info("Current settings: {}", connection.get().GetSettings());

                    connection.get().Update(newConnectionSettings);
                    nm.ActivateConnection(new DBusPath(connection.get().getObjectPath()),
                            new DBusPath(device.getObjectPath()), new DBusPath("/"));
                } else {
                    nm.AddAndActivateConnection(newConnectionSettings, new DBusPath(device.getObjectPath()),
                            new DBusPath("/"));
                }
            }
        }
        
        // Handle not configured devices
        List<Device> availableInterfaces = getAllDevices();

        for(Device device : availableInterfaces) {
            NMDeviceType deviceType = getDeviceType(device);
            Boolean isNMManaged = getDeviceManaged(device);

            String ipInterface = getDeviceIpInterface(device);

            if (!isNMManaged || !SUPPORTED_DEVICES.contains(deviceType)) {
                logger.warn("Device \"{}\" of type \"{}\" currently not supported (is NM managed: {})", ipInterface,
                        deviceType, isNMManaged);
                continue;
            }

            if(!configuredInterfaces.contains(ipInterface)) {
                logger.warn("Device \"{}\" of type \"{}\" not configured. Disabling...", ipInterface,
                        deviceType, isNMManaged);
                Optional<Connection> connection = getAppliedConnection(device);
                device.Disconnect();
                if (connection.isPresent()) {
                    connection.get().Delete();
                }
            }
        }
    }
    
    private List<Device> getAllDevices() throws DBusException {
        List<DBusPath> devicePaths = nm.GetAllDevices();
        
        List<Device> devices = new ArrayList<>();
        for(DBusPath path : devicePaths) {
            devices.add(dbusConnection.getRemoteObject(NM_BUS_NAME, path.getPath(), Device.class));
        }
        
        return devices;
    }
    
    private Boolean getDeviceManaged(Device device) throws DBusException {
        Properties deviceProperties = dbusConnection.getRemoteObject(NM_BUS_NAME, device.getObjectPath(),
                Properties.class);
        
        return deviceProperties.Get("org.freedesktop.NetworkManager.Device", "Managed");
    }

    private NMDeviceType getDeviceType(Device device) throws DBusException {
        Properties deviceProperties = dbusConnection.getRemoteObject(NM_BUS_NAME, device.getObjectPath(),
                Properties.class);

        return NMDeviceType.fromUInt32(deviceProperties.Get("org.freedesktop.NetworkManager.Device", "DeviceType"));
    }
    
    private String getDeviceIpInterface(Device device) throws DBusException {
        Properties deviceProperties = dbusConnection.getRemoteObject(NM_BUS_NAME, device.getObjectPath(),
                Properties.class);

        return deviceProperties.Get("org.freedesktop.NetworkManager.Device", "Interface");
    }

    private Device getDeviceByIpIface(String iface) throws DBusException {
        DBusPath ifaceDevicePath = nm.GetDeviceByIpIface(iface);
        return dbusConnection.getRemoteObject(NM_BUS_NAME, ifaceDevicePath.getPath(), Device.class);
    }

    private Optional<Connection> getAppliedConnection(Device dev) throws DBusException {
        try {
            Map<String, Map<String, Variant<?>>> connectionSettings = dev.GetAppliedConnection(new UInt32(0))
                    .getConnection();
            String uuid = String.valueOf(connectionSettings.get("connection").get("uuid")).replaceAll("\\[|\\]", "");

            Settings settings = this.dbusConnection.getRemoteObject(NM_BUS_NAME, NM_SETTINGS_PATH, Settings.class);

            DBusPath connectionPath = settings.GetConnectionByUuid(uuid);
            return Optional.of(dbusConnection.getRemoteObject(NM_BUS_NAME, connectionPath.getPath(), Connection.class));
        } catch (DBusExecutionException e) {
            logger.debug("Could not find applied connection for {}, caused by", dev.getObjectPath(), e);
            return Optional.empty();
        }
    }
}