package net.blancworks.figura.network.websockets.messages.pubsub;

import com.google.common.io.LittleEndianDataInputStream;

public class ChannelAvatarUpdateHandler extends ChannelMessageHandler {

    @Override
    public void handleMessage(LittleEndianDataInputStream stream) throws Exception {
        super.handleMessage(stream);

        //PlayerDataManager.getDataForPlayer(senderID).isInvalidated = true;
        
        System.out.println("AVATAR UPDATE RECEIVED");
    }

    @Override
    public String getProtocolName() {
        return "figura_v1:channel_avatar_update";
    }
}
