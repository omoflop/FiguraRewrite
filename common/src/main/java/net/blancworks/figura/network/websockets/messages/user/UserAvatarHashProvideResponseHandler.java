package net.blancworks.figura.network.websockets.messages.user;

import com.google.common.io.LittleEndianDataInputStream;
import net.blancworks.figura.network.websockets.messages.MessageHandler;

import java.util.UUID;

public class UserAvatarHashProvideResponseHandler extends MessageHandler {

    @Override
    public void handleMessage(LittleEndianDataInputStream stream) throws Exception {
        super.handleMessage(stream);
        
        UUID id = readUUID(stream);
        String hash = readString(stream);
        
        //Handle?
        /**PlayerData pDat = PlayerDataManager.getDataForPlayer(id);
        
        if(!pDat.lastHash.equals(hash)){
            pDat.isInvalidated = true;   
        }**/
    }

    @Override
    public String getProtocolName() {
        return "figura_v1:user_avatar_hash_provide";
    }
}
