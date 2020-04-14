# Bluetooth tracing Proof of Concept

The goal of this project is to confirm that Bluetooth technology on both Android and iOS can be used for tracing solutions. In order to verify that, the following features were put in scope:

1. Background bluetooth service advertising (including some unique ID)
2. Background bluetooth service scan (and reading of unique ID)

Other things were checked in this PoC:

1. Battery level consumption
2. Feasibility of constant background Bluetooth activity
3. Interference with other apps using Bluetooth


### Cloning Instruction 

```
git clone https://github.com/epam/covidresistance
```

### Description per platform

##### iOS:

Foreground and background bluetooth advertising/scanning accomplished with standard CoreBluetooth framework's peripheral/central manager (see PeripheralService and CentralService). There is a limitation for background execution of PoC according to the Apple answer on a support ticket:

When a scanning app is in the background, the didDiscoverPeripheral() method will only be called once when the other phone is first encountered, as opposed to multiple times when the app is in the foreground. As Bluetooth is a shared resource, when other apps and system resources need to use Bluetooth, advertising for apps in the background will slow down, and may even stop for short periods of time. On the scanning side, the scan rate also drops once the app is in the background. Also, both these rates will further slow down when the phone screen goes off, and even further down after a while when the phone goes to sleep mode.

##### Android:

Foreground and background (when app is not on the screen) bluetooth advertising/scanning accomplished with standard Foreground Service. There are limitation regarding discovering of iOS devices which are in the backgorund regarding to its limitations.

The config of bluetooth service and shared data is define in com.epam.crowdresitance.bluetooth.BtConfig file.

### License Information

Licensed under Apache 2.0, [LICENSE](https://github.com/company/repo/blob/master/LICENSE)
