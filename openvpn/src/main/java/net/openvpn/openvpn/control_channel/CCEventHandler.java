package net.openvpn.openvpn.control_channel;

import android.util.Log;
import com.openvpn.openvpn.dpc.JsonValue;
import com.openvpn.openvpn.dpc.Request;
import com.openvpn.openvpn.dpc.Response;
import com.openvpn.openvpn.dpc.dpc_api;
import net.openvpn.openvpn.ClientAPI_AppCustomControlMessageEvent;
import net.openvpn.openvpn.MessageQueue;

public class CCEventHandler
{
    private static final String TAG = "CCEventHandler";
    private boolean enabled = false;
    private final MessageQueue messageQueue;
    private final ICCSender sender;

    public CCEventHandler(MessageQueue messageQueue, ICCSender sender) {
        this.messageQueue = messageQueue;
        this.sender = sender;
    }

    private void handleDPC1(String payload) {
        log("DPC Request RECEIVED", payload);
        Response response = new Response();
        JsonValue root = dpc_api.parseString(payload, response);

        if (response.hasErrors()) {
            log("ERROR: Failed to process DPC request", "Failed to parse JSON payload: " + response.toJSONString());
            return;
        }

        if (root.isMember("dpc_status")) {
            log("DPC Status RECEIVED", root.toStyledString());
            return;
        }

        if (!root.isMember("dpc_request")) {
            log("Unexpected payload type", root.toStyledString());
            return;
        }

        dpc_api.setCommonResponseFields(root, response);
        if (!dpc_api.validateJSON(root, response)) {
            log("ERROR: Failed to validate DPC request JSON", response.toJSONString());
            send_dpc1(response);
            return;
        }

        Request req = Request.fromJSON(root);
        if (!dpc_api.validateRequest(req, response)) {
            log("ERROR: Failed to validate DPC request structure", response.toJSONString());
            send_dpc1(response);
            return;
        }

        dpc_api.process(req, response);

        if (response.hasErrors()) {
            log("ERROR: DPC processing returned errors", response.toJSONString());
        } else {
            log("DPC Response SENT", response.toJSONString());
        }

        send_dpc1(response);
    }

    private void log(String message) {
        log(message, null);
    }

    private void log(String message, String details) {
        Log.i(TAG, message);
        messageQueue.post_log(message);
        if (details != null && !details.isEmpty()) {
            messageQueue.post_log(details);
        }
    }

    private void send(String protocol, String payload) {
        sender.send_app_control_channel_msg(protocol, payload);
    }

    private void send(String protocol, Response response) {
        send(protocol, response.toJSONString());
    }

    private void send_dpc1(String payload) {
        send("dpc1", payload);
    }

    private void send_dpc1(Response response) {
        send_dpc1(response.toJSONString());
    }

    public void handle(ClientAPI_AppCustomControlMessageEvent event) {
        if (!enabled) return;

        String protocol = event.getProtocol();
        String payload = event.getPayload();

        if (!dpc_api.isProtocolSupported(protocol)) {
            log("ERROR: UNKNOWN CC PROTOCOL \"" + protocol + "\"");
            return;
        }

        if ("dpc1".equals(protocol)) {
            handleDPC1(payload);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
