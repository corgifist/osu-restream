package com.osurestream.engine.util;

import com.osurestream.engine.primitives.TextPrimitive;

import java.awt.image.BufferedImage;

public interface TextRenderingProvider {
    BufferedImage render(TextPrimitive primitive);

}
