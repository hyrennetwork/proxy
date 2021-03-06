package net.hyren.proxy.echo.packets

import net.hyren.core.shared.echo.api.buffer.EchoBufferInput
import net.hyren.core.shared.echo.api.buffer.EchoBufferOutput
import net.hyren.core.shared.echo.api.packets.EchoPacket
import java.util.*

/**
 * @author Gutyerrez
 */
class TellPacket : EchoPacket() {

    var senderId: UUID? = null
    var receiverId: UUID? = null
    var message: String? = null

    override fun write(buffer: EchoBufferOutput) {
        buffer.writeUUID(this.senderId)
        buffer.writeUUID(this.receiverId)
        buffer.writeString(this.message)
    }

    override fun read(buffer: EchoBufferInput) {
        this.senderId = buffer.readUUID()
        this.receiverId = buffer.readUUID()
        this.message = buffer.readString()
    }

}