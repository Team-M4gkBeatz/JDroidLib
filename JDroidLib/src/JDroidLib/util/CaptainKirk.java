/* Copyright (C) 2014 beatsleigher.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

package JDroidLib.util;

import JDroidLib.enums.*;

import java.io.*;
import java.util.*;

/**
 * This is Captain Kirk! Say hello! He will be our commander and captain, throughout this journey.
 * He will defeat those meanies and protect your device from unwanted stuff. Or, he will at some point in time!
 * @author beatsleigher
 */
@SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "UnusedAssignment", "StringConcatenationInsideStringBufferAppend"})
public class CaptainKirk {
    
    ResourceManager resMan = null;
    private File adb = null;
    private File fastboot = null;
    
    /**
     * Default constructor: Installs ADB/fastboot and gets other data.
     */
    public CaptainKirk() {
        resMan = new ResourceManager();
        if (System.getProperty("os.name").equals("Linux"))
            resMan.install(OS.LINUX, "Default");
        else if (System.getProperty("os.name").contains("Mac")) 
            resMan.install(OS.MAC_OS, "Default");
        else
            resMan.install(OS.WINDOWS, "Default");
        adb = new File(System.getProperty("user.home") + "/.jdroidlib/bin/adb");
        fastboot = new File(System.getProperty("user.home") + "/.jdroidlib/bin/fastboot");
    }
    
    /**
     * Executes an ADB command:
     * @param shell Issue a shell command.
     * @param remount the device.
     * @param deviceSerial Issue the command to a specific device. If this is set to "", or null, it will be ignored and the command will be issued globally.
     * @param commands to be executed. (Also used for process args when using shell).
     * @return ADB output as String.
     * @throws IOException when something went wrong.
     */
    public String executeADBCommand(boolean shell, boolean remount, String deviceSerial, String[] commands) throws IOException {
        ///////////////////
        // Variables /////
        /////////////////
        StringBuilder str = new StringBuilder();
        ProcessBuilder process = new ProcessBuilder();
        Process pr = null;
        BufferedReader processReader = null;
        List<String> args = new ArrayList();
        String line = "";
        
        ////////////////////
        // Remount device//
        //////////////////
        if (remount) {
            args.add(adb.toString());
            if (deviceSerial != null | !deviceSerial.equals(""))
                args.add("-s " + deviceSerial);
            args.add("remount");
            process.command(args);
            pr = process.start();
            processReader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            while ((line = processReader.readLine()) != null) {
                // Wait for remounting to finish.
            }
            pr.destroy();
            processReader.close();
            args.clear();
            process = new ProcessBuilder();
        }
        
        ////////////////////
        // Execute command/
        //////////////////
        args.add(adb.toString());
        if (deviceSerial != null)
                args.add("-s " + deviceSerial);
        if (shell)
            args.add("shell");
        args.addAll(Arrays.asList(commands));
        process.command(args);
        pr = process.start();
        processReader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        while ((line = processReader.readLine()) != null) {
            str.append(line + "\n");
        }
        pr.destroy();
        processReader.close();
        args.clear();
        line = null;
        
        return str.toString();
    }
    
    /**
     * Executes a fastboot command:
     * @param deviceSerial the target device's serial. If this is set to "" or null, it will be ignored and the command will be issued globally.
     * @param commands the commands to be executed.
     * @return the output.
     * @throws IOException if something went wrong.
     */
    public String executeFastbootCommand(String deviceSerial, String[] commands) throws IOException {
        ///////////////////
        // Variables /////
        /////////////////
        StringBuilder str = new StringBuilder();
        ProcessBuilder process = new ProcessBuilder();
        Process pr = null;
        BufferedReader processReader = null;
        List<String> args = new ArrayList();
        String line = "";
        
        ////////////////////
        // Execute command/
        //////////////////
        args.add(fastboot.toString());
        if (deviceSerial != null | !deviceSerial.equals(""))
            args.add("-s " + deviceSerial);
        args.addAll(Arrays.asList(commands));
        process.command(args);
        pr = process.start();
        processReader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        while ((line = processReader.readLine()) != null)
            str.append(line + "\n");
        pr.destroy();
        processReader.close();
        args.clear();
        line = null;
        
        return str.toString();
    }
    
    /**
     * Reboots device to desired mode -- Will not work if device is in fastboot! For fastboot-reboots, use the respective method!
     * @param deviceSerial The specific device to reboot. Will be ignored if "" or null!
     * @param mode The mode to reboot into.
     * @return the process output.
     * @throws IOException if something went wrong.
     */
    public String ADB_rebootDevice(String deviceSerial, RebootTo mode) throws IOException {
        ///////////////////
        // Variables /////
        /////////////////
        StringBuilder str = new StringBuilder();
        ProcessBuilder process = new ProcessBuilder();
        List<String> args = new ArrayList();
        Process pr = null;
        BufferedReader processReader = null;
        String line = "";
        
        ////////////////////
        // Execute command/
        //////////////////
        args.add(adb.toString());
        if (deviceSerial != null | !deviceSerial.equals(""))
            args.add("-s " + deviceSerial);
        switch (mode) {
            case ANDROID:
                args.add("reboot");
                break;
            case RECOVERY:
                args.add("reboot recovery");
                break;
            case BOOTLOADER:
                args.add("reboot-bootloader");
                break;
        }
        process.command(args);
        pr = process.start();
        processReader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        while ((line = processReader.readLine()) != null) 
            str.append(line + "\n");
        pr.destroy();
        processReader.close();
        args.clear();
        line = null;
        
        return str.toString();
    }
    
    /**
     * Reboots device to desired mode -- Will NOT work if device is booted to recovery or Android! Use ADB:rebootDevice() for those reboots!
     * @param deviceSerial The specific device.
     * @param mode The mode to reboot the device to. 
     * @return the output.
     * @throws IOException if something went wrong.
     */
    public String fastboot_rebootDevice(String deviceSerial, RebootTo mode) throws IOException {
        ///////////////////
        // Variables /////
        /////////////////
        StringBuilder str = new StringBuilder();
        ProcessBuilder process = new ProcessBuilder();
        List<String> args = new ArrayList();
        Process pr = null;
        BufferedReader processReader = null;
        String line = "";
        
        ////////////////////
        // Execute command/
        //////////////////
        args.add(fastboot.toString());
        if (deviceSerial != null | !deviceSerial.equals(""))
            args.add("-s " + deviceSerial);
        switch (mode) {
            case ANDROID:
                args.add("reboot");
                break;
            case RECOVERY:
                args.add("reboot");
                break;
            case BOOTLOADER:
                args.add("reboot-bootloader");
                break;
        }
        process.command(args);
        pr = process.start();
        processReader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        while ((line = processReader.readLine()) != null) 
            str.append(line + "\n");
        pr.destroy();
        processReader.close();
        args.clear();
        line = null;
        
        return str.toString();
    }
    
    /**
     * Gets a List(String) of devices and their respective states.
     * @return devices and device states.
     * @throws IOException if something went wrong.
     */
    public List<String> getConnectedDevices() throws IOException {
        List<String> devices = new ArrayList();
        String[] cmd = {"devices"};
        
        String raw = executeADBCommand(false, false, null, cmd);
        BufferedReader reader = new BufferedReader(new StringReader(raw));
        String line = "";
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("List "))
                continue;
            devices.add(line);
        }
        
        return devices;
    }
    
}

/*Please ignore this. This is just here, so I don't always have to open methods, but can just C&P comments I need.*/
///////////////////
// Variables /////
/////////////////


////////////////////
// Execute command/
//////////////////