package com.xc.framework.port.usb;

import android.content.Context;
import android.hardware.usb.UsbDevice;

/**
 * Date：2019/11/27
 * Author：ZhangXuanChen
 * Description：usb参数
 */
public class UsbPortParam {
    /**
     * usb设备
     */
    private UsbDevice usbDevice;
    /**
     * 波特率
     */
    private int baudrate;
    /**
     * 数据位，默认8,可选值为5~8
     */
    private int dataBits = 8;
    /**
     * 停止位，1:1位停止位(默认)；2:2位停止位
     */
    private int stopBits = 1;
    /**
     * 校验位，0:无校验位(默认)；1:奇校验位(ODD);2:偶校验位(EVEN)
     */
    private int parity = 0;


    public UsbPortParam(UsbDevice usbDevice, int baudrate) {
        this.usbDevice = usbDevice;
        this.baudrate = baudrate;
    }

    public UsbPortParam(Context context, int vid, int pid, int baudrate) {
        this.usbDevice = UsbPortFinder.getInstance().getUsbDevice(context, vid, pid);
        this.baudrate = baudrate;
    }

    public UsbPortParam(Context context, int vid, int pid, int baudrate, int dataBits, int stopBits, int parity) {
        this.usbDevice = UsbPortFinder.getInstance().getUsbDevice(context, vid, pid);
        this.baudrate = baudrate >= 0 ? baudrate : 0;
        this.dataBits = dataBits >= 0 ? dataBits : this.dataBits;
        this.stopBits = stopBits >= 0 ? stopBits : this.stopBits;
        this.parity = parity >= 0 ? parity : this.parity;
    }

    public UsbDevice getUsbDevice() {
        return usbDevice;
    }

    public void setUsbDevice(UsbDevice usbDevice) {
        this.usbDevice = usbDevice;
    }
    public void setUsbDevice(Context context, int vid, int pid) {
        this.usbDevice= UsbPortFinder.getInstance().getUsbDevice(context, vid, pid);
    }
    public int getBaudrate() {
        return baudrate;
    }

    public void setBaudrate(int baudrate) {
        this.baudrate = baudrate;
    }

    public int getDataBits() {
        return dataBits;
    }

    public void setDataBits(int dataBits) {
        this.dataBits = dataBits;
    }

    public int getStopBits() {
        return stopBits;
    }

    public void setStopBits(int stopBits) {
        this.stopBits = stopBits;
    }

    public int getParity() {
        return parity;
    }

    public void setParity(int parity) {
        this.parity = parity;
    }


}