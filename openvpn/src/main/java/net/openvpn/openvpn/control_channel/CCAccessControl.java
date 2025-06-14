package net.openvpn.openvpn.control_channel;

import com.openvpn.openvpn.dpc.dpc_api;
import net.openvpn.openvpn.ClientAPI_Config;
import net.openvpn.openvpn.OpenVPNClientHelperWrapper;

public class CCAccessControl
{
    public static boolean processConfig(ClientAPI_Config config) {
        // Очищаем пользовательские протоколы перед проверкой
        config.setAppCustomProtocols("");

        // Получаем CA и проверяем разрешение
        String vpnCa = OpenVPNClientHelperWrapper.eval_config(config).getVpnCa();
        boolean isAllowed = dpc_api.isCAAllowed(vpnCa);

        // Устанавливаем специальные протоколы, если разрешено
        if (isAllowed) {
            config.setAppCustomProtocols("cck1:dpc1");
        }

        return isAllowed;
    }
}