package com.thedevjade.typingwithlux.dialogue.entries

import com.typewritermc.core.entries.get
import com.typewritermc.core.interaction.InteractionContext
import com.typewritermc.engine.paper.entry.dialogue.DialogueMessenger
import com.typewritermc.engine.paper.entry.dialogue.MessengerState
import com.typewritermc.engine.paper.entry.dialogue.TickContext
import com.typewritermc.engine.paper.entry.entity.SimpleEntityDefinition
import com.typewritermc.engine.paper.extensions.placeholderapi.parsePlaceholders
import com.typewritermc.engine.paper.utils.asMini
import io.papermc.paper.command.brigadier.argument.ArgumentTypes.player
import io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay.override
import jdk.internal.org.commonmark.parser.block.BlockContinue.finished
import me.clip.placeholderapi.PlaceholderAPI
import org.aselstudios.luxdialoguesapi.Builders.Dialogue
import org.aselstudios.luxdialoguesapi.Builders.Page
import org.aselstudios.luxdialoguesapi.LuxDialoguesAPI
import org.bukkit.entity.Player
import kotlin.reflect.typeOf

class RegularLuxDialogueHandler(player: Player, context: InteractionContext, entry: RegularLuxDialogueEntry) :
    DialogueMessenger<RegularLuxDialogueEntry>(player, context, entry) {

        var dialogue: Dialogue? = null;


    override fun init() {
        super.init()

        val npc = entry.speaker.get() as SimpleEntityDefinition
        val data = npc.data.get().first { it.type() == typeOf<LuxNpcData>() } as LuxNpcData

        val totalTime: Int = (entry.duration.get(player, context).toMillis() * 20 / 1000).toInt()

        val chars = entry.text.length.coerceAtLeast(1)
        val time = (totalTime / chars).toInt().coerceAtLeast(1)
        val dialogueBuilder: Dialogue.Builder  = Dialogue.Builder()
            .setDialogueID(entry.id)
            .setRange(-1.0)
            .setDialogueSpeed(time)
            .setTypingSound(data.soundName)
            .setTypingSoundPitch(1.0)
            .setTypingSoundVolume(1.0)
            .setSelectionSound("luxdialogues:luxdialogues.sounds.selection")
            .setAnswerNumbers(false)
            .setArrowImage("hand", "#cdff29", -7)
            .setDialogueBackgroundImage("dialogue-background", "#f8ffe0", 0)
            .setAnswerBackgroundImage("answer-background", "#f8ffe0", 90)
            .setDialogueText("#4f4a3e", 10)
            .setAnswerText("#4f4a3e", 13, "#4f4a3e")
            .setCharacterImage(data.characterName, -16)
            .setCharacterNameText(data.characterName, "#4f4a3e", 20)
            .setNameStartImage("name-start")
            .setNameMidImage("name-mid")
            .setNameEndImage("name-end")
            .setNameImageColor("#f8ffe0")
            .setFogImage("fog", "#000000")
            .setPreventExit(false);
        val page: Page = Page.Builder()
            .addLine(entry.text.parsePlaceholders(player))
            .build();

        dialogueBuilder.addPage(page)

        dialogue = dialogueBuilder.build()

        LuxDialoguesAPI.getProvider().sendDialogue(player, dialogue);

    }

    override fun tick(context: TickContext) {
        super.tick(context)
        if (state != MessengerState.RUNNING) return

        val tempPlaceholder = PlaceholderAPI.setPlaceholders(player, "%luxdialogues_dialogue%").lowercase()
        if (tempPlaceholder != "none" && tempPlaceholder != "Player Offline") return

        state = MessengerState.FINISHED
    }
}