package org.firstinspires.ftc.teamcode;

import android.graphics.Color;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/*
 * Color sensor OpMode with a confidence score attached to every reading.
 *
 * The classifier (Red/Blue/Yellow/etc.) is a set of thresholds on hue,
 * saturation and value. Any threshold-based classifier has the same failure
 * mode: readings near a boundary flip unpredictably between two labels from
 * frame to frame, and there is normally no way to tell a solid reading from
 * a marginal one just by looking at the label. This version scores each
 * reading on three independent axes and combines them into one confidence
 * percentage:
 *
 * 1. Signal quality - is the sensor even getting a clean signal? Based on
 *    distance from target (too far = background bleed, too close = glare)
 *    and whether the raw R/G/B channels are clipped (pinned near 1.0, which
 *    means gain is too high and the reading is meaningless).
 *
 * 2. Classification margin - how far is this reading from the nearest
 *    decision boundary in the classifier? A hue of 16 (just inside the
 *    Orange bucket, which starts at 15) is far less trustworthy than a hue
 *    of 30 (dead center).
 *
 * 3. Stability - how much has the reading been moving frame to frame? Uses
 *    a circular variance for hue (a plain variance breaks at the 0/360
 *    wrap) and standard deviation for saturation/value.
 *
 * Sensor must be configured as "colorS".
 */
@TeleOp(name = "Team Color Sensor", group = "Sensor")
public class TeamColorSensor extends LinearOpMode {

    // ---- Gain ----
    private static final float DEFAULT_GAIN = 3.0f;
    private static final float GAIN_STEP = 0.02f;
    private static final float MIN_GAIN = 1.0f;
    private static final float MAX_GAIN = 50.0f;

    // ---- Distance gating ----
    // Readings inside [IDEAL_MIN, IDEAL_MAX] score full marks on the
    // distance component of signal quality. Outside that band, the score
    // falls off linearly, hitting zero at 0cm (too close, glare-prone) or
    // at MAX_VALID_DISTANCE_CM (too far, likely reading background).
    private static final float IDEAL_MIN_DISTANCE_CM = 0.5f;
    private static final float IDEAL_MAX_DISTANCE_CM = 2.5f;
    private static final double MAX_VALID_DISTANCE_CM = 5.0;

    // ---- Smoothing ----
    private static final int SMOOTHING_WINDOW = 5;

    // ---- Classification thresholds ----
    // Tune these against readings taken directly against your own game
    // pieces. computeMargin() below references these same constants, so a
    // change here automatically updates the confidence scoring too.
    private static final float VALUE_BLACK_THRESHOLD = 0.15f;
    private static final float SATURATION_GRAY_THRESHOLD = 0.35f;
    private static final float VALUE_WHITE_THRESHOLD = 0.70f;
    private static final float[] HUE_BOUNDARIES = {15, 45, 70, 170, 200, 260, 320, 345};

    // ---- Confidence scoring ----
    private static final float CLIP_THRESHOLD = 0.98f; // raw channel considered clipped at/above this
    private static final float VALUE_MARGIN_NORMALIZER = 0.15f;      // margin >= this = full score
    private static final float SATURATION_MARGIN_NORMALIZER = 0.15f;
    private static final float HUE_MARGIN_NORMALIZER = 10f;          // degrees
    private static final float STABILITY_NORMALIZER = 0.10f;         // stddev >= this = zero stability score

    private static final float WEIGHT_SIGNAL_QUALITY = 0.35f;
    private static final float WEIGHT_MARGIN = 0.35f;
    private static final float WEIGHT_STABILITY = 0.30f;

    private static final float CONFIDENCE_HIGH_THRESHOLD = 75f;
    private static final float CONFIDENCE_MEDIUM_THRESHOLD = 45f;
    private static final float CONFIDENCE_UNCERTAIN_THRESHOLD = 35f; // below this, the color is flagged, not asserted

    private NormalizedColorSensor colorSensor;
    private DistanceSensor distanceSensor;

    private final float[] hueSinBuffer = new float[SMOOTHING_WINDOW];
    private final float[] hueCosBuffer = new float[SMOOTHING_WINDOW];
    private final float[] satBuffer = new float[SMOOTHING_WINDOW];
    private final float[] valBuffer = new float[SMOOTHING_WINDOW];
    private final boolean[] clippedBuffer = new boolean[SMOOTHING_WINDOW];
    private int bufferIndex = 0;
    private int samplesCollected = 0;

    @Override
    public void runOpMode() {
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "colorS");
        distanceSensor = (colorSensor instanceof DistanceSensor) ? (DistanceSensor) colorSensor : null;

        float gain = DEFAULT_GAIN;
        colorSensor.setGain(gain);

        if (colorSensor instanceof SwitchableLight) {
            ((SwitchableLight) colorSensor).enableLight(true);
        }

