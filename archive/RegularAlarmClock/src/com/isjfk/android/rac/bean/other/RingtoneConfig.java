/**
 * (C) Copyright InfiniteSpace Studio, 2011-2012. All rights reserved.
 */
package com.isjfk.android.rac.bean.other;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.isjfk.android.rac.common.Log;
import com.isjfk.android.rac.common.RACContext;
import com.isjfk.android.util.Base64Util;
import com.isjfk.android.util.JavaUtil;

/**
 * 铃声配置。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2012-3-19
 */
public class RingtoneConfig implements Parcelable {

    public static final Integer TYPE_DEFAULT = 0;
    public static final Integer TYPE_PREFERENCE = 1;
    public static final Integer TYPE_RINGTONE = 2;
    public static final Integer TYPE_MUSIC = 3;

    /** 铃声类型 */
    private int type = TYPE_DEFAULT;

    /** 铃声文件路径。key: type; value: 路径 */
    private Map<Integer, Uri> ringtones;

    /** 响铃音量 */
    private int volume = RACContext.getMaxVolume();

    /** 淡入时间，单位“秒” */
    private int fadeInTime = RACContext.getDefaultFadeInTime();

    public RingtoneConfig() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @SuppressLint("UseSparseArrays")
    public Map<Integer, Uri> getRingtones() {
        if (ringtones == null) {
            ringtones = new HashMap<Integer, Uri>();
        }
        return ringtones;
    }

    public void setRingtones(Map<Integer, Uri> ringtones) {
        this.ringtones = ringtones;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getFadeInTime() {
        return fadeInTime;
    }

    public void setFadeInTime(int fadeInTime) {
        this.fadeInTime = fadeInTime;
    }

    public boolean isDefault() {
        return TYPE_DEFAULT.equals(type);
    }

    public boolean isPreference() {
        return TYPE_PREFERENCE.equals(type);
    }

    public boolean isRingtone() {
        return TYPE_RINGTONE.equals(type);
    }

    public boolean isMusic() {
        return TYPE_MUSIC.equals(type);
    }

    public Uri getRingtone() {
        return getRingtone(type);
    }

    public void setRingtone(Uri ringtoneUri) {
        setRingtone(type, ringtoneUri);
    }

    public Uri getRingtone(int type) {
        return getRingtones().get(type);
    }

    public void setRingtone(int type, Uri ringtone) {
        if (ringtone != null) {
            getRingtones().put(type, ringtone);
        } else {
            getRingtones().remove(type);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(type);
        dest.writeValue(ringtones);
        dest.writeValue(volume);
        dest.writeValue(fadeInTime);
    }

    public static final Parcelable.Creator<RingtoneConfig> CREATOR = new Parcelable.Creator<RingtoneConfig>() {
        @SuppressWarnings("unchecked")
        public RingtoneConfig createFromParcel(Parcel in) {
            RingtoneConfig config = new RingtoneConfig();
            config.type = (Integer) in.readValue(null);
            config.ringtones = (Map<Integer, Uri>) in.readValue(null);
            config.volume = (Integer) in.readValue(null);
            config.fadeInTime = (Integer) in.readValue(null);
            return config;
        }
        public RingtoneConfig[] newArray(int size) {
            return new RingtoneConfig[size];
        }
    };

    /**
     * 将铃声配置编码为字符串。
     *
     * @param config 铃声配置
     * @return 字符串表示的铃声配置，如果config为null则返回null
     */
    public static String encode(RingtoneConfig config) {
        if (config == null) {
            return null;
        }

        StringBuilder buf = new StringBuilder();
        buf.append(config.getType()).append("|").append(config.getVolume()).append("|");

        Uri ringtoneUri = config.getRingtone(RingtoneConfig.TYPE_RINGTONE);
        if (ringtoneUri != null) {
            buf.append(RingtoneConfig.TYPE_RINGTONE).append(":").append(
                    Base64Util.encode(ringtoneUri.toString()));
        }

        Uri musicUri = config.getRingtone(RingtoneConfig.TYPE_MUSIC);
        if (musicUri != null) {
            if (ringtoneUri != null) {
                buf.append(",");
            }
            buf.append(RingtoneConfig.TYPE_MUSIC).append(":").append(
                    Base64Util.encode(musicUri.toString()));
        }

        buf.append("|").append(config.getFadeInTime());

        return buf.toString();
    }

    /**
     * 将字符串解码为铃声配置。
     *
     * @param str 字符串
     * @return 铃声配置，如果str为空或解码失败则返回null
     */
    @SuppressLint("UseSparseArrays")
    public static RingtoneConfig decode(String str) {
        if (JavaUtil.isEmpty(str)) {
            return null;
        }

        String[] parts = str.split("\\|", -1);
        if (parts.length < 3) {
            Log.e("error decode RingtoneConfig: " + str);
            Log.e("error detail: expect parts of size >=3 but got " + parts.length);
            return null;
        }

        Integer type = null;
        try {
            type = Integer.valueOf(parts[0]);
        } catch (Exception e) {
            Log.e("error decode RingtoneConfig: " + str);
            Log.e("error detail: illegel type: " + parts[0], e);
            return null;
        }

        Integer volume = null;
        try {
            volume = Integer.valueOf(parts[1]);
        } catch (Exception e) {
            Log.e("error decode RingtoneConfig: " + str);
            Log.e("error detail: illegel volume: " + parts[1], e);
            return null;
        }

        Map<Integer, Uri> ringtones = new HashMap<Integer, Uri>();
        if (JavaUtil.isNotEmpty(parts[2])) {
            String[] ringtoneMapParts = parts[2].split(",", -1);
            for (String ringtoneMapPart : ringtoneMapParts) {
                String[] ringtoneParts = ringtoneMapPart.split(":", -1);
                if (ringtoneParts.length != 2) {
                    Log.e("error decode RingtoneConfig: " + str);
                    Log.e("error detail: illegal ringtoneMapPart: " + ringtoneMapPart);
                    Log.e("error detail: expect ringtoneParts of size 2 but got " + ringtoneParts.length);
                    return null;
                }

                Integer ringtoneType = null;
                try {
                    ringtoneType = Integer.valueOf(ringtoneParts[0]);
                } catch (Exception e) {
                    Log.e("error decode RingtoneConfig: " + str);
                    Log.e("error detail: illegal ringtonePart: " + ringtoneParts[0]);
                    Log.e("error detail: illegel ringtoneType: " + ringtoneParts[0], e);
                    return null;
                }

                String ringtone = Base64Util.decode(ringtoneParts[1]);

                if (JavaUtil.isNotEmpty(ringtone)) {
                    ringtones.put(ringtoneType, Uri.parse(ringtone));
                }
            }
        }

        RingtoneConfig config = new RingtoneConfig();
        config.setType(type);
        config.setVolume(volume);
        config.setRingtones(ringtones);

        if (parts.length > 3) {
            try {
                config.setFadeInTime(Integer.valueOf(parts[3]));
            } catch (Exception e) {
                Log.e("error decode RingtoneConfig: " + str);
                Log.e("error detail: illegel fadeInTime: " + parts[3], e);
            }
        }

        return config;
    }

}
