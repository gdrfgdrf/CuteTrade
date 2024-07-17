package io.github.gdrfgdrf.cutetrade.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.util.Language;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * From <a href="https://github.com/FabricMC/fabric/pull/2668/files/4999c5beb477252baa2966245b6e4dea6dbc5f99">...</a>
 * @author gdrfgdrf
 */
@SuppressWarnings("all")
@Mixin(Language.class)
public class LanguageMixin {
    @Shadow
    @Final
    private static Logger LOGGER;

    @Redirect(method = "create", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;build()Lcom/google/common/collect/ImmutableMap;", remap = false))
    private static ImmutableMap<String, String> create(ImmutableMap.Builder<String, String> cir) {
        Map<String, String> map = new HashMap<>(cir.buildOrThrow());

        ModContainer cutetrade = FabricLoader.getInstance().getModContainer("cutetrade").get();
        Optional<Path> path = cutetrade.findPath("assets/cutetrade/lang/en_us.json")
                .filter(Files::isRegularFile);
        if (path.isEmpty()) {
            return ImmutableMap.copyOf(map);
        }

        loadFromPath(path.get(), map::put);

        return ImmutableMap.copyOf(map);
    }

    private static void loadFromPath(Path path, BiConsumer<String, String> entryConsumer) {
        try (InputStream stream = Files.newInputStream(path)) {
            LOGGER.debug("Loading translations from {}", path);
            load(stream, entryConsumer);
        } catch (JsonParseException | IOException e) {
            LOGGER.error("Couldn't read strings from {}", path, e);
        }
    }

    @Shadow
    public static void load(InputStream inputStream, BiConsumer<String, String> entryConsumer) {
    }
}
