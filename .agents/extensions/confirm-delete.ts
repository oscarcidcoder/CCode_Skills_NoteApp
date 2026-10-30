/**
 * Confirm Delete Extension
 *
 * Asks for confirmation before running any bash command that contains "rm".
 * If the user declines (or there is no UI to ask), the call is blocked.
 */

import type { ExtensionAPI } from "@earendil-works/pi-coding-agent";

/** Matches "rm" as a standalone word: rm, rm -rf, /bin/rm, xargs rm, `rm`, etc. */
const RM_PATTERN = /\brm\b/i;

export default function (pi: ExtensionAPI) {
	pi.on("tool_call", async (event, ctx) => {
		if (event.toolName !== "bash") return undefined;

		const command = event.input?.command;

		// Fail closed: if we cannot inspect the command, do not run it.
		if (typeof command !== "string") {
			return { block: true, reason: "Bash command could not be inspected for 'rm' (fail-closed)" };
		}

		if (!RM_PATTERN.test(command)) return undefined;

		// No way to ask for confirmation (print / json mode) => block.
		if (!ctx.hasUI) {
			return { block: true, reason: "Command contains 'rm' and no UI is available for confirmation" };
		}

		const approved = await ctx.ui.confirm(
			"⚠️ Command contains 'rm'",
			`Allow this command to run?\n\n${command}`,
		);

		if (!approved) {
			ctx.ui.notify("Command blocked: rm not approved", "info");
			return { block: true, reason: "Blocked by user: command contains 'rm'" };
		}

		return undefined;
	});
}
