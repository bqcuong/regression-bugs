package edu.harvard.pallmall.service;

import javax.usb.*;
import java.util.List;

/**
 * The RFID Scanner Service Implementor is
 */
public class RfidScannerServiceImpl {

    /**
     * Searches through all human interface devices attached to machine
     * in search of specified RFID scanner.
     * @param usbHub - root hub for the services of the host manager
     * @param vendorIdentifier - vendor ID of the RFID scanner
     * @param productIdentifier - product ID of the RFID scanner
     * @return
     */
    public static UsbDevice findAttachedUsbRfidScanner(UsbHub usbHub, short vendorIdentifier, short productIdentifier)
    {
        List<UsbDevice> usbDevices = usbHub.getAttachedUsbDevices();
        for (UsbDevice device : usbDevices) {
            short deviceProductId = device.getUsbDeviceDescriptor().idProduct();
            short deviceVendorId = device.getUsbDeviceDescriptor().idVendor();

            if (deviceProductId == productIdentifier && deviceVendorId == vendorIdentifier){
                return device;
            }

            if (device.isUsbHub()) {
                device = findAttachedUsbRfidScanner((UsbHub) device, vendorIdentifier, productIdentifier);
                if (device != null) {
                    return device;
                }
            }

        }
        return null;
    }

    /**
     * Retrieves the communication interface of RFID scanner
     * @param usbRfidScanner - RFID scanner
     * @return
     */
    public static UsbInterface lookupRfidScannerInterface(UsbDevice usbRfidScanner) {
        UsbConfiguration configuration = usbRfidScanner.getActiveUsbConfiguration();
        UsbInterface rfidScannerInterface =  configuration.getUsbInterface((byte) 0x00);
        return rfidScannerInterface;
    }

}
