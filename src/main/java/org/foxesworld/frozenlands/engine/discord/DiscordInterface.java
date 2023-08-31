package org.foxesworld.frozenlands.engine.discord;

import club.minnced.discord.rpc.DiscordRPC;

public interface DiscordInterface {
    DiscordRPC getDiscordLib();
    void discordRpcStart(String icon);
}
