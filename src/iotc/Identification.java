package iotc;

import iotc.db.Command;
import iotc.db.Device;
import iotc.db.Room;
import iotc.db.Sensor;

/**
 * 識別子
 * @author atsushi-o
 */
public class Identification {
    private Room room;
    private Device device;
    private Command command;
    private Sensor sensor;

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
    public Sensor getSensor() {
        return sensor;
    }

    /**
     * @param sensor the sensor to set
     */
    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
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
                } else if (sensor != null) {
                    sb.append("::").append(sensor.getName());
                }
            }
        }
        if (sb.length() <= 0) {
            sb.append("not defined");
        }
        return sb.toString();
    }
}
