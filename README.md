# PlayerHeads-statistics-simulator
_Warning: This is not a plugin that should be used on a live server with players. This is a testing-plugin meant to validate droprates on a private or local server and can have severe performance costs and trigger watchdogs/crash-detection.  If you still want to use it, you need to configure the server properly to disable watchdogs/crash-detection._

General function and features:
 * Logging for HeadRoll event details at both high and low plugin priorities (droprate,droproll, killer, target, success state)
 * Logging for HeadDrop event details (behedee, drops)
 * When an entity is killed by the player, creates 1 million simulated copies of the death event and records rates,rolls,successes for the simulation in order to validate overall success rate (varies with RNG). (_very performance heavy_).
 * Prevents items from dropping from simulated events.

Note: simulated death events are presented as new bukkit death events and will be handled by PlayerHeads exactly like a regular death (since PH cannot tell the difference) - so we can use the resulting success or failures to measure and validate PlayerHeads behavior.

Logging at different priorities was done to highlight rate changes by outside plugins (like TrophyLuckModifier).
