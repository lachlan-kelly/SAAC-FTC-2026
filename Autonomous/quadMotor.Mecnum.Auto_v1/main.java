package org.firstinspires.ftc.teamcode;

import android.graphics.Color;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

@TeleOp(name = "colorTest2", group = "Sensor")
public class colorTest2 extends LinearOpMode {

    private NormalizedColorSensor colorSensor;

    @Override
    public void runOpMode() {
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "colorS");
        float[] hsvValues = new float[3];

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            NormalizedRGBA colors = colorSensor.getNormalizedColors();
            Color.colorToHSV(colors.toColor(), hsvValues);
            float hue = hsvValues[0];
            String colorName = "Unknown";

            if (colors.alpha > 0.01) {
                if (hue >= 1 && hue < 20 || hue >= 330) {
                    colorName = "Red";
                } else if (hue >= 30 && hue < 65) {
                    colorName = "Orange";
                } else if (hue >= 65 && hue < 130) {
                    colorName = "Yellow";
                } else if (hue >= 130 && hue < 165) {
                    colorName = "Green";
                } else if (hue >= 165 && hue < 260) {
                    colorName = "Blue";
                } else if (hue >= 0 && hue < 1) {
                    colorName = "Black";
                }
            } else {
                colorName = "No object";
            }

            telemetry.addData("Color", colorName);
            telemetry.addData("Hue", hue);
            telemetry.update();

            sleep(50);
        }
    }
}