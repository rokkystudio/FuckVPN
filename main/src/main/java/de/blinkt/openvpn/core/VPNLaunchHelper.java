/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.core;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Vector;

import de.blinkt.openvpn.R;
import de.blinkt.openvpn.VpnProfile;

public class VPNLaunchHelper
{
    private static final String MINIPIEVPN = "pie_openvpn";

    public static String[] buildOpenvpnArgv(Context context) {
        String abi = Build.SUPPORTED_ABIS[0]; // напр. arm64-v8a
        String nativeDir = context.getApplicationInfo().nativeLibraryDir;

        // Отбрасываем последнюю папку (обычно arm64 или armeabi)
        File nativeRoot = new File(nativeDir).getParentFile(); // /data/app/xxx/lib
        File libFile = new File(nativeRoot, abi + "/libovpnexec.so");

        Log.e("OpenVPN", "🔍 ABI: " + abi);
        Log.e("OpenVPN", "📂 nativeLibraryDir: " + nativeDir);
        Log.e("OpenVPN", "📄 Итоговый путь к libovpnexec.so: " + libFile.getAbsolutePath());

        return new String[] {
            libFile.getAbsolutePath(),
            "--config",
            "stdin"
        };
    }

    public static void startOpenVpn(VpnProfile startprofile, Context context, String startReason, boolean replace_running_vpn) {
        Intent startVPN = startprofile.getStartServiceIntent(context, startReason, replace_running_vpn);
        if (startVPN != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                //noinspection NewApi
                context.startForegroundService(startVPN);
            else
                context.startService(startVPN);

        }
    }
}
