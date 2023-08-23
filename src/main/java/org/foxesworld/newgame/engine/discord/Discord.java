package org.foxesworld.newgame.engine.discord;

import club.minnced.discord.rpc.*;

public class Discord implements DiscordInterface {

    private static DiscordRPC lib;
    private DiscordRichPresence presence;
    private String applicationId = "712667904956432456";

    public Discord(String details, String state) {
        lib = DiscordRPC.INSTANCE;
        String steamId = "";
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = (user) -> {
            //Instance.setUserLogin(user.username);
            //System.out.println(user.discriminator);
        };
        lib.Discord_Initialize(applicationId, handlers, true, steamId);
        presence = new DiscordRichPresence();
        presence.startTimestamp = System.currentTimeMillis() / 1000;
        presence.details   = details;
        presence.state     = state;
        lib.Discord_UpdatePresence(presence);
    }

    public void discordRpcStart(String icon) {
        presence.largeImageKey = icon;
        lib.Discord_UpdatePresence(presence);
        Thread t = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                lib.Discord_RunCallbacks();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    lib.Discord_Shutdown();
                    break;
                }
            }
        }, "RPC-Callback-Handler");
        t.start();
    }

    public static DiscordRPC getLib() {
        return lib;
    }
}