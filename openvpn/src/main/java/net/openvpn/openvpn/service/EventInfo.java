package net.openvpn.openvpn.service;

import net.openvpn.openvpn.NotificationActionType;

public class EventInfo
{
    public int flags;
    public int icon_res_id;
    public NotificationActionType notif_action_type;
    public int priority;
    public int progress;
    public int res_id;

    public EventInfo(int resId, int iconResId, int progressValue, int priorityValue, int flagsValue) {
        res_id = resId;
        icon_res_id = iconResId;
        progress = progressValue;
        priority = priorityValue;
        flags = flagsValue;
        notif_action_type = NotificationActionType.NONE;
    }

    public EventInfo(int resId, int iconResId, int progressValue, int priorityValue, int flagsValue, NotificationActionType actionType) {
        res_id = resId;
        icon_res_id = iconResId;
        progress = progressValue;
        priority = priorityValue;
        flags = flagsValue;
        notif_action_type = actionType;
    }
}
