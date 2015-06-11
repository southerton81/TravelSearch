package dmitriy.com.travelsearch;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.DisplayMetrics;

import com.larvalabs.svgandroid.SVG;

import java.util.regex.Pattern;

public class Utils {

    public static Pattern createPatternByMasksSeparatedBySpaces(String mask) {
        String RegExp;

        if (mask.isEmpty())
            RegExp = new String("$muchnothing");
        else {
            String[] Masks = mask.split("\\s+"); // s+ - whitespaces
            StringBuilder StringBuilder = new StringBuilder(Masks.length);
            for (int n = 0; n < Masks.length; n++) {
                String m = Masks[n];
                if (m.isEmpty()) continue;
                StringBuilder.append("(?i)\\b" + m); // i - case insensitive, b - beginning of the word
                if (n < (Masks.length - 1))
                    StringBuilder.append("|"); // | - concat masks
            }
            RegExp = StringBuilder.toString();
        }

        return Pattern.compile(RegExp);
    }

    public static Bitmap Svg2Bitmap(SVG svg, float size) {
        RectF rect = svg.getLimits();
        float w = rect.right + rect.left;
        float h = rect.bottom + rect.top;
        float scale = 1f;
        if (w > h) scale = size / w;
        else scale = size / h;
        w *= scale;
        h *= scale;
        Bitmap bitmap = Bitmap.createBitmap((int)w, (int)h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.scale(scale, scale);
        canvas.drawPicture(svg.getPicture());
        return bitmap;
    }

    public static float getButtonIconSize(Activity a) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        a.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return 48f * displayMetrics.density;
    }

}
