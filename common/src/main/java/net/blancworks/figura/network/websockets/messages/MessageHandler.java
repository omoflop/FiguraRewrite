package net.blancworks.figura.network.websockets.messages;

import com.google.common.io.LittleEndianDataInputStream;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public abstract class MessageHandler {
    public void handleMessage(LittleEndianDataInputStream stream) throws Exception{
        
    }

    public abstract String getProtocolName();

    public String readString(LittleEndianDataInputStream stream) throws IOException {
        int length = stream.readInt();
        byte[] strData = new byte[length];
        stream.read(strData);
        
        return new String(strData, StandardCharsets.UTF_8);
    }
    
    public UUID readUUID(LittleEndianDataInputStream stream) throws IOException {
        return UUID.fromString(readString(stream));
    }
}
