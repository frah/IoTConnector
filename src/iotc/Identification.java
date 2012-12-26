package iotc;

import iotc.db.Command;
import iotc.db.Device;
import iotc.db.Room;
import iotc.db.Sensor;

import java.util.List;

/**
 * 識別子
 * @author atsushi-o
 */
public class Identification {
    private Room room;
    private Device device;
    private Command command;
    private List<Sensor> sensors;

    public Identification() {}
    public Identification(Room r) {
        this.room = r;
    }

    /**
     * @return the room
     */
    public Room getRoom() {
        return room;
    }

    /**
     * @param room the room to set
     */
    public void setRoom(Room room) {
        this.room = room;
    }

    /**
     * @return the device
     */
    public Device getDevice() {
        return device;
    }

    /**
     * @param device the device to set
     */
    public void setDevice(Device device) {
        this.device = device;
    }

    /**
     * @return the command
     */
    public Command getCommand() {
        return command;
    }

    /**
     * @param command the command to set
     */
    public void setCommand(Command command) {
        this.command = command;
    }

    /**
     * @return the sensor
     */
    public List<Sensor> getSensors() {
        return sensors;
    }

    /**
     * @param sensor the sensor to set
     */
    public void setSensors(List<Sensor> sensor) {
        this.sensors = sensor;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (room != null) {
            sb.append(room.getName());
            if (device != null) {
                sb.append("::").append(device.getName());
                if (command != null) {
                    sb.append("::").append(command.getName());
                }
            }
            if (sensors != null) {
                sb.append("::");
                String prefix = sb.toString();
                sb.setLength(0);
                for (Sensor s : sensors) {
                    sb.append(prefix);
                    if (device == null) {
                        sb.append(s.getDevice().getName()).append("::");
                    }
                    sb.append(s.getSensorType().getName()).append(System.lineSeparator());
                }
            }
        }
        if (sb.length() <= 0) {
            sb.append("not defined");
        }
        return sb.toString();
    }
}