        boolean xPrev = false;

        telemetry.addLine("Initialized. Press start.");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {

            if (gamepad1.a) {
                gain = Math.min(MAX_GAIN, gain + GAIN_STEP);
                colorSensor.setGain(gain);
            } else if (gamepad1.b) {
                gain = Math.max(MIN_GAIN, gain - GAIN_STEP);
                colorSensor.setGain(gain);
            }

            boolean xNow = gamepad1.x;
            if (xNow && !xPrev && colorSensor instanceof SwitchableLight) {
                SwitchableLight light = (SwitchableLight) colorSensor;
                light.enableLight(!light.isLightOn());
            }
            xPrev = xNow;

            NormalizedRGBA colors = colorSensor.getNormalizedColors();
            float[] hsv = new float[3];
            Color.colorToHSV(colors.toColor(), hsv);

            double distanceCm = (distanceSensor != null)
                    ? distanceSensor.getDistance(DistanceUnit.CM)
                    : -1;
            boolean inRange = (distanceSensor == null) || (distanceCm <= MAX_VALID_DISTANCE_CM);

            String colorName;
            if (!inRange) {
                colorName = "Out of range";
                samplesCollected = 0; // drop stale samples so old readings don't bleed in
            } else {
                boolean clipped = colors.red >= CLIP_THRESHOLD || colors.green >= CLIP_THRESHOLD || colors.blue >= CLIP_THRESHOLD;
                pushSample(hsv[0], hsv[1], hsv[2], clipped);

                float avgHue = averageHue();
                float avgSat = average(satBuffer);
                float avgVal = average(valBuffer);
                String rawGuess = classify(avgHue, avgSat, avgVal);

                float signalQuality = computeSignalQuality(distanceCm);
                float margin = computeMargin(avgHue, avgSat, avgVal);
                float stability = computeStability();
                float confidencePct = clamp01(
                        WEIGHT_SIGNAL_QUALITY * signalQuality
                                + WEIGHT_MARGIN * margin
                                + WEIGHT_STABILITY * stability
                ) * 100f;

                colorName = (confidencePct < CONFIDENCE_UNCERTAIN_THRESHOLD)
                        ? "Uncertain (best guess: " + rawGuess + ")"
                        : rawGuess;

                telemetry.addData("Hue / Sat / Val (avg)", "%.1f / %.3f / %.3f", avgHue, avgSat, avgVal);
                telemetry.addData("Signal quality", "%.0f%%", signalQuality * 100f);
                telemetry.addData("Classification margin", "%.0f%%", margin * 100f);
                telemetry.addData("Stability", "%.0f%%", stability * 100f);
                telemetry.addData("Confidence", "%.0f%% (%s)", confidencePct, confidenceLabel(confidencePct));
            }

            telemetry.addData("Gain", "%.2f", gain);
            telemetry.addData("Raw RGB", "%.3f, %.3f, %.3f", colors.red, colors.green, colors.blue);
            if (distanceSensor != null) {
                telemetry.addData("Distance (cm)", "%.2f", distanceCm);
            }
            telemetry.addData("Color", colorName);
            telemetry.update();
        }
    }

    private void pushSample(float hue, float sat, float val, boolean clipped) {
        double rad = Math.toRadians(hue);
        hueSinBuffer[bufferIndex] = (float) Math.sin(rad);
        hueCosBuffer[bufferIndex] = (float) Math.cos(rad);
        satBuffer[bufferIndex] = sat;
        valBuffer[bufferIndex] = val;
        clippedBuffer[bufferIndex] = clipped;
        bufferIndex = (bufferIndex + 1) % SMOOTHING_WINDOW;
        if (samplesCollected < SMOOTHING_WINDOW) samplesCollected++;
    }

    // Circular mean, needed because hue wraps at 360 and red straddles the
    // 0/360 boundary. A plain arithmetic mean would report readings near
    // pure red as an incorrect mid-range hue.
    private float averageHue() {
        int n = Math.max(samplesCollected, 1);
        float sumSin = 0, sumCos = 0;
        for (int i = 0; i < n; i++) {
            sumSin += hueSinBuffer[i];
            sumCos += hueCosBuffer[i];
        }
        double angle = Math.toDegrees(Math.atan2(sumSin, sumCos));
        if (angle < 0) angle += 360;
        return (float) angle;
    }

    private float average(float[] buffer) {
        float sum = 0;
        int n = Math.max(samplesCollected, 1);
        for (int i = 0; i < n; i++) sum += buffer[i];
        return sum / n;
    }

    private float stdDev(float[] buffer) {
        int n = Math.max(samplesCollected, 1);
        float mean = average(buffer);
        float sumSq = 0;
        for (int i = 0; i < n; i++) {
            float d = buffer[i] - mean;
            sumSq += d * d;
        }
        return (float) Math.sqrt(sumSq / n);
    }

    // --- Confidence components ---

    private float computeSignalQuality(double distanceCm) {
        return (distanceScore((float) distanceCm) + clippingScore()) / 2f;
    }

    private float distanceScore(float distanceCm) {
        if (distanceCm < 0) return 1f; // no distance sensor available; don't penalize
        if (distanceCm >= IDEAL_MIN_DISTANCE_CM && distanceCm <= IDEAL_MAX_DISTANCE_CM) return 1f;
        if (distanceCm < IDEAL_MIN_DISTANCE_CM) {
            return clamp01(distanceCm / IDEAL_MIN_DISTANCE_CM);
        }
        return clamp01(1f - (distanceCm - IDEAL_MAX_DISTANCE_CM) / ((float) MAX_VALID_DISTANCE_CM - IDEAL_MAX_DISTANCE_CM));
    }

    private float clippingScore() {
        int n = Math.max(samplesCollected, 1);
        int clippedCount = 0;
        for (int i = 0; i < n; i++) {
            if (clippedBuffer[i]) clippedCount++;
        }
        return 1f - ((float) clippedCount / n);
    }

    // How far the current average reading sits from the nearest decision
    // boundary in classify(), normalized to 0-1. Mirrors classify()'s own
    // branching so the margin reflects the boundary that actually matters
    // for this reading, not an unrelated one.
    private float computeMargin(float hue, float sat, float val) {
        if (val < VALUE_BLACK_THRESHOLD) {
            return clamp01((VALUE_BLACK_THRESHOLD - val) / VALUE_MARGIN_NORMALIZER);
        }
        if (sat < SATURATION_GRAY_THRESHOLD) {
            float satMargin = clamp01((SATURATION_GRAY_THRESHOLD - sat) / SATURATION_MARGIN_NORMALIZER);
            float whiteMargin = clamp01(Math.abs(val - VALUE_WHITE_THRESHOLD) / VALUE_MARGIN_NORMALIZER);
            return Math.min(satMargin, whiteMargin);
        }
        float chromaMargin = clamp01((sat - SATURATION_GRAY_THRESHOLD) / SATURATION_MARGIN_NORMALIZER);
        float darkMargin = clamp01((val - VALUE_BLACK_THRESHOLD) / VALUE_MARGIN_NORMALIZER);
        float hueMargin = clamp01(hueBoundaryMargin(hue) / HUE_MARGIN_NORMALIZER);
        return Math.min(chromaMargin, Math.min(darkMargin, hueMargin));
    }

    private float hueBoundaryMargin(float hue) {
        float minDist = 180f;
        for (float boundary : HUE_BOUNDARIES) {
            float diff = Math.abs(hue - boundary);
            float circDist = Math.min(diff, 360f - diff);
            if (circDist < minDist) minDist = circDist;
        }
        return minDist;
    }

    // How much the reading has been moving frame to frame. Scaled down
    // while the smoothing buffer is still filling, since a handful of
    // samples cannot establish stability either way.
    private float computeStability() {
        if (samplesCollected == 0) return 0f;
        int n = samplesCollected;

        float sumSin = 0, sumCos = 0;
        for (int i = 0; i < n; i++) {
            sumSin += hueSinBuffer[i];
            sumCos += hueCosBuffer[i];
        }
        float hueStability = clamp01((float) Math.sqrt(sumSin * sumSin + sumCos * sumCos) / n);

        float satStability = 1f - clamp01(stdDev(satBuffer) / STABILITY_NORMALIZER);
        float valStability = 1f - clamp01(stdDev(valBuffer) / STABILITY_NORMALIZER);

        float fillRatio = (float) samplesCollected / SMOOTHING_WINDOW;
        return ((hueStability + satStability + valStability) / 3f) * fillRatio;
    }

    private String confidenceLabel(float pct) {
        if (pct >= CONFIDENCE_HIGH_THRESHOLD) return "High";
        if (pct >= CONFIDENCE_MEDIUM_THRESHOLD) return "Medium";
        return "Low";
    }

    private float clamp01(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    // --- Classification ---

    private String classify(float hue, float saturation, float value) {
        if (value < VALUE_BLACK_THRESHOLD) {
            return "Black";
        }
        if (saturation < SATURATION_GRAY_THRESHOLD) {
            return (value > VALUE_WHITE_THRESHOLD) ? "White" : "Gray";
        }
        if (hue < 15 || hue >= 345) {
            return "Red";
        } else if (hue < 45) {
            return "Orange";
        } else if (hue < 70) {
            return "Yellow";
        } else if (hue < 170) {
            return "Green";
        } else if (hue < 200) {
            return "Cyan";
        } else if (hue < 260) {
            return "Blue";
        } else if (hue < 320) {
            return "Purple";
        } else {
            return "Pink";
        }
    }
}