package com.lhbank.daq.util;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @author maowenping 2018-06-16 客户端数据采集送到后台
 */
public class LhAppDaqRequestUtils {

    private String lonlat = null;//gps的经纬度

    private double lon = 0d;//经度

    private double lat = 0d;//纬度

    private static LhAppDaqRequestUtils sInstance;

    private Context activity;

    private LhAppDaqRequestUtils() {
    }

    public void init(Context activity) {
        this.activity = activity;
    }

    public static LhAppDaqRequestUtils getInstance() {
        if (sInstance == null) {
            sInstance = new LhAppDaqRequestUtils();
        }
        return sInstance;
    }
    /**
     * 采集硬件信息
     */
    public String getHandwareInfo(String cifseq, String mobile) {
        JSONObject json = new JSONObject();
        Utils.init(activity);
        String model = DeviceUtils.getModel();//获取设备型号；如MI2SC-小米
        String uuid = getUUID(activity);//获取uuid
        String sdkDes = DeviceUtils.getSDKDescVersion();//获取系统SDK版本说明，例如4.3
        String sdkInt = DeviceUtils.getSDKVersion() + "";//获取系统SDK版本号
        String sysVer = DeviceUtils.getSystemVersion();//获取系统版本号,如MIUI9.5
        String mac = DeviceUtils.getMacAddress();//获取mac地址
        String vender = DeviceUtils.getManufacturer();//获取设备产商
        String carrie = PhoneUtils.getSimOperatorByMnc();//获取网络运营商名称，如电信
        String ip = NetworkUtils.getIPAddress(true);//ipv4
        //获取gps经纬度
        if (LocationUtils.isGpsEnabled() && LocationUtils.isLocationEnabled()) {//开gps且有定位服务权限
            LocationUtils.register(10000, 10, new LocationUtils.OnLocationChangeListener() {
                @Override
                public void getLastKnownLocation(Location location) {
                    if (location != null) {
                        //latitude  纬度,longitude 经度
                        lon = location.getLongitude();
                        lat = location.getLatitude();
                        lonlat = lon + "," + lat;
                    }
                }

                @Override
                public void onLocationChanged(Location location) {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }
            });
        }
        String address = null;
        String imei = PhoneUtils.getIMEI();//imei
        String imsi = PhoneUtils.getIMSI();//imsi
        String androidId = DeviceUtils.getAndroidID();
        String serialNumber = DeviceUtils.getSerialNumber();
        String appInfo = null;
        StringBuilder sb = new StringBuilder();
        //获取已安装app信息(非系统自带）
        List<AppUtils.AppInfo> appInfoList = AppUtils.getAppsInfo();
        if (appInfoList != null && !appInfoList.isEmpty()) {
            for (int i = 0; i < appInfoList.size(); i++) {
                if (!StringUtils.isEmpty(appInfoList.get(i).getName()) && !appInfoList.get(i).isSystem()) {
                    sb.append(appInfoList.get(i).getName());
                    sb.append(",");
                }
            }
            appInfo = sb.substring(0, sb.length() - 1);
        }
        try {
            json.put("uuid", uuid);
            json.put("clientType", "android");//系统型号
            json.put("channel", "pmobile");//渠道
            if (!StringUtils.isEmpty(cifseq)) {
                json.put("cifseq", cifseq);
            } else {
                json.put("cifseq", "lhyh999999");
            }
            if (!StringUtils.isEmpty(mobile)) {
                json.put("mobile", mobile);
            } else {
                json.put("mobile", "lhyh999999");
            }
            if (!StringUtils.isEmpty(model)) {
                json.put("model", model);
            }
            if (!StringUtils.isEmpty(sdkDes)) {
                json.put("sdkDes", sdkDes);
            }
            if (!StringUtils.isEmpty(sdkInt)) {
                json.put("sdkInt", sdkInt);
            }
            if (!StringUtils.isEmpty(sysVer)) {
                json.put("sysVer", sysVer);
            }
            if (!StringUtils.isEmpty(mac)) {
                json.put("mac", mac);
            }
            if (!StringUtils.isEmpty(vender)) {
                json.put("vender", vender);
            }
            if (!StringUtils.isEmpty(carrie)) {
                json.put("carrie", carrie);
            }
            if (!StringUtils.isEmpty(ip)) {
                json.put("ip", ip);
            }
            if (!StringUtils.isEmpty(lonlat)) {
                json.put("lonlat", lonlat);
            }
            if (!StringUtils.isEmpty(address)) {
                json.put("address", address);
            }
            if (!StringUtils.isEmpty(imei)) {
                json.put("imei", imei);
            }
            if (!StringUtils.isEmpty(imsi)) {
                json.put("imsi", imsi);
            }
            if (!StringUtils.isEmpty(androidId)) {
                json.put("androidId", androidId);
            }
            if (!StringUtils.isEmpty(serialNumber)) {
                json.put("serialNumber", serialNumber);
            }
            if (!StringUtils.isEmpty(appInfo)) {
                json.put("appInfo", appInfo);
            }
        } catch (JSONException e) {

        }
        String paramJson = json.toString();
        return paramJson;
    }

    /**
     * 获取设备UUID
     *
     * @param activity
     * @return
     */
    public String getUUID(Context activity) {
        UuidUtils.buidleID(activity).check();
        String uuid = UuidUtils.getUUID();//设备唯一标识
        uuid = uuid.replaceAll("-", "");
        return uuid;
    }
}