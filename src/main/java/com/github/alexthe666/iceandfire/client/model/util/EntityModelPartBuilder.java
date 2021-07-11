package com.github.alexthe666.iceandfire.client.model.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.model.ModelRenderer;

public class EntityModelPartBuilder {
    private static final Map<String, Iterable<ModelRenderer>> PART_MAP = new HashMap<>();
    private static final Map<String, Iterable<AdvancedModelBox>> ALL_PART_MAP = new HashMap<>();

    private static Iterable<ModelRenderer> getPartsForRenderFromClass(Class clazz, String identifier) {
        Iterable<ModelRenderer> boxes = null;
        if (PART_MAP.get(identifier) == null) {
            List<ModelRenderer> rendererList = new ArrayList<>();
            try {
                for (Field f : clazz.getDeclaredFields()) {
                    Object obj = f.get(null);
                    if (obj instanceof ModelRenderer) {


                        rendererList.add((ModelRenderer) obj);
                    }
                }
            } catch (Exception e0) {
            }
            boxes = PART_MAP.put(identifier, ImmutableList.copyOf(rendererList));
        } else {
            boxes = PART_MAP.get(identifier);
        }

        return boxes;
    }

    public static Iterable<AdvancedModelBox> getAllPartsFromClass(Class clazz, String identifier) {
        Iterable<AdvancedModelBox> boxes = null;
        if (ALL_PART_MAP.get(identifier) == null) {
            List<AdvancedModelBox> rendererList = new ArrayList<>();
            try {
                for (Field f : clazz.getDeclaredFields()) {
                    Object obj = f.get(null);
                    if (obj instanceof AdvancedModelBox) {
                        rendererList.add((AdvancedModelBox) obj);
                    }
                }
            } catch (Exception e0) {
            }
            ALL_PART_MAP.put(identifier, ImmutableList.copyOf(rendererList));
        }
        boxes = ALL_PART_MAP.get(identifier);

        return boxes;
    }


}
