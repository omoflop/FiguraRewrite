package net.blancworks.figura.network.websockets.messages;

import com.google.common.io.LittleEndianDataInputStream;

public class DebugMessageHandler extends MessageHandler {

    @Override
    public void handleMessage(LittleEndianDataInputStream stream) throws Exception {
        super.handleMessage(stream);

        try {
            //FiguraMod.LOGGER.info(stream.readInt());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getProtocolName() {
        return "figura_v1:debug";
    }
}
