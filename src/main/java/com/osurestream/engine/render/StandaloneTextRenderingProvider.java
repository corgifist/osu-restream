package com.osurestream.engine.render;

import com.osurestream.engine.primitives.Renderable;
import com.osurestream.engine.primitives.TextPrimitive;
import com.osurestream.engine.util.TextRenderingProvider;
import com.osurestream.engine.util.TextRepresentation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class StandaloneTextRenderingProvider implements TextRenderingProvider {
    @Override
    public BufferedImage render(TextPrimitive primitive) {
        try {
            TextRepresentation representation = new TextRepresentation(
                    primitive.getFont(),
                    primitive.getText(),
                    primitive.getSize()
            );
            if (TextPrimitive.TEXT_CACHE.containsKey(representation)) {
               return TextPrimitive.TEXT_CACHE.get(representation);
            }
            String[] lines = primitive.getText().split("\n");
            int lineCount = Math.max(1, lines.length);
            int maxLineLength = 0;
            int characterSize = primitive.getSize() * 1;
            for (String line : lines) {
                if (line.length() > maxLineLength)
                    maxLineLength = line.length();
            }

            BufferedImage texture = new BufferedImage(Math.max(1, (int) (characterSize * 0.8f * maxLineLength)),
                    Math.max(1, (int) (lineCount * characterSize * 2)), BufferedImage.TYPE_4BYTE_ABGR_PRE);
            Graphics2D g2d = (Graphics2D) texture.createGraphics();

            FileInputStream fontStream = new FileInputStream(new File("assets/ttf/" + primitive.getFont()));
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont((float) primitive.getSize());

            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            g2d.setColor(new Color(0, 0, 0, 0));
            g2d.fillRect(0, 0, texture.getWidth(), texture.getHeight());
            g2d.setColor(Color.WHITE);
            g2d.setFont(font);
            for (int i = 0; i < lines.length; i++) {
                int realIndex = i + 1;
                String line = lines[i];
                g2d.drawString(line, 0, characterSize * realIndex);
            }

            TextPrimitive.TEXT_CACHE.put(representation, texture);
            return texture;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (FontFormatException e) {
            throw new RuntimeException(e);
        }

    }
}
