package net.blancworks.figura.network.websockets.messages.pings;

import com.google.common.io.LittleEndianDataOutputStream;
import net.blancworks.figura.network.websockets.messages.MessageSender;

import java.io.IOException;


public class PingMessageSender extends MessageSender {

    //public Queue<CustomScript.LuaPing> pingSet = new LinkedList<>();
    
    /**public PingMessageSender(Queue<CustomScript.LuaPing> pings){
        pingSet.addAll(pings);
        pings.clear();
    }**/

    @Override
    protected void write(LittleEndianDataOutputStream stream) throws IOException {
        super.write(stream);

        /**ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        LittleEndianDataOutputStream outWriter = new LittleEndianDataOutputStream(outStream);

        int setSize = pingSet.size();


        outWriter.writeShort(setSize);
        
        //System.out.println("Wrote " + pingSet.size() + " pings");
        
        for(int i = 0; i < setSize; i++){
            CustomScript.LuaPing p = pingSet.poll();
            outWriter.writeShort(p.functionID);
            try {
                LuaNetworkReadWriter.writeLuaValue(p.args, outWriter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        byte[] outData = outStream.toByteArray();

        stream.writeInt(outData.length);
        stream.write(outData);**/
    }

    @Override
    public String getProtocolName() {
        return "figura_v1:ping";
    }
}
