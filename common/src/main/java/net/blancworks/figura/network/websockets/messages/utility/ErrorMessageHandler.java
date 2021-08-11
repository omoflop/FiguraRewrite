package net.blancworks.figura.network.websockets.messages.utility;

import com.google.common.io.LittleEndianDataInputStream;
import net.blancworks.figura.network.websockets.messages.MessageHandler;

public class ErrorMessageHandler extends MessageHandler {

    @Override
    public void handleMessage(LittleEndianDataInputStream stream) throws Exception {
        super.handleMessage(stream);
    }

    @Override
    public String getProtocolName() {
        return "figura_v1:error";
    }
}
