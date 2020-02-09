package net.szum123321.elytra_swap.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class SwapEnableArgumentType implements ArgumentType<Integer> {
    private static final Collection<String> EXAMPLES = Arrays.asList("enable", "disable");

    private SwapEnableArgumentType(){
    }

    public static SwapEnableArgumentType Int(){
        return new SwapEnableArgumentType();
    }

    public static int getInteger(final CommandContext<?> context, final String name) {
        return context.getArgument(name, Integer.class);
    }


    @Override
    public Integer parse(StringReader reader) throws CommandSyntaxException {
        String val = reader.readString();

        switch(val){
            case "enable":
                return 1;

            case "disable":
                return 2;

            default:
                return 0;
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if("enable".startsWith(builder.getRemaining().toLowerCase()))
            builder.suggest("enable");

        if("disable".startsWith(builder.getRemaining().toLowerCase()))
            builder.suggest("disable");

        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
