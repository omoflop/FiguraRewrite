package net.blancworks.figura.network.websockets.messages.user;

import com.google.common.io.LittleEndianDataOutputStream;
import net.blancworks.figura.network.websockets.messages.MessageSender;

import java.io.IOException;
import java.util.UUID;

public class UserGetCurrentAvatarMessageSender extends MessageSender {
    public UUID id;
    
    public UserGetCurrentAvatarMessageSender(UUID id) {
        this.id = id;
    }

    @Override
    public String getProtocolName() {
        return "figura_v1:user_get_current_avatar";
    }

    @Override
    protected void write(LittleEndianDataOutputStream stream) throws IOException {
        super.write(stream);
        
        writeUUID(id, stream);
    }
}
