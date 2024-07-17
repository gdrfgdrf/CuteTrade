package io.github.gdrfgdrf.cutetrade;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * From net.minecraft.text.TranslatableTextContent (1.20)
 */
@SuppressWarnings("unused")
public class FixedTranslatableTextContent implements TextContent {
    public static final Object[] EMPTY_ARGUMENTS = new Object[0];
    private final String key;
    private final Object[] args;
    @Nullable
    private Language languageCache;
    private List<StringVisitable> translations = ImmutableList.of();

    public FixedTranslatableTextContent(String key, Object[] args) {
        this.key = key;
        this.args = args;
    }

    private void updateTranslations() {
        Language language = Language.getInstance();
        if (language != this.languageCache) {
            this.languageCache = language;
            String string =  language.get(this.key);

            try {
                ImmutableList.Builder<StringVisitable> builder = ImmutableList.builder();
                Objects.requireNonNull(builder);

                builder.add(StringVisitable.plain(string));

                this.translations = builder.build();
            } catch (TranslationException var4) {
                this.translations = ImmutableList.of(StringVisitable.plain(string));
            }

        }
    }

    @Override
    public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> visitor, Style style) {
        this.updateTranslations();
        Iterator<StringVisitable> var3 = this.translations.iterator();

        Optional<T> optional;
        do {
            if (!var3.hasNext()) {
                return Optional.empty();
            }

            StringVisitable stringVisitable = var3.next();
            optional = stringVisitable.visit(visitor, style);
        } while (optional.isEmpty());

        return optional;
    }

    @Override
    public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
        this.updateTranslations();
        Iterator<StringVisitable> var2 = this.translations.iterator();

        Optional<T> optional;
        do {
            if (!var2.hasNext()) {
                return Optional.empty();
            }

            StringVisitable stringVisitable = var2.next();
            optional = stringVisitable.visit(visitor);
        } while (optional.isEmpty());

        return optional;
    }

    @Override
    public MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
        Object[] objects = new Object[this.args.length];

        for (int i = 0; i < objects.length; ++i) {
            Object object = this.args[i];
            if (object instanceof Text) {
                objects[i] = Texts.parse(source, (Text) object, sender, depth);
            } else {
                objects[i] = object;
            }
        }

        return MutableText.of(new FixedTranslatableTextContent(this.key, objects));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else {
            if (o instanceof TranslatableTextContent translatableTextContent) {
                if (Objects.equals(this.key, translatableTextContent.getKey()) &&
                        Arrays.equals(this.args, translatableTextContent.getArgs())) {
                    return true;
                }
            }
            return o instanceof FixedTranslatableTextContent fixedTranslatableTextContent &&
                    this.key.equals(fixedTranslatableTextContent.key) &&
                    Arrays.equals(this.args, fixedTranslatableTextContent.args);
        }
    }

    @Override
    public int hashCode() {
        int i = Objects.hashCode(this.key);
        i = 31 * i + Arrays.hashCode(this.args);
        return i;
    }

    @Override
    public String toString() {
        return "fixedTranslation{key='" + this.key + "'" + ", args=" + Arrays.toString(this.args) + "}";
    }
}
