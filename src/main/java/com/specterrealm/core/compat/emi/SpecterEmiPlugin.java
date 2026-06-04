package com.specterrealm.core.compat.emi;

import com.specterrealm.core.init.ItemInit;
import com.specterrealm.core.init.SpecterMaterial;
import com.specterrealm.core.init.SpecterPart;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * EMI recipe viewer integration.
 *
 * Hides items that are config-disabled so they do not appear in the EMI
 * item list. Safe when EMI is absent — @EmiEntrypoint is only scanned by
 * EMI itself, so this class is never loaded if EMI is not present.
 */
@EmiEntrypoint
public class SpecterEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        hideDisabledParts(registry);
        hideDisabledIngots(registry);
    }

    private void hideDisabledParts(EmiRegistry registry) {
        var parts = ItemInit.getParts();

        for (SpecterPart part : SpecterPart.values()) {
            if (part.materialPrefix) {
                for (SpecterMaterial mat : part.materials) {
                    if (!part.isEnabled(mat)) {
                        DeferredItem<?> di = parts.get(part.itemId(mat));
                        if (di != null) registry.removeEmi(EmiStack.of(di.get()));
                    }
                }
            } else {
                if (!part.isEnabled()) {
                    DeferredItem<?> di = parts.get(part.itemId());
                    if (di != null) registry.removeEmi(EmiStack.of(di.get()));
                }
            }
        }
    }

    private void hideDisabledIngots(EmiRegistry registry) {
        // Base ingots are hidden if the base_materials category is disabled
        // or if the specific material is disabled.
        // Material name is derived from "{material}_ingot" key.
        if (!com.specterrealm.core.config.SpecterRealmConfig.CATEGORY_BASE_MATERIALS.get()) {
            ItemInit.getBaseIngots().values().forEach(di ->
                registry.removeEmi(EmiStack.of(di.get())));
            return;
        }
        for (var entry : ItemInit.getBaseIngots().entrySet()) {
            String mat = entry.getKey().replace("_ingot", "");
            SpecterMaterial sm = materialById(mat);
            if (sm != null && !sm.isEnabled()) {
                registry.removeEmi(EmiStack.of(entry.getValue().get()));
            }
        }
    }

    private static SpecterMaterial materialById(String id) {
        for (SpecterMaterial m : SpecterMaterial.values()) {
            if (m.id.equals(id)) return m;
        }
        return null;
    }
}
