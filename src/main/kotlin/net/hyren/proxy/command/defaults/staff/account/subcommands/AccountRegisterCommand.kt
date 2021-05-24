package net.hyren.proxy.command.defaults.staff.account.subcommands

import net.hyren.core.bungee.command.CustomCommand
import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.commands.argument.Argument
import net.hyren.core.shared.users.data.User
import net.hyren.core.shared.users.passwords.storage.dto.CreateUserPasswordDTO
import net.hyren.core.shared.users.passwords.storage.dto.FetchUserPasswordByUserIdDTO
import net.hyren.core.shared.users.storage.dto.CreateUserDTO
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import java.util.*

/**
 * @author Gutyerrez
 */
class AccountRegisterCommand : CustomCommand("registrar") {

	override fun getDescription() = "Registrar um usuário."

	override fun getArguments() = listOf(
		Argument("usuário"),
		Argument("senha")
	)

	override fun onCommand(
		commandSender: CommandSender,
		user: User?,
		args: Array<out String>
	): Boolean {
		val targetUser = CoreProvider.Cache.Local.USERS.provide().fetchByName(args[0])
			?: CoreProvider.Repositories.PostgreSQL.USERS_REPOSITORY.provide().create(
				CreateUserDTO(
					UUID.nameUUIDFromBytes(
						"OfflinePlayer:${args[0]}".toByteArray(Charsets.UTF_8)
					),
					args[0],
					"undefined"
				)
			)

		if (CoreProvider.Repositories.PostgreSQL.USERS_PASSWORDS_REPOSITORY.provide().fetchByUserId(
				FetchUserPasswordByUserIdDTO(targetUser.getUniqueId())
			).stream().anyMatch { it.enabled }
		) {
			commandSender.sendMessage(
				TextComponent("§cEste usuário já está registrado.")
			)
			return false
		}

		CoreProvider.Repositories.PostgreSQL.USERS_PASSWORDS_REPOSITORY.provide().create(
			CreateUserPasswordDTO(
				targetUser.id,
				args[1]
			)
		)

		commandSender.sendMessage(TextComponent("§eO usuário ${args[0]} foi registrado."))
		return true
	}

}