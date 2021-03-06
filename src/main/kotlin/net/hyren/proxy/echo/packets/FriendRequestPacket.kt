package net.hyren.proxy.echo.packets

import net.hyren.core.shared.echo.api.buffer.EchoBufferInput
import net.hyren.core.shared.echo.api.buffer.EchoBufferOutput
import net.hyren.core.shared.echo.api.packets.EchoPacket
import net.hyren.core.shared.users.storage.table.UsersTable
import net.md_5.bungee.api.chat.BaseComponent
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

/**
 * @author Gutyerrez
 */
class FriendRequestPacket : EchoPacket() {

    var userId: EntityID<UUID>? = null
    var targetId: EntityID<UUID>? = null
    var message: Array<BaseComponent>? = null

    override fun write(buffer: EchoBufferOutput) {
        buffer.writeEntityID(this.userId)
        buffer.writeEntityID(this.targetId)
        buffer.writeBaseComponent(message)
    }

    override fun read(buffer: EchoBufferInput) {
        this.userId = buffer.readEntityID(UsersTable)
        this.targetId = buffer.readEntityID(UsersTable)
        this.message = buffer.readBaseComponent()
    }

}